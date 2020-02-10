package model.api.protocol.response

case class AfterAttackResponse(salvo: Map[String, String],
                              game: GameStatus)

case class GameStatus(playerTurn: Option[String],
                      won: Option[String])

/*{
  "salvo": {
  "0x0": "hit",
  "8x4": "hit",
  "DxA": "kill",
  "AxA": "miss",
  "7xF": "miss"
},
  "game": {
  "player_turn": "player-1"
}
}*/
