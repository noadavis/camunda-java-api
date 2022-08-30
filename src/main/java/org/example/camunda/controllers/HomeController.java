package org.example.camunda.controllers;

import org.example.camunda.app.user.CustomUserDetailService;
import org.example.camunda.helpers.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

@Controller
@RequestMapping("")
public class HomeController {

    @Autowired
    ApplicationContext context;
    @Autowired
    CustomUserDetailService userDetailService;

    @PostMapping(value = "/auth/create_user", produces="application/json")
    @ResponseBody
    public HashMap<String, Object> create_user(
            @RequestParam(value = "username", defaultValue = "") String username,
            @RequestParam(value = "password", defaultValue = "") String password) {
        String desc = "";
        boolean error = true;
        if (username.length() > 2 && password.length() > 2) {
            if (userDetailService.checkUsername(username)) {
                userDetailService.createUser(username, password);
                error = false;
            } else desc = "User already exist";
        } else desc = "Username or password length error";
        return Utils.getAnswer(error ? "": "ok", error, desc);
    }

    @GetMapping("/auth/register")
    public String register() {
        return "base/register";
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/task";
    }

    @GetMapping("/auth/logout")
    public String logout(HttpServletRequest request) {
        SecurityContextHolder.clearContext();
        for (Cookie cookie : request.getCookies()) {
            cookie.setMaxAge(0);
        }
        return "redirect:/auth/login";
    }
}