package com.support.service;

import com.support.domain.SupportInfoDto;
import com.support.domain.SupportInfoTable;

import java.util.List;

public interface SupportInfoBiz {
    List<SupportInfoDto> getAllSupportInfoList(); // 지원하는 지자체 목록 검색(all)

    SupportInfoTable insertSupportInfoTable(String[] csv);

    SupportInfoDto getSupportInfoByCode(String code);

    SupportInfoDto updateSupportInfo(SupportInfoDto supportInfoDto, String code);

    List<String> searchInstituteByMinRate();

    List<String> searchRegionLimitDescByCnt(int cnt);

    SupportInfoTable saveSupportInfo(SupportInfoTable supportInfoTable);

    SupportInfoDto recommendSupportInfo(String input);
}
