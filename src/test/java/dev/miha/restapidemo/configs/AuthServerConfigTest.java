package dev.miha.restapidemo.configs;

import dev.miha.restapidemo.accounts.Account;
import dev.miha.restapidemo.accounts.AccountRole;
import dev.miha.restapidemo.accounts.AccountService;
import dev.miha.restapidemo.common.BaseControllerTest;
import dev.miha.restapidemo.common.TestDescription;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthServerConfigTest extends BaseControllerTest {

  @Autowired
  AccountService accountService;

  @Test
  @TestDescription("인증 토큰을 발급 받는 테스트")
  public void getAuthToken() throws Exception {
    //Given
    String username = "dev.mihakim@gmail.com";
    String password = "qwer1234";
    Account miha = Account.builder()
            .email(username)
            .password(password)
            .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
            .build();
    this.accountService.saveAccount(miha);

    String clintId = "myApp";
    String clientSecret = "pass";

    this.mockMvc.perform(post("/oauth/token") // oauth/token 핸들러 제공
            .with(httpBasic(clintId, clientSecret))   // 테스트용 디펜던시 추가해야함 spring-security-test
            .param("username", username)
            .param("password", password)
            .param("grant_type", "password"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("access_token").exists());
  }
}