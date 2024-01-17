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
//        System.out.println(request.url);
//        System.out.println(request.message);
        String filterResult;
        List<String> keyward;

        if (request.url==null && request.message==null){
            return "error";
        }else if(request.url!=null){
            System.out.println("case 2---------------------------");
            String urlContent = crawlingService.getURLContent(request.url);
            System.out.println(urlContent);
            if (urlContent.length()>1000)
                urlContent = urlContent.substring(0,1000);
            keyward = chatService.generateText(urlContent);  // 조절 필요
        }else{
            System.out.println("case 3---------------------------");
            keyward = chatService.generateText(request.message);
        }

        if (keyward==null){ // 키워드 추출 실패
            return "false";
        }

        // 키워드 추출 확인
        for (int i=0;i<keyward.size();i++){
            System.out.println(keyward.get(i));
        }

        /**
         * 모델 통신 코드
         * 모델과 통신하여 filterResult에 필터링 결과 저장
         */
        filterResult = "true";  // 임시 필터링 결과 저장

        return filterResult;
    }

    @PostMapping("/flask")
    public String testflask(@RequestBody String requestData){
        return modelService.callFlaskService(requestData);
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
