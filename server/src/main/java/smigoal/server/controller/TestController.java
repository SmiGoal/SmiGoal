package smigoal.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import smigoal.server.service.CrawlingService;

import java.util.HashMap;
import java.util.Map;

@Controller
@Slf4j
@RequestMapping("/s3test")
@RequiredArgsConstructor
public class TestController {

    private final CrawlingService crawlingService;

    @PostMapping("")
    public ResponseEntity<Map<String, Object>> s3(@RequestBody String url){

        log.info("url = {}", url);

        String fileUrl = crawlingService.screenShot(url);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("fileUrl", fileUrl);

        return ResponseEntity
                .ok()
                .body(responseBody);
    }
}
