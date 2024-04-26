package smigoal.server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.flashvayne.chatgpt.service.ChatgptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class GPTService {
    private final ChatgptService chatgptService;

    @Value("${chatgpt.api-key}")
    private String API_KEY;
    private static final String ENDPOINT = "https://api.openai.com/v1/chat/completions";

    public String summarizeText(String userPrompt){
        String prompt = userPrompt.replace("\n", " ").trim();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + API_KEY);

        Map<String, Object> requestBody = new HashMap<>();
//        requestBody.put("model", "gpt-4-1106-preview");
        // 비용문제로 최종단계에서만 gpt4 사용
        requestBody.put("model", "gpt-3.5-turbo-1106");
        requestBody.put("temperature", 0.1);
        requestBody.put("max_tokens", 300);
        requestBody.put("top_p", 1);
        requestBody.put("frequency_penalty", 0.5);
        requestBody.put("presence_penalty", 0.0);

        List<Map<String, String>> messages = new ArrayList<>();

        // 시스템 메세지
        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", "너는 유저가 보낸 웹사이트 내용을 요약해주는 모델이야. 250자 이내로 주어진 내용을 요약해줘");
        messages.add(systemMessage);

        // 유저 메세지
        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", prompt);
        messages.add(userMessage);

        requestBody.put("messages", messages);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(ENDPOINT, requestEntity, Map.class);
            Map responseBody = response.getBody();

            List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
            String content = (String) ((Map<String, Object>) choices.get(0).get("message")).get("content");

            log.info("gpt : content = {}", content);

            return content;
        } catch (RestClientException e) {
            e.printStackTrace();

//            return "Error: " + e.getMessage();
            return null;
        }
    }

}