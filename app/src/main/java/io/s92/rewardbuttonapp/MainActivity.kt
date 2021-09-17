package io.s92.rewardbuttonapp

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import kotlin.system.exitProcess

/**
 * To connect phone to robot:
 *
 * on robot:
 * - systemctl start bluetooth (<-- todo: make automatic)
 * - sudo rfkill unblock bluetooth
 * - sudo bluetoothctl
 * - [bluetoothctl]> discoverable on
 * - [bluetoothctl]> pairable on
 * - [bluetoothctl]> trust $DEVICE_MAC
 *
 * on phone:
 * - bluetooth settings
 * - scan for devices
 * - pick robot-*
 *
 * on robot:
 * - [bluetoothctl]> discoverable off
 */
class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    if (!mBluetoothAdapter.isEnabled) {
      exitProcess(1)
    }
    val pairedDevices = mBluetoothAdapter.bondedDevices
    val pairedDev: Array<Any> = pairedDevices.toTypedArray()
    val mArrayAdapter = ArrayAdapter<BluetoothDevice>(this, android.R.layout.simple_list_item_1, 0)
    if (pairedDevices.size > 0) {
      for (device in pairedDevices) {
        mArrayAdapter.add(device)
      }
    }
    val listView = findViewById<ListView>(R.id.PairedDevicesListView)
    listView.adapter = mArrayAdapter
    listView.setOnItemClickListener { _, _, position, _ ->
      val connectThread = ConnectThread(pairedDev[position] as BluetoothDevice)
      connectThread.start()
    }
    //    val connectThread = ConnectThread(pairedDev.last() as BluetoothDevice)
    //    connectThread.run()
    // connectThread.cancel()
  }

  fun buttonOnClick(v: View?) {
    val button: Button? = v as Button?
  }

  private class ConnectThread(device: BluetoothDevice) : Thread() {
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
          System.exit(1)
        } catch (closeException: IOException) { // System.exit(1);
        }
        return
      }

      val connectedThread = ConnectedThread(mmSocket)
      // Do work to manage the connection (in a separate thread)
      // manageConnectedSocket(mmSocket);
      val bytes = ByteArray(1)
      bytes[0] = 97
      while (true) {
        bytes[0] = ((bytes[0] - 97 + 1) % 26 + 97).toByte()
        connectedThread.write(bytes)
        sleep(250)
        Log.w("RewardButton", "Sending " + bytes[0].toInt());
      }
    }

    /** Will cancel an in-progress connection, and close the socket */
    fun cancel() {
      try {
        mmSocket!!.close()
      } catch (e: IOException) {}
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
