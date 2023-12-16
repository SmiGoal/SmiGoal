package smigoal.server.service;

import io.github.flashvayne.chatgpt.service.ChatgptService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GPTService {
    private final ChatgptService chatgptService;

    public String getChatResponse(String prompt){
        return chatgptService.sendMessage(prompt);
    }

    @Value("${chatgpt.api-key}")
    private String API_KEY;
    private static final String ENDPOINT = "https://api.openai.com/v1/completions";

    public String generateText(String userPrompt) {
        String prompt = userPrompt + "\n\n이 텍스트에서 주요 키워드만 json형식으로 추출해주세요.\n응답 형식 : {\"키워드1\", \"키워드2\", \"키워드3\"}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + API_KEY);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "text-davinci-003");
        requestBody.put("prompt", prompt);
        requestBody.put("temperature", 0.1);
        requestBody.put("max_tokens", 60);
        requestBody.put("top_p", 1);
        requestBody.put("frequency_penalty", 0.5);
        requestBody.put("presence_penalty", 0.0);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(ENDPOINT, requestEntity, Map.class);
            Map responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
                if (!choices.isEmpty()) {
                    // 'text' 필드만 추출하여 반환
                    Map<String, Object> firstChoice = choices.get(0);
                    if (firstChoice.containsKey("text")) {
                        String textResponse = firstChoice.get("text").toString().trim();
                        // 여기서 추가적인 문자열 처리 로직을 적용하여 'text'만을 추출
//                        return extractTextOnly(textResponse);
                        return textResponse;
                    }
                }
            }
            return "응답이 없습니다.";
        } catch (RestClientException e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

}