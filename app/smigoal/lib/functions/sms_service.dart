import 'dart:isolate';

import 'package:flutter/services.dart';
import 'package:smigoal/main.dart';

import '../models/message.dart';

class SMSService {
  const SMSService(this.onReceive);
  static const platform = MethodChannel('com.example.smigoal/sms');
  final Function(String, String, String, DateTime) onReceive;

  void initialize() {
    platform.setMethodCallHandler(_onMethodCall);
    // startBackgroundTask();
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
        final result = await requestServer.postRequest(message);
        if(result != "") onReceive(sender, message, result, timestamp);
        // print('result: ${requestServer.extractUrl(message)}${requestServer.containsUrl(message)}');
        requestServer.saveMessage(Message(
          sender: sender,
          content: message,
          timestamp: timestamp,
          containsUrl: false
        ));
        List<Message> l = await requestServer.getMessages();
        // print('From Box');
        // l.forEach((element) { print(element.content); });
        // notification.showNotification(message, sender);
        break;
      default:
        print('Unknown method ${call.method}');
    }
  }

  // Future<void> startBackgroundTask() async {
  //   final ReceivePort receivePort = ReceivePort();
  //   final Isolate isolate = await Isolate.spawn(
  //       backgroundTask, receivePort.sendPort);
  //
  //   receivePort.listen((dynamic data) {
  //     // 데이터를 받아서 처리
  //     print("Received data from background: $data");
  //   });
  // }
  //
  // static void backgroundTask(SendPort sendPort) {
  //   final ReceivePort receivePort = ReceivePort();
  //
  //   sendPort.send(receivePort.sendPort);
  //
  //   receivePort.listen((dynamic data) {
  //   // 백그라운드 작업을 수행하고 결과를 메인 Isolate로 전송
  //     final String message = data;
  //     final String result = "Processed: $message";
  //     platform.invokeMethod('onReceivedSMS', {"message": message, "result": result});
  //   });
  // }
}
