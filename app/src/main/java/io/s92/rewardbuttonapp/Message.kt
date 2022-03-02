package io.s92.rewardbuttonapp

import java.lang.Float.max
import java.lang.Float.min
import java.util.*
import kotlin.experimental.or
import kotlin.math.round

class Message(val discriminant: Byte, val data: Byte = 0x0, val extra_data: Byte = 0x0)


object Messages {

  val Heartbeat = Message(0x0)

  private fun move(data: Byte) = Message(0x01, data)

  fun Move(linear_x: Float, angular_z: Float): Message {
      val x: Byte = round(127 * max(-1.0F, min(1.0F, linear_x)) / 1.0F).toInt().toByte()
      val z: Byte = round(127 * max(-1.0F, min(1.0F, angular_z)) / 1.0F).toInt().toByte()
      return Message(0x01, x, z)
  }

  val StopMoving = move(0x0)

  private fun score(reward: Byte) = Message(0x02, reward)

  val Reward = score(1)
  val Punish = score(-1)
}
