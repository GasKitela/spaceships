package services

import model.api.protocol.response.{GameStatusResponse, NewSimulationResponse}
import model.errors._
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FlatSpec}
import repository.Store

class UserServiceTest extends FlatSpec with BeforeAndAfter with BeforeAndAfterAll with UserServiceTestSupport {

  val userService = new UserService(simulationServiceMock, simulationClientMock)

  before {
    Store.instancePlayer = null

  }

  "Attempt to create user when there is none" should "be successful" in {
    userService.createInstanceUser(instanceUserRequest) shouldBe Right()
  }

  "Attempt to create user when there is none again" should "be successful" in {
    userService.createInstanceUser(instanceUserRequest) shouldBe Right()
    userService.createInstanceUser(anotherInstanceUserRequest) shouldBe Left(UserAlreadyCreatedError(401))
  }

  "Send new simulation request with no instance player set" should "fail for not having instance player" in {
    userService.sendNewSimulationRequest(newSimulationRequest) shouldBe Left(InstancePlayerNotDefinedError(400))
  }

  "Send new simulation request" should "succeed" in {
    userService.createInstanceUser(instanceUserRequest) shouldBe Right()
    userService.sendNewSimulationRequest(newSimulationRequest) shouldBe Right(_: NewSimulationResponse)
  }

  "Send new simulation request to unexistent host" should "fail with http error" in {
    userService.createInstanceUser(instanceUserRequest)
    userService.sendNewSimulationRequest(newSimulationRequestUnexistentHost) shouldBe Left(HttpClientError(500))
  }

  "Sending two new simulation request succesfully" should "fill games list in object with two elements" in {
    userService.createInstanceUser(instanceUserRequest)
    userService.sendNewSimulationRequest(newSimulationRequest)
    userService.sendNewSimulationRequest(anotherSimulationRequest)

    Store.games.size shouldBe 2
  }

  "Get game status from unexistent game id" should "fail with game not found" in {
    userService.createInstanceUser(instanceUserRequest)
    userService.sendNewSimulationRequest(newSimulationRequest)
    userService.getGameStatus("randomId") shouldBe Left(GameNotFoundError(400, "randomId"))
  }

  "Get game status from existent game id" should "succeed" in {
    userService.createInstanceUser(instanceUserRequest)
    userService.sendNewSimulationRequest(newSimulationRequest)
    userService.getGameStatus("gameId-12345").right.get shouldBe a [GameStatusResponse]
  }

  "Attempt to send more salvo than allowed" should "fail with player cant attack error" in {
    userService.createInstanceUser(instanceUserRequest)
    userService.sendNewSimulationRequest(newSimulationRequest)
    userService.sendAttack("gameId-12345", excesiveSalvoRequest) shouldBe Left(PlayerCantAttackError(401, Store.instancePlayer.userId))
  }

}
