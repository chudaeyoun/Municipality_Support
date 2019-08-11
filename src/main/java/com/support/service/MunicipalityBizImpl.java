package com.support.service;

import com.support.domain.Municipality;
import com.support.repository.MunicipalityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MunicipalityBizImpl implements MunicipalityBiz {

    @Autowired
    private MunicipalityRepository municipalityRepository;

    @Override
    public boolean existsMunicipalityCode(String code) {
        return municipalityRepository.existsByCode(code);
    }

    @Override
    public boolean existsMunicipalityRegion(String region) {
        return municipalityRepository.existsByRegion(region);
    }

    @Override
    public Municipality getMunicipalityRegion(String region) {
        return municipalityRepository.findByRegion(region);
    }
}
