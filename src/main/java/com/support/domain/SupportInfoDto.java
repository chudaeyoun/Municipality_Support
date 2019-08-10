package com.support.domain;

import lombok.Data;

@Data
public class SupportInfoDto {
    private String region;           // 지자체명
    private String target;          // 지원대상
    private String usage;           // 용도
    private String limit;    // 지원한도
    private String rate;            // 이차보전
    private String institute;       // 추천기관
    private String mgmt;            // 관리점
    private String reception;       // 취급점
}
