package dev.miha.restapidemo.configs;

import dev.miha.restapidemo.accounts.AccountService;
import dev.miha.restapidemo.common.AppProperties;
import dev.miha.restapidemo.common.BaseControllerTest;
import dev.miha.restapidemo.common.TestDescription;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthServerConfigTest extends BaseControllerTest {

  @Autowired
  AccountService accountService;

  @Autowired
  AppProperties appProperties;

  @Test
  @TestDescription("인증 토큰을 발급 받는 테스트")
  public void getAuthToken() throws Exception {
    //Given
    // application이 뜰때 유저정보가 저장되어 있으니까 저장할필요없음

    this.mockMvc.perform(post("/oauth/token") // oauth/token 핸들러 제공
            .with(httpBasic(appProperties.getClientId(), appProperties.getClientSecret()))   // 테스트용 디펜던시 추가해야함 spring-security-test
            .param("username", appProperties.getUserUsername())
            .param("password", appProperties.getUserPassword())
            .param("grant_type", "password"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("access_token").exists());
  }
}