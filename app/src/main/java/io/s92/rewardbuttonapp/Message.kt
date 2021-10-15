package io.s92.rewardbuttonapp

class Message(val discriminant: Byte, val data: Byte = 0x0)

object Messages {

  val Heartbeat = Message(0x0)

  private fun move(data: Byte) = Message(0x01, data)

  val MoveForward = move(0x01)
  val MoveBackward = move(0x02)
  val MoveLeft = move(0x04)
  val MoveRight = move(0x08)

  private fun score(reward: Byte) = Message(0x02, reward)

  val Reward = score(-1)
  val Punish = score(1)
}
