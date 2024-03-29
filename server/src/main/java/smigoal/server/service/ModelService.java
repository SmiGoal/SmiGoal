package smigoal.server.service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import smigoal.server.dto.ModelResponseDto;

import java.util.List;

@Service
public class ModelService {

    private final WebClient webClient;

    // 1번째 방식(2번째로 해도 되는데 그냥 이왕 쓰는거 이렇게 써봄)
    public ModelService(WebClient.Builder builder) {
        this.webClient = builder
//                .baseUrl("http://smigoal-model:5000")
                .baseUrl("http://localhost:5000")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public ModelResponseDto callFlaskService(List<String> requestData) {
        ModelResponseDto response = webClient.post()
                .uri("/test")
                .bodyValue(requestData)
                .retrieve()
                .bodyToMono(ModelResponseDto.class) // 응답을 String으로 변환(비동기타입인 Mono로 반환됨)
                .block();

        return response;
    }
}
