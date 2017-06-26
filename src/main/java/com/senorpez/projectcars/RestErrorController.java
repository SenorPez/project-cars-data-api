package com.senorpez.projectcars;

import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RequestMapping(
        value = "/error",
        method = {RequestMethod.GET},
        produces = {"application/vnd.senorpez.pcars.v1+json; charset=UTF-8", "application/json; charset=UTF-8"}
)
@RestController
class RestErrorController implements ErrorController {
    private static final String PATH = "/error";

    @RequestMapping
    public void handle(HttpServletRequest request) {
        throw new PageNotFoundAPIException();
    }

    @Override
    public String getErrorPath() {
        return PATH;
    }
}
