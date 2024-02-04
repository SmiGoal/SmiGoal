import 'dart:convert';
import 'dart:io';

import 'package:dio/io.dart';
import 'package:hive/hive.dart';
import 'package:smigoal/models/message.dart';
import 'package:dio/dio.dart';

import '../functions/api_service.dart';

class MessageDB {
  static final _requestServer = MessageDB._singleton();
  late final Box<Message> box;

  factory MessageDB() {
    return _requestServer;
  }
  MessageDB._singleton() {}

  String extractUrl(String message) {
    // 이거 안되는 것도 해결해야함
    final urlPattern = RegExp(
      r'(https?:\/\/)?(www\.)?[-a-zA-Z0-9@:%._\+~#=]{2,256}\.[a-z]{2,6}\b([-a-zA-Z0-9@:%_\+.~#?&//=]*)',
      caseSensitive: false,
    );
    final match = urlPattern.firstMatch(message);
    print("match? $match");
    return match != null ? match.group(0) ?? "" : "";
  }
  bool containsUrl(String text) {
    // URL을 검출하기 위한 정규 표현식 패턴
    final urlPattern = RegExp(
      r'(https?:\/\/)?(www\.)?[-a-zA-Z0-9@:%._\+~#=]{2,256}\.[a-z]{2,6}\b([-a-zA-Z0-9@:%_\+.~#?&//=]*)',
      caseSensitive: false,
    );

    // 주어진 텍스트에서 URL이 있는지 검사
    return urlPattern.hasMatch(text);
  }

  Future<String> postRequest(String message) async {
    print("Request 요청");
    final dio = Dio(); // Dio 인스턴스 생성
    // (dio.httpClientAdapter as DefaultHttpClientAdapter).onHttpClientCreate =
    //     (HttpClient client) {
    //   client.badCertificateCallback =
    //       (X509Certificate cert, String host, int port) => true;
    //   return client;
    // };

    final apiService = ApiService(dio);
    final url = extractUrl(message);
    print(containsUrl(message));
    print(url);
    message = message.replaceFirst(url, "");
    print("BODY : $url | $message");

    // Map을 사용하여 요청 본문 데이터 구성
    final Map<String, String?> postData = {
      "url": null,
      "message": message,
    };

    try {
      final response = await apiService.getResponse(postData);
      print("서버 응답: $response");
      return response;
    } catch (e) {
      print("에러 발생: $e");
      return "";
    }
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
