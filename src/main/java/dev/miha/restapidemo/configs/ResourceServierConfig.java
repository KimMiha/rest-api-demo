package dev.miha.restapidemo.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;

@Configuration
@EnableResourceServer
public class ResourceServierConfig extends ResourceServerConfigurerAdapter {

  @Override
  public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
    resources.resourceId("event");
  }

  @Override
  public void configure(HttpSecurity http) throws Exception {
    http
            .anonymous()
            .and()
            .authorizeRequests()
            .mvcMatchers(HttpMethod.GET, "/api/**")
            .permitAll()
 //           .anonymous() 인증을 하지 않은 상태로만 사용할수있다. 인증을 하면 사용불가
            .anyRequest()
            .authenticated()
            .and()
            // 인증이 잘못됐거나 권한이 없을때 예외 발생
            .exceptionHandling()
            //그중 접근권한이 없을때는 OAuth2AccessDeniedHandler 이 핸들러를 사용하겠다고 등록.
            .accessDeniedHandler(new OAuth2AccessDeniedHandler());
  }
}
