package com.terheyden.valid;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * HelloController class.
 */
@RequestMapping("/")
@RestController
public class HelloController {

    /**
     * By adding @Valid, Spring will validate the request body, and if it fails, it will throw a 400 Bad Request.
     */
    @PostMapping
    public String greet(@RequestBody @Valid GreetingDto greetingDto) {
        return "%s %s!".formatted(greetingDto.getSalutation(), greetingDto.getName());
    }
}
