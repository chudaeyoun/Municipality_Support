package com.support.api;

import com.google.gson.JsonObject;
import com.support.domain.UserDto;
import com.support.repository.UserRepository;
import com.support.service.UserBiz;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(SupportInfoApiController.class)
public class UserApiControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserBiz userBiz;

    @MockBean
    private UserRepository userRepository;

    @Test
    public void signup() throws Exception {
        // given
        UserDto userDto = getUser();
        String jwt = userBiz.makeJwt(getUser());
        given(userBiz.makeJwt(getUser())).willReturn(jwt);

        JsonObject json = new JsonObject();
        json.addProperty("id", userDto.getId());
        json.addProperty("pw", userDto.getPw());

        mvc.perform(
                post("/api/user/singup")
                        .contentType("application/json")
                        .content(json.toString()))
                .andExpect(status().isOk());
    }

    @Test
    public void signin() throws Exception {
        // given
        UserDto userDto = getUser();
        String jwt = userBiz.makeJwt(getUser());
        given(userBiz.makeJwt(getUser())).willReturn(jwt);

        JsonObject json = new JsonObject();
        json.addProperty("id", userDto.getId());
        json.addProperty("pw", userDto.getPw());

        mvc.perform(
                post("/api/user/signin")
                        .contentType("application/json")
                        .content(json.toString()))
                .andExpect(status().isOk());
    }

    @Test
    public void refresh() throws Exception {
        // given
        UserDto userDto = getUser();
        String jwt = userBiz.makeJwt(getUser());
        given(userBiz.makeJwt(getUser())).willReturn(jwt);

        JsonObject json = new JsonObject();
        json.addProperty("id", userDto.getId());
        json.addProperty("pw", userDto.getPw());

        mvc.perform(
                post("/api/user/refresh")
                        .contentType("application/json")
                        .content(json.toString()))
                .andExpect(status().isOk());
    }

    private UserDto getUser() {
        UserDto userDto = new UserDto();

        userDto.setId("test");
        userDto.setPw("1111");

        return userDto;
    }
}
