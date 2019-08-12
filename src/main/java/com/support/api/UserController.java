package com.support.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.support.domain.UserDto;
import com.support.domain.UserTable;
import com.support.service.UserBizImpl;
import com.support.util.BizException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserBizImpl userBizImpl;

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signup(@RequestBody UserDto userDto) {

        logger.info("Method : signup(), param {userDto} => " + userDto);

        if(userDto == null) {
            logger.error("파라미터 확인을 해주세요. param {userDto} => null");
            return new ResponseEntity(new BizException("계정정보를 확인해주세요."), HttpStatus.BAD_REQUEST);
        }

        try {
            userBizImpl.registerUser(userDto);
            userDto.setJwt(userBizImpl.makeJwt(userDto));

            return new ResponseEntity(userDto, HttpStatus.OK);
        } catch(Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<String> signin(@RequestBody UserDto userDto) {
        logger.info("Method : createJwt(), param {userDto} => " + userDto);

        return new ResponseEntity(userBizImpl.makeJwt(userDto), HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<String> refresh(@RequestBody UserDto userDto) {
        logger.info("Method : createJwt(), param {userDto} => " + userDto);

        return new ResponseEntity(userBizImpl.makeJwt(userDto), HttpStatus.OK);
    }

    @PostMapping("/jwts")
    public ResponseEntity<String> createJwt(@RequestBody UserDto userDto) {
        logger.info("Method : createJwt(), param {userTable} => " + userDto);

        return new ResponseEntity(userBizImpl.makeJwt(userDto), HttpStatus.OK);
    }

}
