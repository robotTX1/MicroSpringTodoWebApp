package com.robottx.todo.controller;

import static com.robottx.todo.contant.EndpointConstants.DASHBOARD_PAGE;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping(DASHBOARD_PAGE)
    public String dashboard(Model model, OAuth2AuthenticationToken authentication) {
        String username = authentication.getPrincipal().getAttribute("family_name");
        model.addAttribute("username", username);
        return "dashboard/dashboard";
    }

}
