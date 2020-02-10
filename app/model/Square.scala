package model

case class Square(x: Int, y: Int, shipClass: Option[String], attacked: Boolean) {

  def isEmpty = shipClass.isEmpty

  def drawValue = (shipClass, attacked) match {
    case (Some(_), true) => "X"
    case (Some(_), false) => "*"
    case (None, true) => "-"
    case _ => "."
  }
}

object Square {
  def initialize = Square(0, 0, None, attacked = false)
}


/*
def drawValue(knownResult: Option[String] = None) = (knownResult, shipClass, attacked) match {
  case (Some("Miss"), _, _) => "-"
  case (Some(_), _, _) => "X"
  case (_, Some(_), true) => "X"
  case (_, Some(_), _) => "*"
  case (_, None, true) => "-"
  case _ => "."
}*/
