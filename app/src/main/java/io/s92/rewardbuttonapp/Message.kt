package io.s92.rewardbuttonapp

import java.util.*
import kotlin.experimental.or

class Message(val discriminant: Byte, val data: Byte = 0x0)

enum class MoveDirection {
  Left,
  Right,
  Forward,
  Backward,
}

private val directionMap: Map<MoveDirection, Byte> =
    mapOf(
        MoveDirection.Forward to 0x1,
        MoveDirection.Backward to 0x2,
        MoveDirection.Left to 0x4,
        MoveDirection.Right to 0x8,
    )

object Messages {

  val Heartbeat = Message(0x0)

  private fun move(data: Byte) = Message(0x01, data)

  fun Move(direction: EnumSet<MoveDirection>): Message {
    return move(
        directionMap.entries.fold(0x0.toByte()) { acc, entry ->
          if (direction.contains(entry.key)) (acc or entry.value) else acc
        })
  }

  val StopMoving = move(0x0)

  private fun score(reward: Byte) = Message(0x02, reward)

  val Reward = score(1)
  val Punish = score(-1)
}
