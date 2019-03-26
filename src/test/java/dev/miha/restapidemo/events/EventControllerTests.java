package dev.miha.restapidemo.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//junit4기준으로 테스트 작
@RunWith(SpringRunner.class)
@WebMvcTest //웹과 관련된 빈들이 모두 등록된다. MockMvc를 주입받아서 손쉽게 사용
public class EventControllerTests {
  //mockMvc를 사용하면 mocking이 되어있는 dispatcher servlet을 상대로 가짜요청과 응답을, 가짜 요청을 만들어서 dispatcher servlet에 보내고 그 응답을 확인할수있는 테스트를 만들수있다.
  //웹과 관련된것(빈)만 등록하기때문에, 슬라이싱 테스트라고도 한다. 계층별로..나눠져서 테스트용 빈들을 등록해서 조금더 빠르다. 구역을 나눠서 테스트하는 거라고 보면되지만, 단위테스트라고 하기에는 어렵다. 너무 많은게 개임되어있어서.
  @Autowired
  MockMvc mockMvc;  // mockMvc 클래스는 요청을 만들수있고 응답을 검증할수있는 Spring MVC Test에 있어 핵심적인 클래스중하나
                    // 웹서버를 띄우지 않기때문에 빠르 dispatcher servlet이란것까지 만들어야해서 단위테스트보다 빠르진 않다

  @Autowired
  ObjectMapper objectMapper;  // 임의의 빈으로 등록

  @Test
  public void createEvent() throws Exception {
    Event event = Event.builder()
            .name("Spring")
            .description("REST API Development with Spring")
            .beginEnrollmentDateTime(LocalDateTime.of(2019,03,26,14,00))
            .closeEnrollmentDateTime(LocalDateTime.of(2019,03,26,15,00))
            .beginEventDateTime(LocalDateTime.of(2019,03,26,15,00))
            .endEventDateTime(LocalDateTime.of(2019,03,26,16,00))
            .basePrice(100)
            .maxPrice(200)
            .limitOfEnrollment(100)
            .location("강남역 스타트업 팩토리")
            .build();

    mockMvc.perform(post("/api/events")    //perform요청한다,  http post 요청을 보낸
                .contentType(MediaType.APPLICATION_JSON_UTF8) //요청의 본문에 제이슨일 담아 보내고 있다 를 알려주는거
                .accept(MediaTypes.HAL_JSON)  //어떠한 응답을 원한다 HAL 형식으로
                .content(objectMapper.writeValueAsString(event))
              )
            .andDo(print()) //어떤 응답과 요청을 받았는지 볼수있다.
            .andExpect(status().isCreated())  //perform하고나면 응답이 나온다. 그 응답을 확인한다.
            .andExpect(jsonPath("id").exists())
    ;
  }
}
