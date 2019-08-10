package com.support.service;

import com.support.domain.Municipality;

import java.util.List;

public interface MunicipalityBiz {
    List<Municipality> getAllMunicipalityTableList();

    void insertMunicipalityList(String[] csv);

    boolean existsMunicipalityCode(String code);

    boolean existsMunicipalityRegion(String region);

    Municipality getMunicipalityRegion(String region);
}
