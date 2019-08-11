package com.support.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.support.domain.Municipality;
import com.support.domain.SupportInfoDto;
import com.support.domain.SupportInfoTable;
import com.support.repository.MunicipalityRepository;
import com.support.repository.SupportInfoRepository;
import com.support.service.MunicipalityBiz;
import com.support.service.SupportInfoBiz;
import com.support.util.BizException;
import com.support.util.CvsUtil;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(SupportInfoApiController.class)
public class SupportInfoApiControllerTest {

    private static String UPLOADED_FOLDER = "file";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private SupportInfoBiz supportInfoBiz;

    @MockBean
    private MunicipalityBiz municipalityBiz;

    @MockBean
    private SupportInfoRepository supportInfoRepository;

    @MockBean
    private MunicipalityRepository municipalityRepository;

    @Test
    public void singleFileUpload() throws Exception {
        // given
        SupportInfoTable supportInfoTable = supportInfoBiz.insertSupportInfoTable(getCsv());
        given(supportInfoBiz.insertSupportInfoTable(getCsv())).willReturn(supportInfoTable);

        JsonObject supportInfoDto = new JsonObject();
        supportInfoDto.addProperty("region", "B");
        supportInfoDto.addProperty("target", "지원대상");
        supportInfoDto.addProperty("usage", "운전");
        supportInfoDto.addProperty("limit", "1억원 이내");
        supportInfoDto.addProperty("rate", "1%");
        supportInfoDto.addProperty("institute", "추천기관");
        supportInfoDto.addProperty("mgmt", "관리점");
        supportInfoDto.addProperty("reception", "취급점");

        mvc.perform(
                post("/api/supportInfo/uploadCsvFile")
                        .contentType("application/json")
                        .content(supportInfoDto.toString())).andExpect(status().isOk());
    }

    @Test
    public void getAllSupportInfoList_OK_TEST() throws Exception {
        // given
        List<SupportInfoTable> supportInfoTableList = getSupportInfoTables(5);
        List<SupportInfoDto> supportInfoDtoList = Lists.newArrayList();
        for (SupportInfoTable supportInfoTable : supportInfoTableList) {
            supportInfoDtoList.add(convertDateToSupportInfoDto(supportInfoTable));
        }
        given(supportInfoBiz.getAllSupportInfoList()).willReturn(supportInfoDtoList);

        // when
        MockHttpServletResponse response = mvc.perform(
                get("/api/supportInfo/list")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    public void getAllSupportInfoList_NO_CONTENT_TEST() throws Exception {
        // given
        given(supportInfoBiz.getAllSupportInfoList()).willReturn(Lists.newArrayList());

        // when
        MockHttpServletResponse response = mvc.perform(
                get("/api/supportInfo/list")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    public void getSupportInfo_OK_TEST() throws Exception {
        // given
        SupportInfoTable supportInfoTable = getSupportInfoTables(1).get(0);
        SupportInfoDto supportInfoDto = convertDateToSupportInfoDto(supportInfoTable);
        supportInfoRepository.save(supportInfoTable);
        given(supportInfoBiz.getSupportInfoByCode(supportInfoTable.getCode())).willReturn(supportInfoDto);

        JsonObject municipality = new JsonObject();
        municipality.addProperty("code", supportInfoTable.getMunicipality().getCode());
        municipality.addProperty("region", supportInfoTable.getMunicipality().getRegion());

        // when
        MockHttpServletResponse response = mvc.perform(
                get("/api/supportInfo/searchRegion")
                        .contentType("application/json")
                        .content(municipality.toString()))
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    public void getSupportInfo_NO_CONTENT_TEST() throws Exception {
        // given
        given(supportInfoBiz.getSupportInfoByCode("1234")).willReturn(new SupportInfoDto());


        JsonObject municipality = new JsonObject();
        municipality.addProperty("region", "1234");

        // when
        MockHttpServletResponse response = mvc.perform(
                get("/api/supportInfo/searchRegion")
                        .contentType("application/json")
                        .content(municipality.toString()))
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    public void getSupportInfo_BAD_REQUEST_TEST() throws Exception {
        // given
        given(supportInfoBiz.getSupportInfoByCode("")).willReturn(new SupportInfoDto());

        // when
        MockHttpServletResponse response = mvc.perform(
                get("/api/supportInfo/searchRegion")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void updateSupportInfo() throws Exception {
        // given
        SupportInfoTable supportInfoTable = getSupportInfoTables(1).get(0);
        SupportInfoDto supportInfoDto = convertDateToSupportInfoDto(supportInfoTable);
        supportInfoRepository.save(supportInfoTable);
        given(supportInfoBiz.updateSupportInfo(supportInfoDto, supportInfoTable.getCode())).willReturn(supportInfoDto);

        JsonObject json = new JsonObject();
        json.addProperty("region", "B");
        json.addProperty("target", "지원대상");
        json.addProperty("usage", "운전");
        json.addProperty("limit", "1억원 이내");
        json.addProperty("rate", "1%");
        json.addProperty("institute", "추천기관");
        json.addProperty("mgmt", "관리점");
        json.addProperty("reception", "취급점");

        mvc.perform(
                post("/api/supportInfo/updateSupportInfo")
                        .contentType("application/json")
                        .content(json.toString())).andExpect(status().isOk());
    }

    @Test
    public void searchRegionLimitDescByCnt() throws Exception {
        //given
        SupportInfoTable supportInfoTable = getSupportInfoTables(1).get(0);
        List<String> instituteList = Lists.newArrayList();
        instituteList.add(supportInfoTable.getMunicipality().getRegion());
        given(supportInfoBiz.searchRegionLimitDescByCnt(1)).willReturn(instituteList);

        JsonObject json = new JsonObject();
        json.addProperty("cnt", "1");

        // when
        MockHttpServletResponse response = mvc.perform(
                get("/api/supportInfo/searchRegionLimitDescByCnt")
                        .contentType("application/json")
                        .content(json.toString()))
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    public void searchInstituteByMinRate() throws Exception {
        //given
        SupportInfoTable supportInfoTable = getSupportInfoTables(1).get(0);
        List<String> instituteList = Lists.newArrayList();
        instituteList.add(supportInfoTable.getMunicipality().getRegion());
        given(supportInfoBiz.searchInstituteByMinRate()).willReturn(instituteList);

        // when
        MockHttpServletResponse response = mvc.perform(
                get("/api/supportInfo/searchInstituteByMinRate")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    private List<SupportInfoTable> getSupportInfoTables(int createCnt) {
        List<SupportInfoTable> supportInfoTableList = Lists.newArrayList();

        for (int i = 1; i <= createCnt; i++) {
            SupportInfoTable supportInfoTable = new SupportInfoTable();
            Municipality municipality = new Municipality();

            municipality.setCode(i + "");
            municipality.setRegion(String.valueOf((char) ('A' + i)));

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
