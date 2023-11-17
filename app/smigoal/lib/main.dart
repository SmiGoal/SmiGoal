import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:permission_handler/permission_handler.dart';

import 'package:smigoal/function/sms_service.dart';
import 'package:smigoal/widgets/smigoal.dart';

var kColorScheme = ColorScheme.fromSeed(
  seedColor: const Color.fromARGB(255, 96, 59, 181),
);
var kDarkColorScheme = ColorScheme.fromSeed(
  brightness: Brightness.dark,
  seedColor: const Color.fromARGB(255, 5, 99, 155),
);
Future<void> requestPermission() async {
  var status = await Permission.sms.status;
  if (!status.isGranted) {
    await Permission.sms.request();
  }
}

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await requestPermission();
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
