package com.support.service;

import com.support.domain.Municipality;
import com.support.domain.SupportInfoDto;
import com.support.domain.SupportInfoTable;
import com.support.repository.SupportInfoRepository;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SupportInfoBizImpl implements SupportInfoBiz {

    @Autowired
    private SupportInfoRepository supportInfoRepository;

    @Override
    public List<SupportInfoDto> getAllSupportInfoList() {
        List<SupportInfoTable> supportInfoTableList = supportInfoRepository.findAll();
        List<SupportInfoDto> supportInfoDtoList = new ArrayList<>();

        for(SupportInfoTable supportInfoTable : supportInfoTableList) {
            supportInfoDtoList.add(convertDateToSupportInfoDto(supportInfoTable));
        }
        return supportInfoDtoList;
    }

    @Override
    public void insertSupportInfoTable(String[] csv) {
        SupportInfoTable supportInfoTable = new SupportInfoTable();
        Municipality municipality = new Municipality();

        municipality.setCode(csv[0]);
        municipality.setRegion(csv[1]);

        supportInfoTable.setMunicipality(municipality);
        supportInfoTable.setCode(csv[0]);
        supportInfoTable.setTarget(csv[2]);
        supportInfoTable.setUsage(csv[3]);
        supportInfoTable.setLimit(csv[4]);
        supportInfoTable.setRate(csv[5]);
        supportInfoTable.setInstitute(csv[6]);
        supportInfoTable.setMgmt(csv[7]);
        supportInfoTable.setReception(csv[8]);

        supportInfoTable.setId("chudaeyoun");

        supportInfoRepository.save(supportInfoTable);
    }

    @Override
    public void updateSunpportInfoTable(String[] csv) {
        // update 어떻게 할지 만들어야함
    }

    @Override
    public SupportInfoDto getSupportInfoByCode(String code) {
        SupportInfoTable supportInfoTable = supportInfoRepository.findByCode(code);
        return convertDateToSupportInfoDto(supportInfoTable);
    }

    @Override
    public SupportInfoDto updateSupportInfo(SupportInfoDto supportInfoDto, String code) {
        SupportInfoTable supportInfoTable = supportInfoRepository.save(convertDateToSupportInfoTable(supportInfoDto, code));
        return convertDateToSupportInfoDto(supportInfoTable);
    }

    private SupportInfoTable convertDateToSupportInfoTable(SupportInfoDto supportInfoDto, String code) {
        SupportInfoTable supportInfoTable = new SupportInfoTable();
        Municipality municipality = new Municipality();

        municipality.setCode(code);
        municipality.setRegion(supportInfoDto.getRegion());

        supportInfoTable.setMunicipality(municipality);
        supportInfoTable.setCode(code);
        supportInfoTable.setTarget(supportInfoDto.getTarget());
        supportInfoTable.setUsage(supportInfoDto.getUsage());
        supportInfoTable.setLimit(supportInfoDto.getLimit());
        supportInfoTable.setRate(supportInfoDto.getRate());
        supportInfoTable.setInstitute(supportInfoDto.getInstitute());
        supportInfoTable.setMgmt(supportInfoDto.getMgmt());
        supportInfoTable.setReception(supportInfoDto.getReception());

        supportInfoTable.setId("chudaeyoun");

        return supportInfoTable;
    }

    private SupportInfoDto convertDateToSupportInfoDto(SupportInfoTable supportInfoTable ) {
        SupportInfoDto supportInfoDto = new SupportInfoDto();

        supportInfoDto.setRegion(supportInfoTable.getMunicipality().getRegion());
        supportInfoDto.setTarget(supportInfoTable.getTarget());
        supportInfoDto.setUsage(supportInfoTable.getUsage());
        supportInfoDto.setLimit(supportInfoTable.getLimit());
        supportInfoDto.setRate(supportInfoTable.getRate());
        supportInfoDto.setInstitute(supportInfoTable.getInstitute());
        supportInfoDto.setMgmt(supportInfoTable.getMgmt());
        supportInfoDto.setReception(supportInfoTable.getReception());

        return supportInfoDto;
    }

}
