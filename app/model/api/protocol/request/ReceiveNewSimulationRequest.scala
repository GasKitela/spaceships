package model.api.protocol.request

case class ReceiveNewSimulationRequest(userId: String,
                                       fullName: String,
                                       spaceshipProtocol: SpaceshipProtocol)

case class SpaceshipProtocol(hostname: String,
                             port: Int)
