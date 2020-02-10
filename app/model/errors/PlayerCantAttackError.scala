package model.errors

case class PlayerCantAttackError(code: Int = 401, playerId: String) extends Throwable
