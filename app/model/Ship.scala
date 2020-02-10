package model


case class Ship(tpe: String, structure: List[Square], alive: Boolean = true) {

  def structurePointsToEmptySquares(points: List[(Int, Int)]) = points.map{ case (x, y) => pointToEmptySquare(x, y) }

  def pointToEmptySquare(x: Int, y: Int) = {
    Square(x, y, Option(tpe), attacked = false)
  }

  def fillSquare(square: Square) = {
    square.copy(attacked = true)
  }

  def isDead = structure.forall(_.attacked)

  def receiveAttack(salvo: List[(Int, Int)]) = {
    val affectedStructure = structure.map {
      case Square(x, y, shipType, _) if salvo.contains((x, y)) => Square(x, y, shipType, attacked = true)
      case square => square
    }

    Ship(tpe, affectedStructure, !affectedStructure.forall{_.attacked})

  }
}

object Winger {

  def structure(x: Int, y: Int) = List((x, y), (x, y + 2),
                                       (x + 1, y), (x + 1, y + 2),
                                       (x + 2, y + 1),
                                       (x + 3, y), (x + 3, y + 2),
                                       (x + 4, y), (x + 4, y + 2))

  def build(randomX: Int, randomY: Int, orientationFunction: Option[OrientationFunction]): Ship = orientationFunction match {
    case Some(f) => Ship("Winger", rotatePoints(structure(randomX, randomY), f).map { case (x, y) => structurePointToSquare(Option("Winger"), x, y) })
    case _ => Ship("Winger", structure(randomX, randomY).map { case (x, y) => structurePointToSquare(Option("Winger"), x, y) })
  }

}

object Angle {

  def structure(x: Int, y: Int)  = List((x, y), (x + 1, y),
                                        (x + 2, y), (x + 3, y),
                                        (x + 3, y + 1),
                                        (x + 3, y + 2))

  def build(randomX: Int, randomY: Int, orientationFunction: Option[OrientationFunction]): Ship = orientationFunction match {
    case Some(f) => Ship("Angle", rotatePoints(structure(randomX, randomY), f).map { case (x, y) => structurePointToSquare(Option("Angle"), x, y) })
    case _ => Ship("Angle", structure(randomX, randomY).map { case (x, y) => structurePointToSquare(Option("Angle"), x, y) })
  }

}

object AClass {

  def structure(x: Int, y: Int) = List((x, y),
                                       (x + 1, y - 1), (x + 1, y + 1),
                                       (x + 2, y - 1), (x + 2, y), (x + 2, y + 1),
                                       (x + 3, y - 1), (x + 3, y + 1))


  def build(randomX: Int, randomY: Int, orientationFunction: Option[OrientationFunction]): Ship = orientationFunction match {
    case Some(f) => Ship("AClass", rotatePoints(structure(randomX, randomY), f).map { case (x, y) => structurePointToSquare(Option("AClass"), x, y) })
    case _ => Ship("AClass", structure(randomX, randomY).map { case (x, y) => structurePointToSquare(Option("AClass"), x, y) })
  }

}

object BClass {

  def structure(x: Int, y: Int) = List((x, y), (x, y + 1),
                                       (x + 1, y), (x + 1, y + 2),
                                       (x + 2, y), (x + 2, y + 1),
                                       (x + 3, y), (x + 3, y + 2),
                                       (x + 4, y), (x + 4, y + 1))

  def build(randomX: Int, randomY: Int, orientationFunction: Option[OrientationFunction]): Ship = orientationFunction match {
    case Some(f) => Ship("BClass", rotatePoints(structure(randomX, randomY), f).map { case (x, y) => structurePointToSquare(Option("BClass"), x, y) })
    case _ => Ship("BClass", structure(randomX, randomY).map { case (x, y) => structurePointToSquare(Option("BClass"), x, y) })
  }

}

object SClass {

  def structure(x: Int, y: Int) = List((x, y), (x, y + 1),
                                       (x + 1, y - 1),
                                       (x + 2, y), (x + 2, y + 1),
                                       (x + 3, y + 2),
                                       (x + 4, y), (x + 4, y + 1))

  def build(randomX: Int, randomY: Int, orientationFunction: Option[OrientationFunction]): Ship = orientationFunction match {
    case Some(f) => Ship("SClass", rotatePoints(structure(randomX, randomY), f).map { case (x, y) => structurePointToSquare(Option("SClass"), x, y) })
    case _ => Ship("SClass", structure(randomX, randomY).map { case (x, y) => structurePointToSquare(Option("SClass"), x, y) })
  }

}
