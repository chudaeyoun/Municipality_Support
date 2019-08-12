package com.support.domain;

import lombok.Data;

@Data
public class UserDto {
    private String id;
    private String pw;
    private String jwt;
}