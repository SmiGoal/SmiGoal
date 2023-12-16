package smigoal.server.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import smigoal.server.service.CrawlingService;
import smigoal.server.service.GPTService;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MainController {

    private final GPTService chatService;
    private final CrawlingService crawlingService;

    @PostMapping("")
    public String smishingCheck(@RequestBody QuestionDTO request){
        String filterResult;
        if (request.url==null && request.message==null){
            return "error";
        }else if(request.url!=null){
            String urlContent = crawlingService.getURLContent(request.url);
            String keyward = chatService.getChatResponse(urlContent);
            filterResult = "true";  // 모델과 통신하여 필터링 결과 저장
        }else{
            String keyward = chatService.getChatResponse(request.message);
            filterResult = "true";  // 모델과 통신하여 필터링 결과 저장
        }
        return filterResult;
    }

    @Getter
    static class QuestionDTO{
        private String url;
        private String message;

        public QuestionDTO(String url, String message) {
            this.url = url;
            this.message = message;
        }
    }
}
