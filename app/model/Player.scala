package model

import model.errors.BoardNotFoundForGameError
import repository.Store


case class Player(userId: String,
                  fullName: String,
                  host: String,
                  port: Int) {

  var boards: List[Board] = Nil

  def newGame(gameId: String): Board = {
    val newBoard = Board.create(gameId)
    boards = newBoard :: boards
    newBoard
  }

  def newGameAsEnemy(gameId: String) = {
    val newBoard = Board.createEmpty(gameId)
    boards = newBoard :: boards
    newBoard
  }

  def update = Store.instancePlayer = this

  def recordSalvoAttackInEnemyBoard(gameId: String, salvo: List[(String, String)]) = {

    val currentGame = Store.games(gameId)

    for {

      enemyBoard <- currentGame.enemy.boards.find(_.gameId.equals(gameId))

      attackedPositionsWithResult = salvo.map { case (pos, res) => (salvoAmmoToPosition(pos), res) }

      _ = attackedPositionsWithResult.foreach { case ((x, y), res) => enemyBoard.fillAttackedPosition(x, y, res)}

    } yield enemyBoard

  }

  def receiveSalvoAttack(gameId: String, salvo: List[String]) = {

    val affectedBoard = for {

      board <- boards.find(_.gameId.equals(gameId))

      attackedPositions = salvo.map(salvoAmmoToPosition)

      _ = attackedPositions.foreach{ case (x, y) => board.receiveAttackInPosition(x, y) }

      newShips = board.ships.map(_.receiveAttack(attackedPositions))

      _ = board.ships = newShips

    } yield board

    affectedBoard match {
      case Some(board) => Right(board)
      case _ => Left(BoardNotFoundForGameError(gameId = gameId))
    }
  }

}
