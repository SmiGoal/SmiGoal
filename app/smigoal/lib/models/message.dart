import 'package:hive/hive.dart';

part 'message.g.dart';

@HiveType(typeId: 0) // Unique typeId for each HiveType
class Message {
  @HiveField(0)
  final String sender;

  @HiveField(1)
  final String content;

  @HiveField(2)
  final DateTime timestamp;

  @HiveField(3)
  final bool containsUrl;

  Message({
    required this.sender,
    required this.content,
    required this.timestamp,
    required this.containsUrl,
  });
}
