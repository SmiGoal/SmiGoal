package smigoal.server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.*;

@Service
@RequiredArgsConstructor
public class GPTService {
    private final ChatgptService chatgptService;

    public String getChatResponse(String prompt){
        return chatgptService.sendMessage(prompt);
    }

    @Value("${chatgpt.api-key}")
    private String API_KEY;
    private static final String ENDPOINT = "https://api.openai.com/v1/chat/completions";

    public List<String> generateText(String userPrompt) {
        String prompt = userPrompt.replace("\n", " ").trim();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + API_KEY);

        Map<String, Object> requestBody = new HashMap<>();
//        requestBody.put("model", "gpt-4-1106-preview");
        // 비용문제로 최종단계에서만 gpt4 사용
        requestBody.put("model", "gpt-3.5-turbo-1106");
        requestBody.put("temperature", 0.1);
        requestBody.put("max_tokens", 30);
        requestBody.put("top_p", 1);
        requestBody.put("frequency_penalty", 0.5);
        requestBody.put("presence_penalty", 0.0);

        List<Map<String, String>> messages = new ArrayList<>();

        // 시스템 메세지
        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", "너는 유저의 질문에서 주요 키워드들만 리스트 형식으로 추출해주는 모델이야. 예시 응답 형식 : {키워드1, 키워드2, 키워드3}, 가장 중요한 키워드 순서대로 추출해줘.");
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

            System.out.println(content);
            // content 예시
            // {예산안, 국회, 윤석열, 대통령, 민생, 한덕수, 국무총리, 거시지표, 국정원장, 조태용, 한동훈, 법무부, 개각, 연합뉴스TV, 조한대}
            // max_tokens 값 설정 시 짤릴 수 있음
            // {윤석열, 대통령, 민생, 예산안, 국회, 한

            // 결과값 다듬기
            content = content.replace("{", "").replace("}","");
            StringTokenizer st = new StringTokenizer(content, ",");

            List<String> result = new ArrayList<>();
            while (st.hasMoreTokens()){
                result.add(st.nextToken().trim());
            }

            if(!result.isEmpty()){  // 마지막 키워드는 짤릴 수 있으므로 제거
                result.remove(result.size()-1);
            }

            return result;
        } catch (RestClientException e) {
            e.printStackTrace();

//            return "Error: " + e.getMessage();
            return null;
        }
    }

}