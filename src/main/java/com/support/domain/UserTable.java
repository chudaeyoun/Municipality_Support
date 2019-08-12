package com.support.domain;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "USER_TABLE")
@Data
public class UserTable {

    @Id
    @Column(name = "ID", nullable = false, length = 20)
    private String id;    // 아이디

    @Column(name = "PW", nullable = false, length = 20)
    private String pw;    // 패스워드

}
