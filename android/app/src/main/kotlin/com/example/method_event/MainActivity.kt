package com.example.method_event

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkRequest
import android.os.BatteryManager
import android.util.Log
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.EventChannel.EventSink
import io.flutter.plugin.common.MethodChannel

class MainActivity : FlutterActivity() {

    private var connectivityManager: ConnectivityManager? = null
    private var eventSink: EventSink? = null
    private var networkCallback: NetworkCallback? = null

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL)
            .setMethodCallHandler { call, result ->
                if (call.method.equals("getBatteryLevel")) {
                    val batteryManager =
                        getSystemService(Context.BATTERY_SERVICE) as BatteryManager
                    val batteryLevel =
                        batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)

                    if (batteryLevel != -1) {
                        result.success(batteryLevel)
                    } else {
                        result.error("UNAVAILABLE", "Battery level not available.", null)
                    }
                } else {
                    result.notImplemented()
                }
            }

        EventChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL_EVENT)
            .setStreamHandler(
                object : EventChannel.StreamHandler {
                    override fun onListen(arguments: Any?, events: EventSink) {
                        eventSink = events
                        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                        val networkRequest = NetworkRequest.Builder().build()

                        networkCallback = object : NetworkCallback() {
                            override fun onAvailable(network: Network) {
                                runOnUiThread {
                                    eventSink?.success("Connected")
                                }
                            }

                            override fun onLost(network: Network) {
                                runOnUiThread {
                                    eventSink?.success("Disconnected")
                                }
                            }
                        }

                        connectivityManager?.registerNetworkCallback(networkRequest, networkCallback!!)
                    }

                    override fun onCancel(arguments: Any) {
                        connectivityManager?.unregisterNetworkCallback(networkCallback!!)
                        networkCallback = null
                        eventSink = null
                    }
                }
            )
    }






    companion object {
        internal const val CHANNEL = "com.example/battery"
        private const val CHANNEL_EVENT = "com.example/connectivity"

    }




}




