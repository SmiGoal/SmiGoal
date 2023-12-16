package smigoal.server.controller;

import io.github.flashvayne.chatgpt.service.ChatgptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import smigoal.server.service.GPTService;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/chat-gpt")
public class GPTController {

    private final GPTService chatService;

    @PostMapping("")
    public String test(@RequestBody String question){
//        return chatService.getChatResponse(question);
        return chatService.generateText(question);
    }

//    @PostMapping("/getmodels")
//    public String test2(){
//        return chatService.generateText();
//    }
}
