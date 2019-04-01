package dev.miha.restapidemo;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class RestApiDemoApplication {

  public static void main(String[] args) {
    SpringApplication.run(RestApiDemoApplication.class, args);
  }

  //객체를 공용으로 쓸수있기때문에 빈으로 등록해서 사용
  @Bean
  public ModelMapper modelMapper(){
    return new ModelMapper();
  }

}
