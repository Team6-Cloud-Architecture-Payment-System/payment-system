package com.example.paymentsystem.routes;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String welcome() {
        return "welcome";
    }

    @GetMapping("/home")
    public String home() {
        return "home";
    }
}

