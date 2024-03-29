package smigoal.server.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
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

    @PostMapping("")
    public ResponseEntity<Map<String, Object>> smishingCheck(@RequestBody QuestionDTO request) {
        List<String> keyward;

        if ((request.url==null || request.url.length == 0) && request.message==null){    // url, 문자 내용 둘 다 없는 경우
            log.info("error : url & message not available.");

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("status", "fail");
            responseBody.put("message", "url & message not available.");

            return ResponseEntity
                    .badRequest()
                    .body(responseBody);
        }else if(request.url!=null && request.url.length != 0){    // url이 있는 경우
            log.info("case 1 : url exist");

            List<String> urls = urlCheckService.getWebpageURL(request.url);
            List<String> urlContents = new ArrayList<>();

            if (urls.isEmpty()){    // 모든 url 검사결과 웹페이지 url이 없는 경우
                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("status", "fail");
                responseBody.put("message", "all url is not available.");

                return ResponseEntity
                        .ok()
                        .body(responseBody);
            }

            for (int i=0;i<urls.size();i++){
                String checkingUrl = urls.get(0);
                String urlContent = crawlingService.getURLContent(checkingUrl);

                if (urlContent == null){
                    Map<String, Object> responseBody = new HashMap<>();
                    responseBody.put("status", "fail");
                    responseBody.put("message", "url detected with no content.");

                    return ResponseEntity
                            .ok()
                            .body(responseBody);
                }

                urlContents.add(urlContent);

                keyward = chatService.generateText(urlContent);
                String detectResult = detectionFromKeywords(keyward);

                if (detectResult.equals("smishing")){   // 스미싱 검출된 경우 검출된 url에 대한 요약본과 썸네일 제공
                    log.info("smishing URL detected.");

                    String summaryContent = chatService.summarizeText(urlContent);
                    String fileUrl = crawlingService.screenShot(checkingUrl);

                    Map<String, Object> responseBody = new HashMap<>();
                    responseBody.put("status", "success");
                    responseBody.put("message", "Detection complete.");
                    responseBody.put("result", "smishing");
                    responseBody.put("summarize", summaryContent);
                    responseBody.put("thumbnail", fileUrl);

                    return ResponseEntity
                            .ok()
                            .body(responseBody);
                }
            }
            String summaryContent = chatService.summarizeText(urlContents.get(0));
            String fileUrl = crawlingService.screenShot(urlContents.get(0));

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("status", "success");
            responseBody.put("message", "Detection complete.");
            responseBody.put("result", "ham");
            responseBody.put("summarize", summaryContent);
            responseBody.put("thumbnail", fileUrl);

            return ResponseEntity
                    .ok()
                    .body(responseBody);
        }else{  // 문자 내용만 있는 경우
            log.info("case 2 : url does not exist");
            keyward = chatService.generateText(request.message);
            String result = detectionFromKeywords(keyward);

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("status", "success");
            responseBody.put("message", "Detection complete.");
            responseBody.put("result", result);
            return ResponseEntity
                    .ok()
                    .body(responseBody);
        }
    }

    private String detectionFromKeywords(List<String> keyward) {
        if (keyward.size() <= 1){ // 키워드 추출 실패 - 스미싱으로 간주
            log.info("error : keyward does not exist.");
            return "smishing";
        }

        // 키워드 추출 확인
        log.info("checking keyword");
        for (int i = 0; i< keyward.size(); i++){
            log.info("keyward {} = {}", i, keyward.get(i));
        }

        // 모델 통신
        return modelService.callFlaskService(keyward);
    }

    @Getter
    static class QuestionDTO {
        private String[] url;
        private String message;

        public QuestionDTO() {
        }

        public QuestionDTO(String[] url, String message) {
            this.url = url;
            this.message = message;
        }
    }
}
