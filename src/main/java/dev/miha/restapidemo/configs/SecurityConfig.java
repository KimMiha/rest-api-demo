package dev.miha.restapidemo.configs;

import dev.miha.restapidemo.accounts.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

@Configuration
@EnableWebSecurity  // + WebSecurityConfigurerAdapter를 상속받으면 스프링부트가 제공하는 스프링시큐리티 설정은 더이상 적용되지 않는다.
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  AccountService accountService;

  @Autowired
  PasswordEncoder passwordEncoder;

  @Bean
  public TokenStore tokenStore(){
    return new InMemoryTokenStore();
  }

  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(accountService)
            .passwordEncoder(passwordEncoder);
  }

  @Override
  public void configure(WebSecurity web) throws Exception {
    //시큐리티 필터를 적용할지 말지를 결정하는 곳
    web.ignoring().mvcMatchers("/docs/index.html");
    //스프링 부트가 제공하는 패스리퀘스 스태틱리소스에대한 기본 위치를 다 가져와서 시큐리티가 적용되지 않도록
    web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {  //이걸 재정의하면 스프링시큐리티를 마음대로설정가능
    http
            .anonymous()  //익명사용자 허용
            .and()
            .formLogin()  //폼 인증 사용
            .and()
            .authorizeRequests()  // 허용할 메소드가 있는데 get요청의 api/하위전체의 모든걸 익명 허용하고
            .mvcMatchers(HttpMethod.GET, "/api/**").anonymous()
            .anyRequest().authenticated();  //나머지는 인증이 필요로하다
  }
}
