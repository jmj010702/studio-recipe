package com.recipe.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

//Prd Test 용
@RestController
public class AWSTestController {
    @GetMapping("/aws-test")
    public String awsTest(){
        return "AWS TEST COMPLETE";
    }
}
