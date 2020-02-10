package services

import client.SimulationClient
import model.api.protocol.request.{ReceiveNewSimulationRequest, SalvoRequest, SpaceshipProtocol}
import model.api.protocol.response.NewSimulationResponse
import model.api.user.{InstanceUserRequest, SendNewSimulationRequest}
import model.errors.HttpClientError
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, Matchers}
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._


trait UserServiceTestSupport extends Matchers with MockitoSugar {

  val simulationServiceMock = mock[SimulationService]
  val simulationClientMock = mock[SimulationClient]

  val instanceUserRequest = InstanceUserRequest("gki10", "Gaston Kitela", "localhost", 9000)
  val anotherInstanceUserRequest = InstanceUserRequest("intruder", "Intruder User", "localhost", 9000)

  val newSimulationRequest = SendNewSimulationRequest(SpaceshipProtocol("localhost", 8080))
  val anotherSimulationRequest = SendNewSimulationRequest(SpaceshipProtocol("localhost", 8081))
  val newSimulationRequestUnexistentHost = SendNewSimulationRequest(SpaceshipProtocol("unexistent.localhost", 1010))

  val receiveNewSimulationRequest = ReceiveNewSimulationRequest(instanceUserRequest.userId,
    instanceUserRequest.fullName, SpaceshipProtocol(newSimulationRequestUnexistentHost.spaceshipProtocol.hostname, newSimulationRequestUnexistentHost.spaceshipProtocol.port))

  val newSimulationResponse = NewSimulationResponse("randomPlayer123", "Random Player", "gameId-12345", "gki10")
  val anotherNewSimulationResponse = NewSimulationResponse("randomPlayer123", "Random Player", "gameId-56789", "gki10")

  val salvoRequest = SalvoRequest(List("0x0", "0x1", "0x2", "0x3", "0x4"))
  val excesiveSalvoRequest = SalvoRequest(List("0x0", "0x1", "0x2", "0x3", "0x4", "0x5"))

  when(simulationClientMock.sendNewSimulationRequest(any(), any(),any())).thenReturn (Left(HttpClientError(500)),
                                                                                      Right(newSimulationResponse),
                                                                                      Right(anotherNewSimulationResponse))

}
