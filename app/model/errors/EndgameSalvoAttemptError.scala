package model.errors

import model.api.protocol.response.GameStatus

case class EndgameSalvoAttemptError(salvo: Map[String, String], gameStatus: GameStatus) extends Throwable
