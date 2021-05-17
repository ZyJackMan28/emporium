package com.emporium.crew.service;

import com.emporium.crew.pojo.User;

public interface UserService {
    Boolean checkUserData(String data, Integer type);

    void sendCode(String phone);

    void register(User user, String code);

    User login(String username, String password);
}
