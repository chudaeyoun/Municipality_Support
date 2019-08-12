package com.support.service;

import com.support.domain.Municipality;
import com.support.repository.MunicipalityRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MunicipalityBizImpl.class)
public class MunicipalityBizImplTest {

    //@Autowired
    private MunicipalityBizImpl municipalityBizImpl;

    @MockBean
    private MunicipalityRepository municipalityRepository;

    @Test
    public void existsMunicipalityCode() {
        Municipality testMunicipality = getMunicipality(1);
        boolean expected = false;
        given(municipalityRepository.existsByCode(testMunicipality.getCode())).willReturn(expected);
        boolean existBool = municipalityBizImpl.existsMunicipalityCode(testMunicipality.getCode());
        assertThat(existBool).isEqualTo(expected);
    }

    @Test
    public void existsMunicipalityRegion() {
        Municipality testMunicipality = getMunicipality(1);
        boolean expected = true;
        given(municipalityRepository.existsByRegion(testMunicipality.getRegion())).willReturn(expected);
        boolean existBool = municipalityBizImpl.existsMunicipalityRegion(testMunicipality.getRegion());
        assertThat(existBool).isEqualTo(expected);
    }

    @Test
    public void getMunicipalityRegion() {
        Municipality expected = getMunicipality(2);
        given(municipalityRepository.findByRegion(expected.getRegion())).willReturn(expected);
        Municipality municipality = municipalityBizImpl.getMunicipalityRegion(expected.getRegion());
        assertThat(municipality).isEqualTo(expected);
    }

    private Municipality getMunicipality(int testcase) {
        Municipality municipality = new Municipality();

        switch (testcase) {
            case 1:
                municipality.setRegion("강릉시");
                municipality.setCode("test1111");
                break;
            case 2:
                municipality.setRegion("강원도");
                municipality.setCode("test2222");
                break;
        }
        return municipality;
    }

}
