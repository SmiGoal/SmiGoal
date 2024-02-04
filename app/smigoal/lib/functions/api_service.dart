import 'package:retrofit/retrofit.dart';
import 'package:dio/dio.dart' hide Headers;
import '../models/message.dart';

part 'api_service.g.dart';

@RestApi(baseUrl: "http://10.0.2.2:8080")
abstract class ApiService {
  factory ApiService(Dio dio, {String baseUrl}) = _ApiService;

  @POST("")
  // @Headers(<String, dynamic>{
  //   'Content-Type': 'application/json',
  // })
  Future<String> getResponse(@Body() Map<String, String?> message);
}
