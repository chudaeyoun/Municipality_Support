package com.support.service;

import com.support.domain.Municipality;

public interface MunicipalityBiz {
    boolean existsMunicipalityCode(String code);

    boolean existsMunicipalityRegion(String region);

    Municipality getMunicipalityRegion(String region);
}
