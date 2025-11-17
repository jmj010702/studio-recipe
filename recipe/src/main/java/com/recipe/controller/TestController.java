package com.recipe.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
public class TestController {

    @GetMapping("/test/{count}")
    public ResponseEntity<Integer> test(@PathVariable("count") Integer count) {
        log.info("리액트에서 요청이 들어왔습니다. Count = {}", count);
        return ResponseEntity.ok(count);
    }
}
