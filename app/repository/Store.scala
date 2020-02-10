package repository

import model.{Game, Player}

object Store {

  var instancePlayer: Player = null

  var games: Map[String, Game] = Map.empty

}
