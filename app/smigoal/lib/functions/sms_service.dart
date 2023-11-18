import 'package:flutter/services.dart';
import 'package:smigoal/main.dart';

class SMSService {
  const SMSService(this.onReceive);
  static const platform = MethodChannel('com.example.smigoal/sms');
  final Function(String, String, DateTime) onReceive;

  void initialize() {
    platform.setMethodCallHandler(_onMethodCall);
  }

  Future<void> _onMethodCall(MethodCall call) async {
    print("Message Received");
    switch (call.method) {
      case "onReceivedSMS":
        final String message = call.arguments['message'];
        final String sender = call.arguments['sender'];
        final DateTime timestamp =
            DateTime.fromMillisecondsSinceEpoch(call.arguments['timestamp']);
        // 여기서 메시지 내용, 송신자, 시각 정보를 처리합니다.
        print("From ${sender}, ${timestamp}: Message: ${message}\n");
        onReceive(sender, message, timestamp);
        notification.showNotification(message, sender);
        break;
      default:
        print('Unknown method ${call.method}');
    }
  }
}
