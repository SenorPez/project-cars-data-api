package com.senorpez.projectcars;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping("/")
class RedirectController {
    @GetMapping("/")
    public RedirectView redirectRoot(RedirectAttributes attributes) {
        return new RedirectView("/v1");
    }
}
