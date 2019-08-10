package com.support.service;

import com.support.domain.Municipality;
import com.support.domain.SupportInfoDto;
import com.support.domain.SupportInfoTable;
import com.support.repository.SupportInfoRepository;
import lombok.Data;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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

    @Data
    class SortInfo {
        String institute;
        Double rate;

        SortInfo(String institute, Double rate) {
            this.institute = institute;
            this.rate = rate;
        }
    }

    @Override
    public List<String> searchInstituteByMinRate() {
        List<SupportInfoTable> supportInfoTableList = supportInfoRepository.findAll();
        List<SortInfo> sortInfoList = new ArrayList<>();
        List<String> instituteMinRateList = new ArrayList<>();

        for(SupportInfoTable supportInfoTable : supportInfoTableList) {
            String rate = supportInfoTable.getRate();

            if(rate.equals("대출이자 전액")) {
                rate = "100.0";
            } else {
                String splitArr[] = rate.split("~");

                if(splitArr.length == 1) {
                    splitArr[0] = splitArr[0].replaceAll("%","");
                } else if(splitArr.length == 2) {
                    splitArr[0] = splitArr[0].replaceAll("%","");
                    splitArr[1] = splitArr[1].replaceAll("%","");
                    BigDecimal numA = new BigDecimal(splitArr[0]);
                    BigDecimal numB = new BigDecimal(splitArr[1]);

                    splitArr[0] = numA.add(numB).divide(new BigDecimal(2), 3, BigDecimal.ROUND_HALF_UP).toString();
                }
                rate = splitArr[0];
            }
            //supportInfoTable.setRate(rate);
            sortInfoList.add(new SortInfo(supportInfoTable.getInstitute(), Double.parseDouble(rate)));
        }

        sortInfoList = sortInfoList.stream().sorted(Comparator.comparing((SortInfo::getRate)))
                                    .collect(Collectors.toList());

        // 최소가 여러 개 있을 수도 있으니 리스트로 반환
        double minRate = sortInfoList.get(0).getRate();
        for(SortInfo sortInfo : sortInfoList) {
            if(minRate == sortInfo.getRate()) {
                instituteMinRateList.add(sortInfo.getInstitute());
            } else{
                break;
            }
        }

        return instituteMinRateList;
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
