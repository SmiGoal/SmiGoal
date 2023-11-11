package smigoal.server.service;

import io.github.flashvayne.chatgpt.service.ChatgptService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatgptService chatgptService;

    public String getChatResponse(String prompt){
        return chatgptService.sendMessage(prompt);
    }

    @Value("${chatgpt.api-key}")
    private String API_KEY;
//    private static final String ENDPOINT = "https://api.openai.com/v1/completions";
    private static final String ENDPOINT = "https://api.openai.com/v1/models";

    public String generateText() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + API_KEY);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model","text-davinci-003");
//        requestBody.put("prompt", prompt);
//        requestBody.put("temperature", temperature);
//        requestBody.put("max_tokens", maxTokens);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.postForEntity(ENDPOINT, requestEntity, Map.class);
        return response.toString();
    }
}