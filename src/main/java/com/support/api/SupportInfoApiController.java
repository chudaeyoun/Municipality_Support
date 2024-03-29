package com.support.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.support.domain.Municipality;
import com.support.domain.SupportInfoDto;
import com.support.domain.SupportInfoTable;
import com.support.service.MunicipalityBiz;
import com.support.service.SupportInfoBiz;
import com.support.util.BizException;
import com.support.util.CvsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/supportInfo")
public class SupportInfoApiController {

    private static final Logger logger = LoggerFactory.getLogger(SupportInfoApiController.class);

    @Autowired
    private SupportInfoBiz supportInfoBiz;

    @Autowired
    private MunicipalityBiz municipalityBiz;

    @PostMapping("/files")
    public ResponseEntity<String> singleFileUpload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            logger.error("파일을 확인해주세요.");
            return new ResponseEntity(new BizException("파일을 확인해주세요."), HttpStatus.BAD_REQUEST);
        }

        CvsUtil cvsUtil = new CvsUtil();
        String line = null;

        try {
            InputStreamReader inputStreamReader = new InputStreamReader(file.getInputStream(), "MS949");
            BufferedReader in = new BufferedReader(inputStreamReader);
            in.readLine(); // 첫 줄 건너 뛰기

            while ((line = in.readLine()) != null) {
                String[] csv = cvsUtil.csvSplit(line);

                // 데이터가 9개의 컬럼이 아닐 경우 패스
                if (csv.length != 9) {
                    continue;
                }
                // 지자체명으로 지자체테이블 검색. 없으면 insert, 있으면 update
                Municipality municipality = municipalityBiz.getMunicipalityRegion(csv[1]);

                if (municipality == null) {
                    // 랜덤으로 생성된 코드가 db에 없을때까지
                    while (true) {
                        String code = UUID.randomUUID().toString();

                        if (!municipalityBiz.existsMunicipalityCode(code)) {
                            csv[0] = code;
                            break;
                        } else {
                            logger.error("duplicate code = " + code);
                        }
                    }
                } else {
                    csv[0] = municipality.getCode();
                }
                supportInfoBiz.insertSupportInfoTable(csv);
            }
            return new ResponseEntity("성공적으로 업로드 하였습니다.", HttpStatus.OK);
        } catch (IOException e) {
            logger.error(e.getMessage());
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/lists")
    public ResponseEntity<List<SupportInfoDto>> getAllSupportInfoList() {
        try {
            List<SupportInfoDto> SupportInfoDtoList = supportInfoBiz.getAllSupportInfoList();
            if (SupportInfoDtoList.isEmpty()) {
                return new ResponseEntity(SupportInfoDtoList, HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity(SupportInfoDtoList, HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/infos")
    public ResponseEntity<SupportInfoDto> getSupportInfo(@RequestBody Municipality municipality) {
        if (municipality == null) {
            logger.error("파라미터 확인을 해주세요. param {region} => null");
            return new ResponseEntity(new BizException("지자체명을 확인해주세요."), HttpStatus.BAD_REQUEST);
        }

        String region = municipality.getRegion();

        logger.info("Method : getSupportInfo(), param {region} => " + region);

        try {
            municipality = municipalityBiz.getMunicipalityRegion(region);
            if (municipality == null) {
                return new ResponseEntity(municipality, HttpStatus.NO_CONTENT);
            }

            SupportInfoDto supportInfoDto = supportInfoBiz.getSupportInfoByCode(municipality.getCode());
            if (supportInfoDto == null) {
                return new ResponseEntity(supportInfoDto, HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity(supportInfoDto, HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/modified")
    public ResponseEntity<SupportInfoDto> updateSupportInfo(@RequestBody SupportInfoDto supportInfoDto) {
        if (supportInfoDto == null) {
            logger.error("파라미터 확인을 해주세요. 지원 지자체 정보 => null");
            return new ResponseEntity(new BizException("지원 지자체 정보의 수정데이터를 확인해주세요."), HttpStatus.BAD_REQUEST);
        }
        logger.info("Method : updateSupportInfo(), param => " + supportInfoDto);

        try {
            Municipality municipality = municipalityBiz.getMunicipalityRegion(supportInfoDto.getRegion());
            if (municipality == null) {
                return new ResponseEntity(supportInfoDto, HttpStatus.NO_CONTENT);
            }

            //지자체정보로 지자체지원정보 검색. 없을때 예외처리
            if (supportInfoBiz.getSupportInfoByCode(municipality.getCode()) == null) {
                return new ResponseEntity(supportInfoDto, HttpStatus.NO_CONTENT);
            }

            supportInfoDto = supportInfoBiz.updateSupportInfo(supportInfoDto, municipality.getCode());
            return new ResponseEntity(supportInfoDto, HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/names")
    public ResponseEntity<SupportInfoDto> searchRegionLimitDescByCnt(@RequestBody String json) {
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(json);
        int cnt = je.getAsJsonObject().get("cnt").getAsInt();

        System.out.println(je.getAsJsonObject().get("cnt"));
        System.out.println("put cnt = " + cnt);

        if (cnt < 0) {
            logger.error("파라미터 확인을 해주세요. param {cnt} => 0보다 작음");
            return new ResponseEntity(new BizException("cnt가 0보다 작습니다."), HttpStatus.BAD_REQUEST);
        }

        try {
            List<String> instituteList = supportInfoBiz.searchRegionLimitDescByCnt(cnt);
            if (instituteList.isEmpty()) {
                return new ResponseEntity(instituteList, HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity(instituteList, HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/rates")
    public ResponseEntity<List<String>> searchInstituteByMinRate() {
        try {
            List<String> instituteList = supportInfoBiz.searchInstituteByMinRate();
            if (instituteList.isEmpty()) {
                return new ResponseEntity(instituteList, HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity(instituteList, HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/save")
    public ResponseEntity<SupportInfoTable> saveSupportInfo(@RequestBody SupportInfoTable supportInfoTable) {
        if (supportInfoTable == null) {
            logger.error("파라미터 확인을 해주세요. 지원 지자체 정보 => null");
            return new ResponseEntity(new BizException("지원 지자체 정보를 확인해주세요."), HttpStatus.BAD_REQUEST);
        }

        logger.info("Method : saveSupportInfo(), param => " + supportInfoTable);

        try {
            supportInfoTable = supportInfoBiz.saveSupportInfo(supportInfoTable);
            if (supportInfoTable == null) {
                return new ResponseEntity(supportInfoTable, HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity(supportInfoTable, HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/recommends")
    public ResponseEntity<SupportInfoTable> recommendSupportInfo(@RequestBody String input) {
        if (input == null) {
            logger.error("파라미터 확인을 해주세요. input => null");
            return new ResponseEntity(new BizException("input 기사 정보를 확인해주세요."), HttpStatus.BAD_REQUEST);
        }

        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(input);
        input = je.getAsJsonObject().get("input").getAsString();

        logger.info("Method : recommendSupportInfo(), param => " + input);

        try {
            SupportInfoDto supportInfoDto = supportInfoBiz.recommendSupportInfo(input);
            if (supportInfoDto == null) {
                return new ResponseEntity(supportInfoDto, HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity(supportInfoDto, HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
