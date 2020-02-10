package model.api.user

case class InstanceUserRequest(userId: String,
                               fullName: String,
                               host: String,
                               port: Int)
