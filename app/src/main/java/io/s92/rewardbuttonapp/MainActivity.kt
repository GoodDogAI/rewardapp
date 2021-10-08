package io.s92.rewardbuttonapp

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
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
    val mArrayAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, 0)
    if (pairedDevices.size > 0) {
      for (device in pairedDevices) {
        mArrayAdapter.add(device.name ?: device.address ?: device.toString())
      }
    }
    val listView = findViewById<ListView>(R.id.PairedDevicesListView)
    listView.adapter = mArrayAdapter
    listView.setOnItemClickListener { _, _, position, _ ->
      val intent = Intent(this, DeviceActivity::class.java)
      intent.putExtra(DeviceActivity.DEVICE_EXTRA, pairedDev[position] as BluetoothDevice)
      startActivity(intent)
    }
  }
}
