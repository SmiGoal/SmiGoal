import 'package:hive/hive.dart';
import 'package:json_annotation/json_annotation.dart';

part 'message.g.dart';

@JsonSerializable()
@HiveType(typeId: 0) // Unique typeId for each HiveType
class Message {
  @JsonKey(name: 'sender')
  @HiveField(0)
  final String sender;

  @JsonKey(name: 'content')
  @HiveField(1)
  final String content;

  @JsonKey(name: 'timestamp')
  @HiveField(2)
  final DateTime timestamp;

  @JsonKey(name: 'contains_url')
  @HiveField(3)
  final bool containsUrl;

  Message({
    required this.sender,
    required this.content,
    required this.timestamp,
    required this.containsUrl,
  });

  Message.api(this.sender, this.timestamp, {
    required this.content,
    required this.containsUrl,
  });

  // JSON 직렬화 및 역직렬화를 위한 메소드
  factory Message.fromJson(Map<String, dynamic> json) => _$MessageFromJson(json);
  Map<String, dynamic> toJson() => _$MessageToJson(this);
}
