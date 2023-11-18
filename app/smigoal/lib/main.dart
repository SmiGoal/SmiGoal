import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:hive/hive.dart';
import 'package:path_provider/path_provider.dart' as path_provider;
import 'package:smigoal/functions/notification_service.dart';

import 'package:smigoal/models/message.dart';
import 'package:smigoal/widgets/smigoal.dart';

var kColorScheme = ColorScheme.fromSeed(
  seedColor: const Color.fromARGB(255, 96, 59, 181),
);
var kDarkColorScheme = ColorScheme.fromSeed(
  brightness: Brightness.dark,
  seedColor: const Color.fromARGB(255, 5, 99, 155),
);
final permissions = [Permission.sms, Permission.notification];

Future<void> requestPermission() async {
  // 권한 상태 확인
  Map<Permission, PermissionStatus> statuses = await permissions.request();
  for (var status in statuses.entries) {
    if (!status.value.isGranted) {
      // 권한이 부여되지 않았다면 추가적인 처리가 필요할 수 있습니다.
      status.key.request();
    }
  }
}

final notification = NotificationService();

void initApp() async {
  WidgetsFlutterBinding.ensureInitialized();
  await requestPermission();
  notification.init();

  final directory = await path_provider.getApplicationDocumentsDirectory();
  Hive.init(directory.path);
  Hive.registerAdapter(MessageAdapter());
}

void main() async {
  initApp();

  SystemChrome.setPreferredOrientations([
    DeviceOrientation.portraitUp,
  ]).then((fn) {
    runApp(MaterialApp(
      theme: ThemeData().copyWith(
        useMaterial3: true,
        colorScheme: kColorScheme,
      ),
      darkTheme: ThemeData().copyWith(
        useMaterial3: true,
        colorScheme: kDarkColorScheme,
      ),
      home: SmiGoal(),
    ));
  });
}
