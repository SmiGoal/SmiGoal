import 'dart:async';

import 'package:flutter/services.dart';

class ResultHandler {
  ResultHandler(this.onReceive);
  final Function(String, String, String, DateTime) onReceive;
  final platform = MethodChannel('com.example.smigoal/sms');

  Future<void> init() async {
    // await _flutterLocalNotificationsPlugin.initialize(initializationSettings);
    platform.setMethodCallHandler(_onMethodCall);
  }

  Future<void> _onMethodCall(MethodCall call) async {
    print("Message Received");
    print(call.method);
    switch (call.method) {
      case "onReceivedSMS":
        print("${call.arguments}");
        final String message = call.arguments['message'];
        print(message);
        final String sender = call.arguments['sender'];
        print(sender);
        final String result = call.arguments['result'];
        print(result);
        final DateTime timestamp =
        DateTime.fromMillisecondsSinceEpoch(call.arguments['timestamp']);
        // 여기서 메시지 내용, 송신자, 시각 정보를 처리합니다.
        print("From ${sender}, ${timestamp}: Message: ${message}\n");
        onReceive(message, sender, result, timestamp);
        break;
      default:
        print('Unknown method ${call.method}');
    }
  }
}
