package controllers

import javax.inject._
import jsonsupport.JSONWriteReadSupport
import model.api.protocol.request.{ReceiveNewSimulationRequest, SalvoRequest}
import play.api.libs.json.Json
import play.api.mvc._
import services.SimulationService

@Singleton
class SimulationController @Inject()(cc: ControllerComponents,
                                     simulationService: SimulationService) extends AbstractController(cc) with JSONWriteReadSupport {

  def receiveNewGameRequest = Action(parse.json[ReceiveNewSimulationRequest]) { implicit req =>
    val response = simulationService.receiveNewSimulationRequest(req.body)
    println(s"New game created. id: ${response.gameId}. First to shoot: ${response.starting}.")
    Ok(Json.toJson(response))
  }

  def receiveSalvo(gameId: String) = Action(parse.json[SalvoRequest]) { implicit req =>
    val response = simulationService.receiveAttack(gameId, req.body)
    response match {
      case Right(r) =>
        println(s"$gameId --> salvo received: ${req.body.salvo}")
        Ok(Json.toJson(r))
      case Left(r) => BadRequest(Json.toJson(r))
  }
  }

}
