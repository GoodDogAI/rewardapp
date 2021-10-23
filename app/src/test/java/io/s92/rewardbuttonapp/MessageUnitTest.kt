package io.s92.rewardbuttonapp

import java.util.*
import org.junit.Assert.assertEquals
import org.junit.Test

class MessageUnitTest {
  @Test
  fun moveMessages() {
    assertEquals(0x01.toByte(), Messages.Move(EnumSet.of(MoveDirection.Forward)).data)
    assertEquals(0x02.toByte(), Messages.Move(EnumSet.of(MoveDirection.Backward)).data)
    assertEquals(0x04.toByte(), Messages.Move(EnumSet.of(MoveDirection.Left)).data)
    assertEquals(0x08.toByte(), Messages.Move(EnumSet.of(MoveDirection.Right)).data)

    assertEquals(
        0x05.toByte(), Messages.Move(EnumSet.of(MoveDirection.Forward, MoveDirection.Left)).data)
    assertEquals(
        0x0a.toByte(), Messages.Move(EnumSet.of(MoveDirection.Backward, MoveDirection.Right)).data)
  }
}
