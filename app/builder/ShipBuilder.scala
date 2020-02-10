package builder

import model._

import scala.util.Random

class ShipBuilder {

  val BOARD_SIZE = 16

  def randomX: Int = Random.nextInt(BOARD_SIZE)

  def randomY: Int = Random.nextInt(BOARD_SIZE)

  def randomOrientation: String = Orientation(Random.nextInt(Orientation.maxId))

  def build(tpe: String): Ship = {

    val orientation = pickRandomOrientationFunction

    tpe match {
      case "Winger" => Winger.build(randomX, randomY, orientation)
      case "Angle" => Angle.build(randomX, randomY, orientation)
      case "AClass" => AClass.build(randomX, randomY, orientation)
      case "BClass" => BClass.build(randomX, randomY, orientation)
      case "SClass" => SClass.build(randomX, randomY, orientation)
    }

  }

  def pickRandomOrientationFunction: Option[OrientationFunction] = randomOrientation match {
    case "Rotate90" => Option(convertPointTo90)
    case "Rotate180" => Option(convertPointTo180)
    case "Rotate270" => Option(convertPointTo270)
    case _ => None

  }

}

object Orientation extends Enumeration {
  type Orientation = Value
  val Regular = Value("Regular")
  val Rotate90 = Value("Rotate90")
  val Rotate180 = Value("Rotate180")
  val Rotate270 = Value("Rotate270")


  implicit def enum2String(value: Orientation): String = value.toString
}

object ShipType extends Enumeration {
  type ShipType = Value
  val Winger = Value("Winger")
  val Angle = Value("Angle")
  val AClass = Value("AClass")
  val BClass = Value("BClass")
  val SClass = Value("SClass")


  implicit def enum2String(value: ShipType): String = value.toString
}
