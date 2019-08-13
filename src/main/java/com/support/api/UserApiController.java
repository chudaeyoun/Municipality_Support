package com.support.api;

import com.support.domain.UserDto;
import com.support.service.UserBiz;
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
public class UserApiController {

    private static final Logger logger = LoggerFactory.getLogger(UserApiController.class);

    @Autowired
    private UserBiz userBiz;

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signup(@RequestBody UserDto userDto) {

        logger.info("Method : signup(), param {userDto} => " + userDto);

        if (userDto == null) {
            logger.error("파라미터 확인을 해주세요. param {userDto} => null");
            return new ResponseEntity(new BizException("계정정보를 확인해주세요."), HttpStatus.BAD_REQUEST);
        }

        try {
            if(userBiz.registerUser(userDto) == 1) {
                userDto.setJwt(userBiz.makeJwt(userDto));
                logger.info("signup() jwt => " + userDto.getJwt());

                return new ResponseEntity(userDto, HttpStatus.OK);
            } else {
                logger.info("이미 가입 되어있습니다.");
                return new ResponseEntity("이미 가입 되어있습니다.", HttpStatus.OK);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<String> signin(@RequestBody UserDto userDto) {
        logger.info("Method : signin(), param {userDto} => " + userDto);

        String jwt = userBiz.makeJwt(userDto);
        userDto.setJwt(jwt);
        logger.info("signin() jwt => " + userDto.getJwt());

        return new ResponseEntity(userDto, HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<String> refresh(@RequestBody UserDto userDto) {
        logger.info("Method : refresh(), param {userDto} => " + userDto);

        String jwt = userBiz.makeJwt(userDto);
        userDto.setJwt(jwt);
        logger.info("refresh() jwt => " + userDto.getJwt());

        return new ResponseEntity(userDto, HttpStatus.OK);
    }

}
