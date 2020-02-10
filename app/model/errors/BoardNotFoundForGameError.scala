package model.errors

case class BoardNotFoundForGameError(code: Int = 400,  gameId: String) extends Throwable
