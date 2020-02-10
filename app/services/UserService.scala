package services

import client.SimulationClient
import javax.inject.{Inject, Singleton}
import model.api.protocol.request.{ReceiveNewSimulationRequest, SalvoRequest, SpaceshipProtocol}
import model.api.protocol.response.{AfterAttackResponse, GameStatus, GameStatusResponse, NewSimulationResponse, PlayerStatusResponse}
import model.{Game, Player}
import model.errors._
import model.api.user.{InstanceUserRequest, SendNewSimulationRequest}
import repository.Store

@Singleton
class UserService @Inject()(simulationService: SimulationService, simulationClient: SimulationClient) {

  def createInstanceUser(newInstanceUserRequest: InstanceUserRequest): Either[UserAlreadyCreatedError, Unit] = {
    if (Store.instancePlayer == null) {

      Store.instancePlayer = Player(newInstanceUserRequest.userId,
                                    newInstanceUserRequest.fullName,
                                    newInstanceUserRequest.host,
                                    newInstanceUserRequest.port)
      Right()
    } else {
      Left(UserAlreadyCreatedError())
    }
  }

  def sendNewSimulationRequest(sendNewSimulationRequest: SendNewSimulationRequest): Either[Throwable, NewSimulationResponse] = {

    val player = Store.instancePlayer

    if (player != null) {

      val receiveNewSimulationRequestToSend = ReceiveNewSimulationRequest(player.userId,
                                                                          player.fullName,
                                                                          SpaceshipProtocol(player.host, player.port))

      val response = simulationClient.sendNewSimulationRequest(receiveNewSimulationRequestToSend,
                                                               sendNewSimulationRequest.spaceshipProtocol.hostname,
                                                               sendNewSimulationRequest.spaceshipProtocol.port)

      response match {
        case Right(res) => {
          val instancePlayer = Store.instancePlayer

          instancePlayer.newGame(res.gameId)
          instancePlayer.update

          val enemy = Player(res.userId,
                             res.fullName,
                             sendNewSimulationRequest.spaceshipProtocol.hostname,
                             sendNewSimulationRequest.spaceshipProtocol.port)

          enemy.newGameAsEnemy(res.gameId)

          val firstTurn = res.starting

          val newGame = Game(instancePlayer, enemy, Option(firstTurn))

          Store.games = Store.games + (res.gameId -> newGame)

          Right(res)
        }
        case Left(err) => Left(err)
      }

    } else Left(InstancePlayerNotDefinedError())

  }

  def getGameStatus(gameId: String): Either[Throwable, GameStatusResponse] = {

    val gameStatusResponse = for {

      game <- Store.games.get(gameId)

      playerUserId = game.player.userId

      enemyUserId = game.enemy.userId

      playerBoard <- game.player.boards.find(_.gameId.equals(gameId))
      playerBoardAsStringList = playerBoard.drawBoard.map(rows => rows.mkString(" ")).toList

      playerStatus = PlayerStatusResponse(playerUserId, playerBoardAsStringList)

      enemyBoard <- game.enemy.boards.find(_.gameId.equals(gameId))
      enemyBoardAsStringList = enemyBoard.drawBoard.map(rows => rows.mkString(" ")).toList

      enemyStatus = PlayerStatusResponse(enemyUserId, enemyBoardAsStringList)

    } yield GameStatusResponse(playerStatus, enemyStatus, GameStatus(won = game.winner, playerTurn = game.turn))

    gameStatusResponse match {
      case Some(gsr) => Right(gsr)
      case None => if (Store.games.get(gameId).isDefined) {
        Left(BoardNotFoundForGameError(gameId = gameId))
      } else {
        Left(GameNotFoundError(400, gameId))
      }
    }

  }

  def sendAttack(gameId: String, sendSalvoRequest: SalvoRequest): Either[Throwable, AfterAttackResponse] = Store.games.get(gameId) match {

    case Some(currentGame) if currentGame.winner.isDefined => Left(buildAfterEndgameErrorResponse(sendSalvoRequest, currentGame))

    case Some(currentGame) => {

      if (playerCanAttack(gameId, currentGame, sendSalvoRequest)) {

        val response = for {
          response <- simulationClient.sendAttack(gameId, sendSalvoRequest, currentGame.enemy.host, currentGame.enemy.port)
        } yield response

        response match {
          case Right(r) => {
            currentGame.player.recordSalvoAttackInEnemyBoard(gameId, r.salvo.toList)
            val updatedCurrentGame = currentGame.copy(winner = r.game.won, turn = r.game.playerTurn)
            Store.games = Store.games + (gameId -> updatedCurrentGame)
            Right(r)
          }
          case Left(err) => Left(err)
        }
      } else Left(PlayerCantAttackError(playerId = Store.instancePlayer.userId))

    }

    case None => Left(GameNotFoundError(gameId = gameId))

  }

  private def playerCanAttack(gameId: String, game: Game, salvoRequest: SalvoRequest): Boolean = {

    val attackingPlayer = Store.instancePlayer

    val attackingPlayerBoards = attackingPlayer.boards.find(_.gameId.equals(gameId))

    val attackingPlayerAliveShips = attackingPlayerBoards.map(_.ships.filter(_.alive))

    (attackingPlayerAliveShips, game.turn) match {
      case (Some(aliveShips), Some(turn)) => aliveShips.size >= salvoRequest.salvo.size && turn.equals(attackingPlayer.userId)
      case _ => false
    }
  }

  private def buildAfterEndgameErrorResponse(salvoRequest: SalvoRequest, game: Game): EndgameSalvoAttemptError = {
    EndgameSalvoAttemptError(salvoRequest.salvo.map(shot => shot -> "Miss").toMap, GameStatus(game.turn, game.winner))
  }

}

