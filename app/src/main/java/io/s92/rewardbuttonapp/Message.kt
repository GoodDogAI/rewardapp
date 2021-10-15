package io.s92.rewardbuttonapp

sealed class Message(val discriminant: Byte) {
  open val data: Byte = 0x0
}

object heartbeatMessage : Message(0x0)

class MoveMessage(override val data: Byte) : Message(0x01)

val MoveForward = MoveMessage(0x01)
val MoveBackward = MoveMessage(0x02)
val MoveLeft = MoveMessage(0x04)
val MoveRight = MoveMessage(0x08)

class ScoreMessage(reward: Byte) : Message(0x02) {
  override val data: Byte = reward
}

val RewardMessage = ScoreMessage(-1)
val PunishMessage = ScoreMessage(1)
