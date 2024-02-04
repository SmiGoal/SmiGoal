import 'dart:convert';

import 'package:dio/dio.dart';
import 'package:hive/hive.dart';
import 'package:smigoal/models/message.dart';
import 'package:http/http.dart' as http;

class RequestServer {
  static final _requestServer = RequestServer._singleton();
  late final Box<Message> box;

  factory RequestServer() {
    return _requestServer;
  }
  RequestServer._singleton() {
  }

  String extractUrl(String message) {
    // 이거 안되는 것도 해결해야함
    final urlPattern = RegExp(
      r'(http|https)://([a-zA-Z0-9\-\._]+)\.([a-zA-Z]{2,5})(:[0-9]{1,5})?(/.*)?',
      caseSensitive: false,
    );
    final match = urlPattern.firstMatch(message);
    print("match? $match");
    return match != null ? match.group(0) ?? "" : "";
  }
  bool containsUrl(String text) {
    // URL을 검출하기 위한 정규 표현식 패턴
    final urlPattern = RegExp(
      r'(http|https)://([a-zA-Z0-9\-\._]+)\.([a-zA-Z]{2,5})(:[0-9]{1,5})?(/.*)?',
      caseSensitive: false,
    );

    // 주어진 텍스트에서 URL이 있는지 검사
    return urlPattern.hasMatch(text);
  }

  Future<void> postData(
      String sender, String message, DateTime timestamp) async {
    final url = extractUrl(message);
    print(containsUrl(message));
    print(url);
    message.replaceFirst(url, "");
    print("BODY : $url | $message");
    // final response = await http.post(
    //   Uri.parse('https://example.com/data'),
    //   headers: <String, String>{
    //     'Content-Type': 'application/json; charset=UTF-8',
    //   },
    //   body: jsonEncode(<String, String>{
    //     'message': 'Sample Data',
    //   }),
    // );

    // if (response.statusCode == 201) {
    //   // 서버가 데이터 생성을 성공적으로 응답했을 경우의 처리
    //   var data = jsonDecode(response.body);
    //   print(data);
    // } else {
    //   // 요청이 실패한 경우의 처리
    //   throw Exception('Failed to post data');
    // }
  }

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
