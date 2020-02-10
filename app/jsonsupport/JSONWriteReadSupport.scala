package jsonsupport

import model.Salvo
import model.api.protocol.request._
import model.api.protocol.response._
import model.api.user._
import model.errors._
import play.api.libs.json.JsonNaming.SnakeCase
import play.api.libs.json.{Json, JsonConfiguration}

trait JSONWriteReadSupport {

  implicit val config = JsonConfiguration(SnakeCase)

  implicit val spaceshipFormat = Json.format[SpaceshipProtocol]

  implicit val receiveNewSimulationRequestFormat = Json.format[ReceiveNewSimulationRequest]

  implicit val sendNewSimulationRequestFormat = Json.format[SendNewSimulationRequest]

  implicit val gameStatusFormat = Json.format[GameStatus]

  implicit val newSimulationResponseFormat = Json.format[NewSimulationResponse]

  implicit val instanceUserRequestFormat = Json.format[InstanceUserRequest]

  implicit val playerStatusResponseFormat = Json.format[PlayerStatusResponse]

  implicit val gameStatusResponseFormat = Json.format[GameStatusResponse]

  implicit val receiveSalvoRequestFormat = Json.format[SalvoRequest]

  implicit val afterAttackResponseFormat = Json.format[AfterAttackResponse]

  implicit val salvoFormat = Json.format[Salvo]

  implicit val httpErrorFormat = Json.format[HttpClientError]

  implicit val boardNotFoundErrorFormat = Json.format[BoardNotFoundForGameError]

  implicit val gameNotFoundErrorFormat = Json.format[GameNotFoundError]

  implicit val playerCantAttackErrorFormat = Json.format[PlayerCantAttackError]

  implicit val endgameSalvoAttemptErrorFormat = Json.format[EndgameSalvoAttemptError]

  implicit val instancePlayerNotDefinedErrorFormat = Json.format[InstancePlayerNotDefinedError]

}
