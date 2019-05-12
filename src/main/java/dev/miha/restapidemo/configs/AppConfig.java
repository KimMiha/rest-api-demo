package dev.miha.restapidemo.configs;

import dev.miha.restapidemo.accounts.Account;
import dev.miha.restapidemo.accounts.AccountRole;
import dev.miha.restapidemo.accounts.AccountService;
import dev.miha.restapidemo.common.AppProperties;
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

      @Autowired
      AppProperties appProperties;

      @Override
      public void run(ApplicationArguments args) throws Exception {
        Account admin = Account.builder()
                .email(appProperties.getAdminUsername())
                .password(appProperties.getAdminPassword())
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();
        accountService.saveAccount(admin);

        Account user = Account.builder()
                .email(appProperties.getUserUsername())
                .password(appProperties.getUserPassword())
                .roles(Set.of(AccountRole.USER))
                .build();
        accountService.saveAccount(user);
      }
    };
  }
}
