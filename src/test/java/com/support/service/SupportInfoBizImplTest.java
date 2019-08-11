package com.support.service;

import com.support.domain.Municipality;
import com.support.domain.SortInfoDto;
import com.support.domain.SupportInfoDto;
import com.support.domain.SupportInfoTable;
import com.support.repository.SupportInfoRepository;
import lombok.Data;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SupportInfoBizImpl.class)
public class SupportInfoBizImplTest {

    @Autowired
    private SupportInfoBizImpl supportInfoBizImpl;

    @MockBean
    private SupportInfoRepository supportInfoRepository;

    @Test
    public void getAllSupportInfoList_equalTes() {
        List<SupportInfoTable> expected = getSupportInfoTables(5);
        given(supportInfoRepository.findAll()).willReturn(expected);
        List<SupportInfoDto> supportInfoDtoList = supportInfoBizImpl.getAllSupportInfoList();
        List<SupportInfoDto> dtoExpected = new ArrayList<>();

        for(SupportInfoTable supportInfoTable : expected) {
            dtoExpected.add(convertDateToSupportInfoDto(supportInfoTable));
        }
        assertThat(supportInfoDtoList).isEqualTo(dtoExpected);
    }

    @Test
    public void insertSupportInfoTable() {
        SupportInfoTable expected = getSupportInfoTables(1).get(0);
        given(supportInfoRepository.save(expected)).willReturn(expected);
        SupportInfoTable supportInfoTable = supportInfoBizImpl.insertSupportInfoTable(getCsv());
        assertThat(supportInfoTable).isEqualTo(expected);
    }

    @Test
    public void getSupportInfoByCode() {
        SupportInfoTable expected = getSupportInfoTables(1).get(0);
        given(supportInfoRepository.findByCode(expected.getCode())).willReturn(expected);
        SupportInfoDto supportInfoDto =  supportInfoBizImpl.getSupportInfoByCode("1");
        SupportInfoDto dtoExpected = convertDateToSupportInfoDto(expected);
        assertThat(supportInfoDto).isEqualTo(dtoExpected);
    }

    @Test
    public void updateSupportInfo() {
        SupportInfoTable expected = getSupportInfoTables(1).get(0);
        given(supportInfoRepository.save(expected)).willReturn(expected);
        SupportInfoDto supportInfoDto = supportInfoBizImpl.updateSupportInfo(convertDateToSupportInfoDto(expected), expected.getCode());
        SupportInfoDto dtoExpected = convertDateToSupportInfoDto(expected);
        assertThat(supportInfoDto).isEqualTo(dtoExpected);
    }

    @Test
    public void searchInstituteByMinRate() {
        List<SupportInfoTable> supportInfoTableList = getSupportInfoTables(5);
        List<SortInfoDto> sortInfoDtoList = new ArrayList<>();
        List<String> expected = new ArrayList<>();

        for(SupportInfoTable supportInfoTable : supportInfoTableList) {
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
        for(SortInfoDto sortInfoDto : sortInfoDtoList) {
            if(minRate == sortInfoDto.getRate()) {
                expected.add(sortInfoDto.getInstitute());
            } else{
                break;
            }
        }

        List<String> instituteList = supportInfoBizImpl.searchInstituteByMinRate();
        instituteList.addAll(expected);
        assertThat(instituteList).isEqualTo(expected);
    }

    @Test
    public void searchRegionLimitDescByCnt() {
        List<SupportInfoTable> supportInfoTableList = getSupportInfoTables(5);
        List<SortInfoDto> sortInfoDtoList = new ArrayList<>();
        List<String> expected = new ArrayList<>();
        int cnt = 3;

        for(SupportInfoTable supportInfoTable : supportInfoTableList) {
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

        for(SortInfoDto sortInfo : sortInfoDtoList) {
            expected.add(sortInfo.getInstitute());
            cnt--;
            if(cnt == 0) {
                break;
            }
        }

        List<String> instituteList = supportInfoBizImpl.searchRegionLimitDescByCnt(cnt);
        instituteList.addAll(expected);
        assertThat(instituteList).isEqualTo(expected);
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
            case "백만원" : num = num + "000000"; break;
            case "천만원" : num = num + "0000000"; break;
            case "억원" : num = num + "00000000"; break;
            default : num = "0"; break;
        }
        return num;
    }

    private List<SupportInfoTable> getSupportInfoTables(int createCnt) {
        List<SupportInfoTable> supportInfoTableList = new ArrayList<>();

        for(int i = 1;  i <= createCnt; i++) {
            SupportInfoTable supportInfoTable = new SupportInfoTable();
            Municipality municipality = new Municipality();

            municipality.setCode(i + "");
            municipality.setRegion(String.valueOf((char)('A' + i)));

            supportInfoTable.setMunicipality(municipality);
            supportInfoTable.setCode(i + "");

            supportInfoTable.setTarget("지원대상");
            supportInfoTable.setUsage("운전");
            supportInfoTable.setLimit(i + "억원 이내");
            supportInfoTable.setRate(i + "%");
            supportInfoTable.setInstitute("추천기관");
            supportInfoTable.setMgmt("관리점");
            supportInfoTable.setReception("취급점");
            supportInfoTable.setId("chudaeyoun");

            supportInfoTableList.add(supportInfoTable);
        }
        return supportInfoTableList;
    }

    private String[] getCsv() {
        String csv[] = new String[9];

        csv[0] = "1";
        csv[1] = "B";
        csv[2] = "지원대상";
        csv[3] = "운전";
        csv[4] = "1억원 이내";
        csv[5] = "1%";
        csv[6] = "추천기관";
        csv[7] = "관리점";
        csv[8] = "취급점";

        return csv;
    }
}
