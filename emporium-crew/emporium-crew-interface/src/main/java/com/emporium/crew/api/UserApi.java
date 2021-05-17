package com.emporium.crew.api;

import com.emporium.crew.pojo.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

public interface UserApi {

    @GetMapping("/query")
    public User login(@RequestParam("username") String username,
                                      @RequestParam("password") String password);
}
