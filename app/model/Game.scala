package model

case class Game(player: Player,
                enemy: Player,
                turn: Option[String],
                winner: Option[String] = None)
