package com.support.domain;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "SUPPORT_INFO_TABLE")
@Data
public class SupportInfoTable extends BaseEntity {

    // 햐냐의 지자체는 하나의 지자체지원정보가 있음
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "MUNICIPALITY_CODE")
    private Municipality municipality;

    @Column(name = "ID", nullable = false, length = 20)
    private String id;    // 지자체 코드

    @Id
    @Column(name = "CODE", nullable = false, length = 100)
    private String code;    // 지자체 코드

    @Column(name = "TARGET", nullable = false, length = 200)
    private String target;    // 지원대상

    @Column(name = "USAGE", nullable = false, length = 20)
    private String usage;    // 용도

    @Column(name = "SUPPORT_LIMIT", nullable = false, length = 20)
    private String limit;    // 지원한도

    @Column(name = "RATE", nullable = false, length = 20)
    private String rate;    // 이차보전

    @Column(name = "INSTITUTE", nullable = false, length = 50)
    private String institute;    // 추천기관

    @Column(name = "MGMT", nullable = false, length = 20)
    private String mgmt;    // 관리점

    @Column(name = "RECEPTION", nullable = false)
    private String reception;    // 취급점
}
