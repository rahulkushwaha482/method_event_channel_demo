import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  static const platform = MethodChannel('com.example/battery');
  String _batteryLevel = "Unknown";
  static const eventChannel = EventChannel('com.example/connectivity');
  String _connectivityStatus = "Unknown";

  @override
  void initState() {
    super.initState();
    eventChannel.receiveBroadcastStream().listen(
      (event) {
        setState(() {
          _connectivityStatus = "Connectivity: $event";
        });
      },
      onError: (error) {
        setState(() {
          _connectivityStatus = "Error: $error";
        });
      },
    );
  }

  Future<void> _getBatteryLevel() async {
    try {
      final int result = await platform.invokeMethod('getBatteryLevel');
      setState(() {
        _batteryLevel = "Battery level: $result%";
      });
    } catch (e) {
      setState(() {
        _batteryLevel = "Failed to get battery level: $e";
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text("Event and Method")),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text(_batteryLevel),
            ElevatedButton(
              onPressed: _getBatteryLevel,
              child: Text("Get Battery Level"),
            ),
            Text(' Network Connectivity Status'),
            Text(_connectivityStatus),
          ],
        ),
      ),
    );
  }
}
