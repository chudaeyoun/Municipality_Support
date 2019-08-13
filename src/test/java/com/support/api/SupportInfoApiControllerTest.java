package com.support.api;

import com.google.gson.JsonObject;
import com.support.domain.Municipality;
import com.support.domain.SupportInfoDto;
import com.support.domain.SupportInfoTable;
import com.support.repository.MunicipalityRepository;
import com.support.repository.SupportInfoRepository;
import com.support.service.MunicipalityBiz;
import com.support.service.SupportInfoBiz;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(SupportInfoApiController.class)
public class SupportInfoApiControllerTest {

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
                post("/api/supportInfo/files")
                        .contentType("multipart/form-data")
                        .content(json.toString()))
                .andExpect(status().isBadRequest());
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
                get("/api/supportInfo/lists")
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
                get("/api/supportInfo/lists")
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
        given(supportInfoBiz.getSupportInfoByCode(supportInfoTable.getCode())).willReturn(supportInfoDto);

        JsonObject json = new JsonObject();
        json.addProperty("code", supportInfoTable.getMunicipality().getCode());
        json.addProperty("region", supportInfoTable.getMunicipality().getRegion());

        mvc.perform(
                post("/api/supportInfo/infos")
                        .contentType("application/json")
                        .content(json.toString()))
                .andExpect(status().isNoContent());
    }

    @Test
    public void getSupportInfo_NO_CONTENT_TEST() throws Exception {
        // given
        given(supportInfoBiz.getSupportInfoByCode("1234")).willReturn(new SupportInfoDto());

        JsonObject json = new JsonObject();
        json.addProperty("region", "1234");

        mvc.perform(
                post("/api/supportInfo/infos")
                        .contentType("application/json")
                        .content(json.toString()))
                .andExpect(status().isNoContent());
    }

    @Test
    public void getSupportInfo_BAD_REQUEST_TEST() throws Exception {
        // given
        given(supportInfoBiz.getSupportInfoByCode("")).willReturn(new SupportInfoDto());

        mvc.perform(
                post("/api/supportInfo/infos")
                        .contentType("application/json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateSupportInfo() throws Exception {
        // given
        SupportInfoTable supportInfoTable = getSupportInfoTables(1).get(0);
        SupportInfoDto supportInfoDto = convertDateToSupportInfoDto(supportInfoTable);

        given(supportInfoBiz.updateSupportInfo(supportInfoDto, supportInfoTable.getCode())).willReturn(supportInfoDto);

        JsonObject json = new JsonObject();
        json.addProperty("region", supportInfoDto.getRegion());
        json.addProperty("target", supportInfoDto.getTarget());
        json.addProperty("usage", supportInfoDto.getUsage());
        json.addProperty("limit", supportInfoDto.getLimit());
        json.addProperty("rate", supportInfoDto.getRate());
        json.addProperty("institute", supportInfoDto.getInstitute());
        json.addProperty("mgmt", supportInfoDto.getMgmt());
        json.addProperty("reception", supportInfoDto.getReception());

        mvc.perform(
                post("/api/supportInfo/modified")
                        .contentType("application/json")
                        .content(json.toString()))
                .andExpect(status().isNoContent());
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

        mvc.perform(
                post("/api/supportInfo/limitDesc")
                        .contentType("application/json")
                        .content(json.toString()))
                .andExpect(status().isOk());
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
                get("/api/supportInfo/leastRate")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    public void saveSupportInfo() throws Exception {
        // given
        SupportInfoTable supportInfoTable = getSupportInfoTables(1).get(0);
        given(supportInfoBiz.saveSupportInfo(supportInfoTable)).willReturn(supportInfoTable);

        JsonObject json = getSupportInfoJson(supportInfoTable);

        mvc.perform(
                post("/api/supportInfo/save")
                        .contentType("application/json")
                        .content(json.toString()))
                .andExpect(status().isOk());
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

    private JsonObject getSupportInfoJson(SupportInfoTable supportInfoTable) {
        JsonObject json = new JsonObject();
        JsonObject municipality = new JsonObject();

        municipality.addProperty("code", supportInfoTable.getCode());
        municipality.addProperty("region", supportInfoTable.getMunicipality().getRegion());
        json.add("municipality", municipality);

        json.addProperty("code", supportInfoTable.getCode());
        json.addProperty("target", supportInfoTable.getTarget());
        json.addProperty("usage", supportInfoTable.getUsage());
        json.addProperty("limit", supportInfoTable.getLimit());
        json.addProperty("rate", supportInfoTable.getRate());
        json.addProperty("institute", supportInfoTable.getInstitute());
        json.addProperty("mgmt", supportInfoTable.getMgmt());
        json.addProperty("reception", supportInfoTable.getReception());
        json.addProperty("id", supportInfoTable.getId());

        return json;
    }
}
