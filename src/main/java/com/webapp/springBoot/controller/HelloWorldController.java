package com.webapp.springBoot.controller;


import org.springframework.boot.Banner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;



@Controller
public class HelloWorldController {
    @GetMapping("/")
    public String indexpage(){
        System.out.println("Прилител запрос оп оп :" + LocalDateTime.now());
        return "index";
    }

    @GetMapping("/name")
    public  String name(@RequestParam String name, Model model){
        System.out.println(model);
        model.addAttribute("name", name);
        System.out.println("Прилител запрос оп оп :" + LocalDateTime.now());
        return "template";
    }

}
