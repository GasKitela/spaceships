package model.api.protocol.response

case class GameStatusResponse(self: PlayerStatusResponse,
                              opponent: PlayerStatusResponse,
                              game: GameStatus)

case class PlayerStatusResponse(userId: String, rows: List[String])
