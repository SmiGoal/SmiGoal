package smigoal.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import smigoal.server.service.CrawlingService;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/crawling")
public class TestController {

    private final CrawlingService crawlingService;

    @PostMapping("")
    public String test(@RequestBody String url) throws InterruptedException {
        System.out.println(url);
        String contents = crawlingService.getURLContent(url);
        return contents;
    }
}
