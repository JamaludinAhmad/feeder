package com.example.feeder_importer.controller;

import com.example.feeder_importer.config.Akun;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/login")
@Controller
public class LoginController {

    @Autowired
    Akun akun;

    @GetMapping
    public String index(){
        return "login";
    }

    @PostMapping
    public String login(@RequestParam String email,
                         @RequestParam String password) throws JsonProcessingException {

        System.out.println(email + " " + password);
        akun.setUsername(email);
        akun.setPass(password);

        akun.login();
        if(!akun.isLogin()){
            return "redirect:login";

        }

        return "redirect:/";

    }
}
