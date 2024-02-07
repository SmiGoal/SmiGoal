package smigoal.server.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import smigoal.server.service.CrawlingService;
import smigoal.server.service.GPTService;
import smigoal.server.service.ModelService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MainController {

    private final GPTService chatService;
    private final CrawlingService crawlingService;
    private final ModelService modelService;

    @PostMapping("")
    public String smishingCheck(@RequestBody QuestionDTO request) throws InterruptedException {
        List<String> keyward;

        if (request.url==null && request.message==null){
            return "error";
        }else if(request.url!=null){
            System.out.println("case 2---------------------------");
            String urlContent = crawlingService.getURLContent(request.url);
            if (urlContent==null){
                return "smimshing";
            }

            int length = urlContent.length();
            System.out.println(urlContent);
            System.out.println(length);
            if (length>1000)
                urlContent = urlContent.substring(length-1000,length);
            keyward = chatService.generateText(urlContent);  // 조절 필요
        }else{
            System.out.println("case 3---------------------------");
            keyward = chatService.generateText(request.message);
        }

        if (keyward==null){ // 키워드 추출 실패 - 스미싱으로 간주
            return "smishing";
        }

        // 키워드 추출 확인
        for (int i=0;i<keyward.size();i++){
            System.out.println(keyward.get(i));
        }

        // 모델 통신
        String filterResult = modelService.callFlaskService(keyward);


        return filterResult;
    }

    @Getter
    static class QuestionDTO {
        private String url;
        private String message;

        public QuestionDTO() {
        }

        public QuestionDTO(String url, String message) {
            this.url = url;
            this.message = message;
        }
    }
}
