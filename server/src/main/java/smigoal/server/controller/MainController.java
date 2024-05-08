package smigoal.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import smigoal.server.dto.ModelResponseDto;
import smigoal.server.service.CrawlingService;
import smigoal.server.service.GPTService;
import smigoal.server.service.ModelService;
import smigoal.server.service.URLCheckService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MainController {

    private final GPTService chatService;
    private final CrawlingService crawlingService;
    private final ModelService modelService;
    private final URLCheckService urlCheckService;

    @Operation(summary = "URL 스미싱 탐지 API", description = "URL에 대한 스미싱 탐지 진행 후 결과 반환", tags = {"Main"})
    @PostMapping("/url")
    public ResponseEntity<Map<String, Object>> urlCheck(@RequestBody QuestionDTO request) {
        if (request.url==null || request.url.length == 0){
            log.info("error : url not available.");

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("status", "fail");
            responseBody.put("code", 420);
            responseBody.put("message", "url not available.");

            return ResponseEntity
                    .badRequest()
                    .body(responseBody);
        }else {    // url이 있는 경우
            log.info("case 1 : url exist");

            List<String> urls = urlCheckService.getWebpageURL(request.url);
            List<String> urlContents = new ArrayList<>();

            if (urls.isEmpty()){    // 모든 url 검사결과 웹페이지 url이 없는 경우
                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("status", "fail");
                responseBody.put("code", 421);
                responseBody.put("message", "all url is not available.");

                return ResponseEntity
                        .ok()
                        .body(responseBody);
            }

            ModelResponseDto firstResult = new ModelResponseDto("ham");

            for (int i=0;i<urls.size();i++){
                String checkingUrl = urls.get(0);
                String urlContent = crawlingService.getURLContent(checkingUrl);

                if (urlContent == null){
                    Map<String, Object> responseBody = new HashMap<>();
                    responseBody.put("status", "fail");
                    responseBody.put("code", 422);
                    responseBody.put("message", "url detected with no content.");

                    return ResponseEntity
                            .ok()
                            .body(responseBody);
                }

                urlContents.add(urlContent);

                ModelResponseDto detectResult = modelService.callFlaskService(urlContent);

                if (i == 0) {
                    firstResult = detectResult;
                }

                if (detectResult.getResult().equals("smishing")){   // 스미싱 검출된 경우 검출된 url에 대한 요약본과 썸네일 제공
                    log.info("smishing URL detected.");

                    String summaryContent = chatService.summarizeText(urlContent);
                    String fileUrl = crawlingService.screenShot(checkingUrl);

                    Map<String, Object> responseBody = new HashMap<>();
                    responseBody.put("status", "success");
                    responseBody.put("code", 200);
                    responseBody.put("message", "Detection complete.");
                    responseBody.put("result", detectResult);
                    responseBody.put("summarize", summaryContent);
                    responseBody.put("thumbnail", fileUrl);

                    return ResponseEntity
                            .ok()
                            .body(responseBody);
                }
            }
            String summaryContent = chatService.summarizeText(urlContents.get(0));
            String fileUrl = crawlingService.screenShot(urls.get(0));

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("status", "success");
            responseBody.put("code", 200);
            responseBody.put("message", "Detection complete.");
            responseBody.put("result", firstResult);
            responseBody.put("summarize", summaryContent);
            responseBody.put("thumbnail", fileUrl);

            return ResponseEntity
                    .ok()
                    .body(responseBody);
        }
    }

    @Operation(summary = "문자 스미싱 탐지 API", description = "문자 내용 대한 스미싱 탐지 진행 후 결과 반환", tags = {"Main"})
    @PostMapping("/message")
    public ResponseEntity<Map<String, Object>> messageCheck(@RequestBody String message){
        if (message == null || message.length() == 0) {
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("status", "fail");
            responseBody.put("code", 430);
            responseBody.put("message", "message not available.");

            return ResponseEntity
                    .ok()
                    .body(responseBody);
        } else {
            ModelResponseDto detectResult = modelService.callFlaskService(message);

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("status", "success");
            responseBody.put("code", 200);
            responseBody.put("message", "Detection complete.");
            responseBody.put("result", detectResult);

            return ResponseEntity
                    .ok()
                    .body(responseBody);
        }
    }

    @Getter
    static class QuestionDTO {
        private String[] url;

        public QuestionDTO() {
        }

        public QuestionDTO(String[] url) {
            this.url = url;
        }
    }
}
