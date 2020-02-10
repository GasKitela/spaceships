package model.errors

case class GameNotFoundError(code: Int = 400, gameId: String) extends Throwable
