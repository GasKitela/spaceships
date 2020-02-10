package controllers

import javax.inject._
import jsonsupport.JSONWriteReadSupport
import model.api.protocol.request.SalvoRequest
import model.api.user.{InstanceUserRequest, SendNewSimulationRequest}
import model.errors.{BoardNotFoundForGameError, EndgameSalvoAttemptError, GameNotFoundError, HttpClientError, InstancePlayerNotDefinedError, PlayerCantAttackError}
import play.api.libs.json.Json
import play.api.mvc._
import services.UserService

@Singleton
class UserController @Inject()(cc: ControllerComponents,
                               userService: UserService) extends AbstractController(cc) with JSONWriteReadSupport {

  def sendNewGameRequest = Action(parse.json[SendNewSimulationRequest]) { implicit req =>
    val response = userService.sendNewSimulationRequest(req.body)
    response match {
      case Right(r) => {
        println(s"A new game has been created at xl-spaceship/user/game/${r.gameId}. First to shoot: ${r.starting}.")
        val url = s"http://${req.body.spaceshipProtocol.hostname}:${req.body.spaceshipProtocol.port}/xl-spaceship/user/game/${r.gameId}"
        SeeOther(url)
      }
      case Left(r: HttpClientError) => InternalServerError(Json.toJson(r))
      case Left(r: InstancePlayerNotDefinedError) => BadRequest(Json.toJson(r))
      case r => BadRequest(r.toString)
    }
  }

  def gameStatus(gameId: String) = Action { implicit req =>
    val response = userService.getGameStatus(gameId)
    response match {
      case Right(r) => Ok(Json.toJson(r))
      case Left(r: GameNotFoundError) => BadRequest(Json.toJson(r))
      case Left(r: BoardNotFoundForGameError) => BadRequest(Json.toJson(r))
      case r => BadRequest(r.toString)
    }
  }

  def sendSalvo(gameId: String) = Action(parse.json[SalvoRequest]) { implicit req =>
    val response = userService.sendAttack(gameId, req.body)
    response match {
      case Right(r) =>
        println(s"$gameId --> salvo shot: ${req.body.salvo}")
        Ok(Json.toJson(r))
      case Left(r: HttpClientError) => InternalServerError(Json.toJson(r))
      case Left(r: GameNotFoundError) => BadRequest(Json.toJson(r))
      case Left(r: PlayerCantAttackError) => Unauthorized(Json.toJson(r))
      case Left(r: EndgameSalvoAttemptError) => NotFound(Json.toJson(r))
      case r => BadRequest(r.toString)
    }
  }

  def createNewUser = Action(parse.json[InstanceUserRequest]) { implicit req =>
    val response = userService.createInstanceUser(req.body)
    response match {
      case Right(_) =>
        println(s"User ${req.body.userId} created successfully!")
        Ok("User created successfully!")
      case Left(_) => BadRequest("ERROR: A player was already created in this instance!")
    }
  }


}
