package model

import builder.ShipBuilder

case class Board(gameId: String) {

  var boardPositions: Array[Array[Square]] = Array.fill[Square](16, 16)(Square.initialize)

  val shipBuilder = new ShipBuilder()

  val shipClasses: List[String] = List("Winger", "Angle", "AClass", "BClass", "SClass")

  var ships: List[Ship] = Nil

  def endOfGame = ships.forall(_.isDead)

  def getSquareStatus(x: Int, y: Int): String = {
    boardPositions(x)(y).shipClass match {
      case Some(shipClassInSpot) => getSquareStatusByShipStatus(shipClassInSpot)
      case _ => "Miss"
    }
  }

  def getSquareStatusByShipStatus(shipClass: String) = {
    getShipByClass(shipClass) match {
      case Some(ship) => if (ship.isDead) "KIll" else "Hit"
      case _ => "Miss"
    }
  }

  def getShipByClass(shipClass: String) = ships.find(_.tpe.equals(shipClass))

  def drawBoard = boardPositions.map { row => row.map(_.drawValue) }

  def placeAllShips: Unit = {
    shipClasses.foreach(shipClass => placeShip(shipBuilder.build(shipClass)))
  }

  def placeShip(ship: Ship): Unit = {

    if (ship.structure.forall { case Square(x, y, _, _) => pointsWithinBounds(x,y) && boardPositions(x)(y).isEmpty }) {
      ship.structure.foreach{ case Square(x, y, tpe, hit) => fillPosition(x, y, tpe, hit) }
      ships = ship :: ships
    } else {
      placeShip(shipBuilder.build(ship.tpe))
    }
  }

  def pointsWithinBounds(x: Int, y: Int): Boolean = x >= 0 && x <= 15 && y >=0 && y <= 15

  def receiveAttackInPosition(x: Int, y: Int): Square = fillPositionUnderAttack(x, y)

  def fillPositionUnderAttack(x: Int, y: Int) = {
    val square = boardPositions(x)(y)

    if (shipInPosition(x, y)) {
      hitPosition(x, y, square.shipClass)
    } else {
      missPosition(x, y, square.shipClass)
    }

  }

  def fillAttackedPosition(x: Int, y: Int, res: String) = {
    val square = boardPositions(x)(y)

    if (!res.equals("Miss")) {
      hitPosition(x, y, Option("UnkownEnemyShip"))
    } else {
      missPosition(x, y, None)
    }

  }

  def fillPosition(x: Int, y: Int, ship: Option[String], hit: Boolean) = {
    boardPositions(x)(y) = Square(x, y, ship, hit)
    boardPositions(x)(y)
  }

  def hitPosition(x: Int, y: Int, ship: Option[String]) = fillPosition(x, y, ship, hit = true)

  def missPosition(x: Int, y: Int, ship: Option[String]) = fillPosition(x, y, ship, hit = true)

  def shipInPosition(x: Int, y: Int) = boardPositions(x)(y).shipClass.isDefined

}

object Board {

  def create(gameId: String) = {
    val board = Board(gameId)
    board.placeAllShips
    board
  }

  //For enemy usage
  def createEmpty(gameId: String) = { Board(gameId) }

}
