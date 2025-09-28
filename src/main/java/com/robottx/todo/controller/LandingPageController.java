package com.robottx.todo.controller;

import static com.robottx.todo.contant.EndpointConstants.LANDING_PAGE;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LandingPageController {

    @GetMapping(value = {"/", LANDING_PAGE})
    public String landingPage() {
        return "landing/index";
    }

}
