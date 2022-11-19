package com.example.mapplication

import android.Manifest
import android.Manifest.permission
import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.content.*
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.net.wifi.WifiManager.SCAN_RESULTS_AVAILABLE_ACTION
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import com.example.mapplication.ui.theme.MApplicationTheme

data class devicesData(
    val data:WifiP2pDevice
        )

class ScreenViewModel: ViewModel(){
    private val peers = mutableListOf<WifiP2pDevice>()
    fun addData(data:WifiP2pDevice){
        if (data!= peers){
            peers.clear()
            peers.addAll(listOf(data))
        }
    }

    fun getAllDevice(): MutableList<WifiP2pDevice> {
        return peers
    }

}

class MainActivity : ComponentActivity() {
    val manager: WifiP2pManager? by lazy(LazyThreadSafetyMode.NONE) {
        getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager?
    }
    var resultList = ArrayList<ScanResult>()

    var channel: WifiP2pManager.Channel? = null
    private val intentFilter = IntentFilter().apply {
        addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
        addAction(WifiP2pManager.EXTRA_WIFI_STATE)
        addAction(WifiP2pManager.EXTRA_DISCOVERY_STATE)

    }
    private val peers = mutableListOf<WifiP2pDevice>()
    val broadcastReceiver = object : BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context, intent: Intent) {
            manager?.discoverPeers(channel, object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                   Toast.makeText(this@MainActivity,"Hurray wifi found for connection",Toast.LENGTH_LONG).show()
                }

                override fun onFailure(reasonCode: Int) {
                    Toast.makeText(this@MainActivity,"NO wifi found for connection $reasonCode",Toast.LENGTH_LONG).show()
                }
            })
            val action: String? = intent.action

            when (action) {
                WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                    manager?.requestPeers(channel) { peers: WifiP2pDeviceList? ->
                       Log.d("TABATUBU","Seen here")
                    }
                }


                WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                    // Check to see if Wi-Fi is enabled and notify appropriate activity
                    val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
                    when (state) {
                        WifiP2pManager.WIFI_P2P_STATE_ENABLED -> {
                            Log.d("ENABLED","wifi enabled for use")

                        }
                        else -> {
                            // Wi-Fi P2P is not enabled
                            Log.d("NOT_ENABLED","wifi not enabled for use")
                        }
                    }
                }

                WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                    // Respond to new connection or disconnections
                }
                WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {
                    // Respond to this device's wifi state changing
                }
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        channel = manager?.initialize(this, mainLooper, null)

//        wifiManager = this.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        setContent {
            MApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {Greeting(peers)}
            }
        }





    }

    /* register the broadcast receiver with the intent values to be matched */
    override fun onResume() {
        super.onResume()
       registerReceiver(broadcastReceiver,intentFilter)

//

    }

    /* unregister the broadcast receiver */
    override fun onPause() {
        super.onPause()

            unregisterReceiver(broadcastReceiver)

    }


}

@Composable
fun Greeting(data: MutableCollection<WifiP2pDevice>) {

    Log.d("Taliban", data.toString())
    Column() {
        Text(text = "peter")
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MApplicationTheme {
//        Greeting()
    }
}