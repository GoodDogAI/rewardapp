package io.s92.rewardbuttonapp

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.TimeUnit

class DeviceActivity : AppCompatActivity() {

  @Volatile private var thread: ConnectThread? = null
  private val messages = LinkedBlockingDeque<Message>()
  private var start: Long = 0

  private val messageMap =
      mapOf(
          R.id.btnBackward to Messages.MoveBackward,
          R.id.btnForward to Messages.MoveForward,
          R.id.btnLeft to Messages.MoveLeft,
          R.id.btnRight to Messages.MoveRight,
          R.id.btnReward to Messages.Reward,
          R.id.btnPunish to Messages.Punish,
      )

  private fun onButtonClick(v: View) {
    messageMap[v.id]?.let { messages.push(it) }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_device)
    val device = intent.getParcelableExtra<BluetoothDevice>(DEVICE_EXTRA)!!
    findViewById<TextView>(R.id.deviceNameView).text =
        "Connected to ${device.name} (${device.address})"
    start = System.nanoTime()
    thread = ConnectThread(device).apply(Thread::start)
    messageMap.keys.forEach { findViewById<Button>(it).setOnClickListener(this::onButtonClick) }
  }

  override fun onDestroy() {
    thread = null
    super.onDestroy()
  }

  companion object {
    const val DEVICE_EXTRA: String = "device"
  }

  private inner class ConnectThread(device: BluetoothDevice) : Thread() {
    private val mmSocket: BluetoothSocket?
    private val mmDevice: BluetoothDevice
    override fun run() {
      val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
      mBluetoothAdapter.cancelDiscovery()
      try {
        mmSocket!!.connect()
      } catch (connectException: IOException) {
        try {
          mmSocket!!.close()
        } catch (closeException: IOException) {}
        finish()
      }

      val connectedThread = ConnectedThread(mmSocket)
      // Do work to manage the connection (in a separate thread)
      // manageConnectedSocket(mmSocket);
      val bytes = ByteArray(8)
      while (currentThread() == thread) {
        val msg = messages.pollFirst(250, TimeUnit.MILLISECONDS) ?: Messages.Heartbeat

        val counter = (System.nanoTime() - start).toInt()
        bytes[0] = (counter shl 24).toByte()
        bytes[1] = (counter shl 16).toByte()
        bytes[2] = (counter shl 8).toByte()
        bytes[3] = counter.toByte()

        bytes[4] = msg.discriminant
        bytes[5] = msg.data
        bytes[6] = 0x0
        bytes[7] = 0x0

        connectedThread.write(bytes)
      }
    }

    init {
      var tmp: BluetoothSocket? = null
      mmDevice = device
      try {
        val m = device::class.java.getMethod("createInsecureRfcommSocket", Int::class.java)
        tmp = m.invoke(device, 1) as BluetoothSocket
      } catch (e: Exception) {}
      mmSocket = tmp
    }
  }

  private class ConnectedThread(private val mmSocket: BluetoothSocket?) : Thread() {
    private val mmInStream: InputStream?
    private val mmOutStream: OutputStream?
    override fun run() {
      val buffer = ByteArray(1024) // buffer store for the stream
      var bytes: Int // bytes returned from read()

      // Keep listening to the InputStream until an exception occurs
      while (true) {
        bytes =
            try {
              // Read from the InputStream
              mmInStream!!.read(buffer)
              // Send the obtained bytes to the UI activity
              // mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();
            } catch (e: IOException) {
              break
            }
      }
    }

    /* Call this from the main activity to send data to the remote device */
    fun write(bytes: ByteArray?) {
      try {
        mmOutStream!!.write(bytes)
      } catch (e: IOException) {}
    }

    /* Call this from the main activity to shutdown the connection */
    fun cancel() {
      try {
        mmSocket!!.close()
      } catch (e: IOException) {}
    }

    init {
      var tmpIn: InputStream? = null
      var tmpOut: OutputStream? = null

      // Get the input and output streams, using temp objects because
      // member streams are final
      try {
        tmpIn = mmSocket!!.inputStream
        tmpOut = mmSocket.outputStream
      } catch (e: IOException) {}
      mmInStream = tmpIn
      mmOutStream = tmpOut
    }
  }
}
