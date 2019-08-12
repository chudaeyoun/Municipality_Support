package com.support.service;

import com.google.common.collect.Maps;
import com.support.domain.UserDto;
import com.support.domain.UserTable;
import com.support.repository.UserRepository;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;
import java.util.Map;

@Service
public class UserBizImpl implements UserBiz {

    @Autowired
    private UserRepository userRepository;

    private String secretKey = "abcdefghijklmnopqrstuvwxyz";

    private static final Logger logger = LoggerFactory.getLogger(UserBizImpl.class);

    @Override
    public String makeJwt(UserDto userDto) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        Date expireTime = new Date();
        expireTime.setTime(expireTime.getTime() + 1000 * 60 * 10); // 만료시간 10분
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(secretKey);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        Map<String, Object> headerMap = Maps.newHashMap();

        headerMap.put("typ","JWT");
        headerMap.put("alg","HS256");

        Map<String, Object> map = Maps.newHashMap();

        String id = userDto.getId();
        String pw = userDto.getPw();

        map.put("id", id);
        map.put("pw", pw);

        JwtBuilder builder = Jwts.builder().setHeader(headerMap)
                .setClaims(map)
                .setExpiration(expireTime)
                .signWith(signatureAlgorithm, signingKey);

        return builder.compact();
    }

    @Override
    public int checkJwt(String jwt) {
        try {
            Claims claims = Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(secretKey))
                    .parseClaimsJws(jwt).getBody(); // 정상 수행된다면 해당 토큰은 정상토큰

            logger.info("expireTime :" + claims.getExpiration());
            logger.info("name :" + claims.get("name"));
            logger.info("Email :" + claims.get("email"));

            return 1;
        } catch (ExpiredJwtException exception) {
            logger.info("토큰 만료");
            return 2;
        } catch (JwtException exception) {
            logger.info("토큰 변조");
            return 3;
        }
    }

    public int registerUser(UserDto userDto) {
        UserTable userTable = new UserTable();
        userTable.setId(userDto.getId());
        userTable.setPw(userDto.getPw());

        if(!userRepository.existsById(userDto.getId())) {
            userRepository.save(userTable);
            return 1;
        } else {
            return 2;
        }
    }
}
