import 'package:hive/hive.dart';
import 'package:smigoal/models/message.dart';

class RequestServer {
  RequestServer();

  Future<void> saveMessage(Message message) async {
    var box = await Hive.openBox<Message>('messages');
    await box.add(message);
    await box.close();
  }

  Future<List<Message>> getMessages() async {
    var box = await Hive.openBox<Message>('messages');
    List<Message> messages = box.values.toList();
    await box.close();
    return messages;
  }
}
