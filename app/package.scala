
package object model {
  type OrientationFunction = (Int, Int, (Int, Int)) => (Int, Int)

  //Points utilities

  def convertPointTo90(axisX: Int, axisY: Int, pointToConvert: (Int, Int)) = {
    val (xToConvert, yToConvert) = pointToConvert

    val convertedX =  axisX - (axisY - yToConvert)
    val convertedY = axisY + axisX - xToConvert

    (convertedX, convertedY)
  }

  def convertPointTo180(axisX: Int, axisY: Int, pointToConvert: (Int, Int)) = {
    convertPointTo90(axisX, axisY, convertPointTo90(axisX, axisY, pointToConvert))
  }

  def convertPointTo270(axisX: Int, axisY: Int, pointToConvert: (Int, Int)) = {
    val (xToConvert, yToConvert) = pointToConvert

    val convertedX =  axisX + axisY - yToConvert
    val convertedY = axisY - (axisX - xToConvert)

    (convertedX, convertedY)
  }

  def rotatePoints(list: List[(Int, Int)], rotation: (Int, Int, (Int, Int)) => (Int, Int)) = {
    list match {
      case _ :: Nil => list
      case (fstX, fstY) :: tail => (fstX, fstY) :: tail.map(p => rotation(fstX, fstY, p))
      case _ => list
    }
  }

  def structurePointToSquare(shipType: Option[String], x: Int, y: Int): Square = Square(x, y, shipType, attacked = false)

  //Salvo utilities

  def salvoAmmoToPosition(salvoAmmo: String) = {
    val salvoSplitted = salvoAmmo.split("x|X", 2)
    salvoSplitted.toList match {
      case x :: y :: Nil => (Integer.parseInt(x, 16), Integer.parseInt(y, 16))
      case _ => (0, 0)
    }

  }

}

