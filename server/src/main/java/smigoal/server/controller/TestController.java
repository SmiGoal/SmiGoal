package smigoal.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import smigoal.server.service.CrawlingService;

@RestController
@Slf4j
@RequiredArgsConstructor
public class TestController {

    private final CrawlingService crawlingService;

    @PostMapping("")
    public String test(@RequestBody String url){
        String contents = crawlingService.getURLContent(url);
        return contents;
    }
}