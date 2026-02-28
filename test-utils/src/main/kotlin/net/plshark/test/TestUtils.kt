package net.plshark.test

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking

/** Test utility methods. */
object TestUtils {
  /** Execute a block with no response immediately. */
  fun doBlocking(block: suspend CoroutineScope.() -> Unit): Unit = runBlocking { block() }
}
