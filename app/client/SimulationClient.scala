package client

import javax.inject.{Inject, Singleton}
import model.api.protocol.response.{AfterAttackResponse, NewSimulationResponse}
import model.errors.HttpClientError
import play.api.libs.ws._
import play.api.libs.json.Json
import org.json4s._
import org.json4s.native.JsonMethods._
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}
import scala.util.{Failure, Success, Try}
import com.softwaremill.sttp._
import com.softwaremill.sttp.akkahttp._
import jsonsupport.JSONWriteReadSupport
import model.api.protocol.request.{ReceiveNewSimulationRequest, SalvoRequest}
import org.json4s.DefaultFormats
import org.json4s.native.Serialization


@Singleton
class SimulationClient @Inject()(ws: WSClient)(implicit ec: ExecutionContext) extends JSONWriteReadSupport {

  implicit val backend = AkkaHttpBackend()
  implicit val formats = DefaultFormats
  implicit val serialization = Serialization

  def sendNewSimulationRequest(newSimulationRequest: ReceiveNewSimulationRequest, hostname: String, port: Int): Either[HttpClientError, NewSimulationResponse] = {

    val request = sttp
      .contentType("application/json")
      .body(Json.toJson(newSimulationRequest).toString)
      .post(uri"http://$hostname:$port/xl-spaceship/protocol/game/new")
      .response(asString)
      .send()

    Try(Await.result(request, Duration(20, "seconds"))) match {
      case Success(response) => response.body match {
        case Left(r) => Left(HttpClientError(response.code, Option(r)))
        case Right(body) => {
          val jsBody = parse(body).camelizeKeys
          Right(jsBody.extract[NewSimulationResponse])
        }
      }
      case Failure(r) => Left(HttpClientError(500, Option(r.toString)))
    }
  }

  def sendAttack(gameId: String, salvo: SalvoRequest, hostname: String, port: Int): Either[HttpClientError, AfterAttackResponse] = {
    val request = sttp
      .contentType("application/json")
      .body(Json.toJson(salvo).toString)
      .put(uri"http://$hostname:$port/xl-spaceship/protocol/game/$gameId")
      .response(asString)
      .send()

    Try(Await.result(request, Duration(200, "seconds"))) match {
      case Success(response) => response.body match {
        case Left(_) => Left(HttpClientError(response.code))
        case Right(body) => {
          val jsBody = parse(body).camelizeKeys
          Right(jsBody.extract[AfterAttackResponse])
        }
      }
      case Failure(r) => Left(HttpClientError(500, Option(r.toString)))
    }

  }

}
