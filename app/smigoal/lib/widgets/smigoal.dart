import 'package:flutter/material.dart';

import 'package:smigoal/functions/sms_service.dart';
import 'package:smigoal/widgets/settings.dart';

class SmiGoal extends StatefulWidget {
  SmiGoal({super.key});

  @override
  State<SmiGoal> createState() => _SmiGoalState();
}

class _SmiGoalState extends State<SmiGoal> {
  String message = "SmiGoal....";
  String sender = "KU";
  DateTime timestamp = DateTime.now();

  @override
  void initState() {
    super.initState();
    final smsService = SMSService(_getMessage);
    smsService.initialize();
  }

  // Future<String> get message async {
  void _getMessage(String message, String sender, DateTime timestamp) {
    setState(() {
      this.message = message;
      this.sender = sender;
      this.timestamp = timestamp;
      
    });
  }

  @override
  Widget build(BuildContext context) {
    print('hello');

    return Scaffold(
      appBar: AppBar(),
      drawer: const Settings(),
      body: SizedBox(
        width: double.infinity,
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text(message),
            Text(sender),
            Text(timestamp.toString()),
          ],
        ),
      ),
    );
  }
}
