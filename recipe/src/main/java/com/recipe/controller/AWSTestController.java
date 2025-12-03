package com.recipe.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

//Prd Test 용
@RestController
@RequestMapping("/aws-test")
public class AWSTestController {
    @GetMapping("/hello")
    public String awsTest(){
        return "AWS TEST COMPLETE";
    }
}
