package model.errors

case class UserAlreadyCreatedError(code: Int = 401) extends Throwable
