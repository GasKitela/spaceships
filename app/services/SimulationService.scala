package services

import client.SimulationClient
import javax.inject.{Inject, Singleton}
import model._
import model.api.protocol.request.{ReceiveNewSimulationRequest, SalvoRequest}
import model.api.protocol.response._
import model.errors.BoardNotFoundForGameError
import repository.Store

import scala.util.Random

@Singleton
class SimulationService @Inject()(simulationClient: SimulationClient) {

  def receiveNewSimulationRequest(simulationRequest: ReceiveNewSimulationRequest) = {

    val randomGameId = "gameId-" + Random.nextInt(100000)

    val instancePlayer = Store.instancePlayer

    instancePlayer.newGame(randomGameId)
    instancePlayer.update

    val enemy = Player(simulationRequest.userId,
                       simulationRequest.fullName,
                       simulationRequest.spaceshipProtocol.hostname,
                       simulationRequest.spaceshipProtocol.port)

    enemy.newGameAsEnemy(randomGameId)

    val firstTurn = pickFirstTurn(instancePlayer.userId, enemy.userId)

    val newGame = Game(instancePlayer, enemy, Option(firstTurn))

    Store.games = Store.games + (randomGameId -> newGame)

    NewSimulationResponse(instancePlayer.userId, instancePlayer.fullName, randomGameId, firstTurn)

  }

  def receiveAttack(gameId: String, receiveSalvoRequest: SalvoRequest): Either[BoardNotFoundForGameError, AfterAttackResponse] = for {

    attackedBoard <- Store.instancePlayer.receiveSalvoAttack(gameId, receiveSalvoRequest.salvo)

    response = buildReceiveAttackResponse(gameId, attackedBoard, receiveSalvoRequest.salvo)

  } yield response

  private def pickFirstTurn(userId: String, enemyUserId: String) = {
    val userIds = List(userId, enemyUserId)

    val idOfTurnPlayer = Random.nextInt(userIds.length)

    userIds(idOfTurnPlayer)
  }

  private def buildReceiveAttackResponse(gameId: String, board: Board, salvo: List[String]): AfterAttackResponse = {

    val game = Store.games(gameId)

    val attackedPositions = salvo.map(salvoAmmoToPosition)

    val salvoWithResults = attackedPositions.map { case (x, y) => s"${x.toHexString}X${y.toHexString}" -> board.getSquareStatus(x, y) }.toMap

    val winner = if (board.endOfGame) Option(game.enemy.userId) else None

    val turn = if (board.endOfGame) None else Option(game.player.userId)

    val updatedGame = game.copy(winner = winner, turn = turn)

    Store.games = Store.games + (gameId -> updatedGame)

    AfterAttackResponse(salvoWithResults,
                        GameStatus(playerTurn = turn,
                                   won = winner))

  }

}
