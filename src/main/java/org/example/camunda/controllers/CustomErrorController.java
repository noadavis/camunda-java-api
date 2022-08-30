package org.example.camunda.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.example.camunda.app.appdata.AppData;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    @Autowired
    private ApplicationContext context;

    @GetMapping("/error")
    public String error(HttpServletRequest request, Authentication authentication, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            String err = HttpStatus.valueOf(statusCode).getReasonPhrase();
            model.addAttribute("error", String.format("[ %d ] %s", statusCode, err));
        } else {
            model.addAttribute("error", "unknown error");
        }
        AppData appData = context.getBean(AppData.class, authentication, "Error", "exclamation-circle", "");
        model.addAttribute("appData", appData);
        return "base/error";
    }

}
