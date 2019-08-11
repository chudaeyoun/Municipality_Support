package com.support.service;

import com.support.domain.Municipality;
import com.support.domain.SortInfoDto;
import com.support.domain.SupportInfoDto;
import com.support.domain.SupportInfoTable;
import com.support.repository.SupportInfoRepository;
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

        for (SupportInfoTable supportInfoTable : supportInfoTableList) {
            supportInfoDtoList.add(convertDateToSupportInfoDto(supportInfoTable));
        }
        return supportInfoDtoList;
    }

    @Override
    public SupportInfoTable insertSupportInfoTable(String[] csv) {
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

        return supportInfoTable;
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

    @Override
    public List<String> searchInstituteByMinRate() {
        List<SupportInfoTable> supportInfoTableList = supportInfoRepository.findAll();
        List<SortInfoDto> sortInfoDtoList = new ArrayList<>();
        List<String> instituteList = new ArrayList<>();

        if (supportInfoTableList.isEmpty()) {
            return instituteList;
        }

        for (SupportInfoTable supportInfoTable : supportInfoTableList) {
            String rate = calRate(supportInfoTable.getRate());
            SortInfoDto sortInfoDto = new SortInfoDto();

            sortInfoDto.setInstitute(supportInfoTable.getInstitute());
            sortInfoDto.setRate(Double.parseDouble(rate));

            sortInfoDtoList.add(sortInfoDto);
        }

        sortInfoDtoList = sortInfoDtoList.stream().sorted(Comparator.comparing((SortInfoDto::getRate)))
                .collect(Collectors.toList());

        // 최소가 여러 개 있을 수도 있으니 리스트로 반환
        double minRate = sortInfoDtoList.get(0).getRate();
        for (SortInfoDto sortInfoDto : sortInfoDtoList) {
            if (minRate == sortInfoDto.getRate()) {
                instituteList.add(sortInfoDto.getInstitute());
            } else {
                break;
            }
        }

        return instituteList;
    }

    @Override
    public List<String> searchRegionLimitDescByCnt(int cnt) {
        List<SupportInfoTable> supportInfoTableList = supportInfoRepository.findAll();
        List<SortInfoDto> sortInfoDtoList = new ArrayList<>();
        List<String> instituteList = new ArrayList<>();

        if (supportInfoTableList.isEmpty()) {
            return instituteList;
        }

        for (SupportInfoTable supportInfoTable : supportInfoTableList) {
            String limit = calLimit(supportInfoTable.getLimit());
            String rate = calRate(supportInfoTable.getRate());
            SortInfoDto sortInfoDto = new SortInfoDto();

            sortInfoDto.setRegion(supportInfoTable.getMunicipality().getRegion());
            sortInfoDto.setInstitute(supportInfoTable.getInstitute());
            sortInfoDto.setRate(Double.parseDouble(rate));
            sortInfoDto.setLimit(Long.parseLong(limit));

            sortInfoDtoList.add(sortInfoDto);
        }

        sortInfoDtoList = sortInfoDtoList.stream().sorted(Comparator.comparing(SortInfoDto::getLimit).reversed()
                .thenComparing(SortInfoDto::getRate))
                .collect(Collectors.toList());

        cnt = Math.min(cnt, sortInfoDtoList.size());

        for (SortInfoDto sortInfo : sortInfoDtoList) {
            instituteList.add(sortInfo.getInstitute());
            cnt--;
            if (cnt == 0) {
                break;
            }
        }

        return instituteList;
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

    private SupportInfoDto convertDateToSupportInfoDto(SupportInfoTable supportInfoTable) {
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

    private String calRate(String rate) {
        if (rate.equals("대출이자 전액")) {
            rate = "100.0";
        } else {
            String splitRate[] = rate.replaceAll("%", "").split("~");

            if (splitRate.length == 2) {
                BigDecimal numA = new BigDecimal(splitRate[0]);
                BigDecimal numB = new BigDecimal(splitRate[1]);
                splitRate[0] = numA.add(numB).divide(new BigDecimal(2), 3, BigDecimal.ROUND_HALF_UP).toString();
            }
            rate = splitRate[0];
        }
        return rate;
    }

    private String calLimit(String limit) {
        String splitLimit = limit.split(" ")[0];
        String num = splitLimit.replaceAll("[^0-9]", "");
        String unit = splitLimit.replaceAll("[0-9]", "");

        switch (unit) {
            case "백만원":
                num = num + "000000";
                break;
            case "천만원":
                num = num + "0000000";
                break;
            case "억원":
                num = num + "00000000";
                break;
            default:
                num = "0";
                break;
        }
        return num;
    }

}
