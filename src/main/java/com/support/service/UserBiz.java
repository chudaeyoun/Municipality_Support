package com.support.service;


import com.support.domain.UserDto;

public interface UserBiz {
    String makeJwt(UserDto userDto);

    int checkJwt(String jwt);

    int registerUser(UserDto userDto);

}
