package com.webapp.springBoot.controllers;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;



@Controller
@RequestMapping("/registr")
public class HelloWorldController {


    @GetMapping("/")
    public  String name(@RequestParam(required = false, defaultValue = "Гость") String name, @RequestHeader("user-agent") String userAgent,  Model model){
        System.out.println(userAgent);
        model.addAttribute("name", name);
        return "hwController/template";
    }

    @PostMapping("/json")
    @ResponseBody
    public ObjectJson jsonGet(@RequestBody  ObjectJson objectJson){
        return objectJson;
    }


    @GetMapping("/redirect")
    public String redirectForPage(@RequestParam String name, @RequestParam(defaultValue = "12:12:12 2025-03-05") String date, RedirectAttributes redirectAttributes){
        LocalDateTime dateTime = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("HH:mm:ss yyyy-MM-dd"));
        System.out.println(dateTime.plusDays(1));
        redirectAttributes.addAttribute("name", name);
        return "redirect:";
    }
    @GetMapping("/calculator")
    public String calculator(@RequestParam(defaultValue = "0") long a, @RequestParam(defaultValue = "0") long b, @RequestParam(defaultValue = "+") char ch, Model model){

        model.addAttribute("a", a);
        model.addAttribute("b", b);
        model.addAttribute("ch", ch);
        try{
            double result = calculator(a, b, ch);
            model.addAttribute("result", result);
        } catch (IllegalArgumentException | ArithmeticException e){
            model.addAttribute("error", e.getMessage());
        }
        return "hwController/calculator";
    }
    private double calculator(long a, long b, char ch){
        double result;
        switch (ch) {
            case '+' -> result = a + b;
            case '-' -> result = a - b;
            case '*' -> result = a * b;
            case '/' -> {
                if (b == 0){
                    throw new ArithmeticException("Деление на ноль заперещено");
                }
                result = a / b;
            }
            default -> throw new IllegalArgumentException("Знак не корректен");
        }
        return result;
    }

}
