package smigoal.server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    // 1. 사용하는 곳에서 url, port 등의 추가적인 설정이 필요
    @Bean
    public WebClient.Builder webClientBuilder(){
        return WebClient.builder();
    }

    // 2. 여러 설정으로 각기 다른 방식을 쓰는 것이 아니라면 이처럼 설정 단계에서 설정
    // 이렇게 하면 서비스 레이어에서는 그냥 @RequiredArgsConstructor를 넣으면 됨
//    @Bean
//    public WebClient webClient(WebClient.Builder builder) {
//        return builder.baseUrl("http://localhost:5000").build();
//    }
}
