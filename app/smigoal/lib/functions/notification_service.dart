import 'package:flutter_local_notifications/flutter_local_notifications.dart';

class NotificationService {
  final FlutterLocalNotificationsPlugin _flutterLocalNotificationsPlugin =
      FlutterLocalNotificationsPlugin();
  static const String _channel = "SmiGoal Notification Channel ID";
  static const String _name = "SmiGoal";
  static const String _description = "Alert Result of Smishing Detection";

  Future<void> init() async {
    const AndroidInitializationSettings initializationSettingsAndroid =
        AndroidInitializationSettings('@mipmap/icon_smigoal');

    const InitializationSettings initializationSettings =
        InitializationSettings(
      android: initializationSettingsAndroid,
    );

    await _flutterLocalNotificationsPlugin.initialize(initializationSettings);
  }

  Future<void> showNotification(String messageBody, String sender) async {
    print('알림을 띄울겁니다...');
    const AndroidNotificationDetails androidPlatformChannelSpecifics =
        AndroidNotificationDetails(_channel, _name,
            channelDescription: _description,
            importance: Importance.max,
            priority: Priority.high,
            showWhen: false);

    const NotificationDetails platformChannelSpecifics =
        NotificationDetails(android: androidPlatformChannelSpecifics);

    await _flutterLocalNotificationsPlugin.show(
      0, // 알림 ID
      '새로운 문자 메시지', // 알림 제목
      '발신자: $sender, 메시지: $messageBody', // 알림 내용
      platformChannelSpecifics,
    );
  }
}
