package dev.miha.restapidemo.configs;

import dev.miha.restapidemo.accounts.Account;
import dev.miha.restapidemo.accounts.AccountRole;
import dev.miha.restapidemo.accounts.AccountService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class AppConfig {

  @Bean
  public ModelMapper modelMapper() {
    return new ModelMapper();
  }

  @Bean
  public PasswordEncoder passwordEncoder(){
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();  // 스프링 최신버전. 여러 인코드더들이 있고 프리픽스가 붙음
  }

  @Bean
  public ApplicationRunner applicationRunner() {
    return new ApplicationRunner() {

      @Autowired
      AccountService accountService;

      @Override
      public void run(ApplicationArguments args) throws Exception {
        Account miha = Account.builder()
                .email("dev.mihakim@gmail.com")
                .password("qwer1234")
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();
        accountService.saveAccount(miha);
      }
    };
  }
}
