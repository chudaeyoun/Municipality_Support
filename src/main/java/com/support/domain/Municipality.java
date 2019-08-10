package com.support.domain;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "MUNICIPALITY_TABLE")
@Data
public class Municipality {

    @Id
    @Column(name = "CODE", nullable = false, length = 100)
    private String code;    // 지자체 코드

    @Column(name = "REGION", nullable = false, length = 20)
    private String region;    // 지자체명

}
