package com.support.service;

import com.support.domain.Municipality;
import com.support.repository.MunicipalityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MunicipalityBizImpl implements MunicipalityBiz {

    @Autowired
    private MunicipalityRepository municipalityRepository;

    @Override
    public List<Municipality> getAllMunicipalityTableList() {
        return null;//municipalityRepository.findAll();
    }

    @Override
    public void insertMunicipalityList(String[] csv) {
        if(!municipalityRepository.existsByRegion(csv[1])) {
            Municipality municipality = new Municipality();

            municipality.setCode(csv[0]);
            municipality.setRegion(csv[1]);

            municipalityRepository.save(municipality);
        }
    }

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
