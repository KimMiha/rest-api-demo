package dev.miha.restapidemo.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.miha.restapidemo.common.RestDocsConfiguration;
import dev.miha.restapidemo.common.TestDescription;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//junit4기준으로 테스트 작성
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)  //다른 스프링 빈 설정파일을 읽어와서 사용하는 방법중 하나.
public class EventControllerTests {
  //mockMvc를 사용하면 mocking이 되어있는 dispatcher servlet을 상대로 가짜요청과 응답을, 가짜 요청을 만들어서 dispatcher servlet에 보내고 그 응답을 확인할수있는 테스트를 만들수있다.
  //웹과 관련된것(빈)만 등록하기때문에, 슬라이싱 테스트라고도 한다. 계층별로..나눠져서 테스트용 빈들을 등록해서 조금더 빠르다. 구역을 나눠서 테스트하는 거라고 보면되지만, 단위테스트라고 하기에는 어렵다. 너무 많은게 개임되어있어서.
  @Autowired
  MockMvc mockMvc;  // mockMvc 클래스는 요청을 만들수있고 응답을 검증할수있는 Spring MVC Test에 있어 핵심적인 클래스중하나
                    // 웹서버를 띄우지 않기때문에 빠르 dispatcher servlet이란것까지 만들어야해서 단위테스트보다 빠르진 않다

  @Autowired
  ObjectMapper objectMapper;  // 임의의 빈으로 등록

  @Test
  @TestDescription("정상적으로 이벤트를 생성하는 테스트")
  public void createEvent() throws Exception {
    //입력값이 제대로 들어오는 경우(eventDto사용) 제대로 동작
    EventDto event = EventDto.builder()
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
            .andDo(print()) //어떤 응답과 요청을 받았는지 볼수있다. /여기서 나오는 모든 내용은 andExpect()로 검증해볼수있다.
            .andExpect(status().isCreated())  //perform하고나면 응답이 나온다. 그 응답을 확인한다.
            .andExpect(jsonPath("id").exists())
            .andExpect(header().exists(HttpHeaders.LOCATION))
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_UTF8_VALUE)) // 특정한 값이 나오는지
            .andExpect(jsonPath("offline").value(true))
            .andExpect(jsonPath("free").value(false))
            .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
            .andExpect(jsonPath("_links.self").exists())
            .andExpect(jsonPath("_links.query-events").exists())
            .andExpect(jsonPath("_links.update-event").exists())
            .andDo(document("create-event",
                    links(
                            linkWithRel("self").description("link to self"),
                            linkWithRel("query-events").description("link to query events"),
                            linkWithRel("update-event").description("link to update on existing event")
                    ),
                    requestHeaders(
                            headerWithName(HttpHeaders.ACCEPT).description("accept headeer"),
                            headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                    ),
                    requestFields(
                            fieldWithPath("name").description("Name of new event"),
                            fieldWithPath("description").description("description of new event"),
                            fieldWithPath("beginEnrollmentDateTime").description("date time of begin of new event"),
                            fieldWithPath("closeEnrollmentDateTime").description("date time of close of new event"),
                            fieldWithPath("beginEventDateTime").description("date time of begin of new event"),
                            fieldWithPath("endEventDateTime").description("date time of end of new event"),
                            fieldWithPath("location").description("location of new evnet"),
                            fieldWithPath("basePrice").description("base price of new event"),
                            fieldWithPath("maxPrice").description("max price of new event"),
                            fieldWithPath("limitOfEnrollment").description("limit of enrollment")
                    ),
                    responseHeaders(
                            headerWithName(HttpHeaders.LOCATION).description("location headeer"),
                            headerWithName(HttpHeaders.CONTENT_TYPE).description("HAL Json type")

                    ),
//                  relaxedResponseFields(  // relaxed 라는 프리픽스를 사용하면 문서의 일부분만 하겠다. 단점-정확한 문서를 만들지 못함
                    responseFields(         // 위에서 links를 확인했지만 변화를 감지하고 제대로 테스트를 하기위해서는 relaxed는 지양하는것이 좋다.
                            fieldWithPath("id").description("Identifier of new event"),
                            fieldWithPath("name").description("Name of new event"),
                            fieldWithPath("description").description("description of new event"),
                            fieldWithPath("beginEnrollmentDateTime").description("date time of begin of new event"),
                            fieldWithPath("closeEnrollmentDateTime").description("date time of close of new event"),
                            fieldWithPath("beginEventDateTime").description("date time of begin of new event"),
                            fieldWithPath("endEventDateTime").description("date time of end of new event"),
                            fieldWithPath("location").description("location of new evnet"),
                            fieldWithPath("basePrice").description("base price of new event"),
                            fieldWithPath("maxPrice").description("max price of new event"),
                            fieldWithPath("limitOfEnrollment").description("limit of enrollment"),
                            fieldWithPath("free").description("It tells if this event is free or not"),
                            fieldWithPath("offline").description("It tells if this event is offline event or not"),
                            fieldWithPath("eventStatus").description("evnet status"),
                            fieldWithPath("_links.self.href").description("link to self"),
                            fieldWithPath("_links.query-events.href").description("link to query event list"),
                            fieldWithPath("_links.update-event.href").description("link to update existing event")
                    )
            ));
    ;
  }

  @Test
  @TestDescription("입력 받을 수 없는 값을 사용한 경우에 에러가 발생하는 테스트 ")
  public void createEvent_Bad_Request() throws Exception {
    //입력값이 비정상적이니 응답값이 Bad Request 여야 한다.
    Event event = Event.builder()
            .id(100)
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
            .free(true)
            .offline(false)
            .eventStatus(EventStatus.PUBLISHED)
            .build();

    mockMvc.perform(post("/api/events")    //perform요청한다,  http post 요청을 보낸
                .contentType(MediaType.APPLICATION_JSON_UTF8) //요청의 본문에 제이슨일 담아 보내고 있다 를 알려주는거
                .accept(MediaTypes.HAL_JSON)  //어떠한 응답을 원한다 HAL 형식으로
                .content(objectMapper.writeValueAsString(event))
              )
            .andDo(print()) //어떤 응답과 요청을 받았는지 볼수있다. /여기서 나오는 모든 내용은 andExpect()로 검증해볼수있다.
            .andExpect(status().isBadRequest())  //perform하고나면 응답이 나온다. 그 응답을 확인한다.
    ;
  }

  @Test
  @TestDescription("입력값이 비어 있는 경우에 에러가 발생하는 테스트")
  public void createEvent_Bad_Request_Empty_Input() throws Exception {
    EventDto eventDto = EventDto.builder().build();

    this.mockMvc.perform(post("/api/events")
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .content(this.objectMapper.writeValueAsString(eventDto))
              )
            .andExpect(status().isBadRequest());
  }

  @Test
  @TestDescription("입력값이 잘못된 경우에 에러가 발생하는 테스트")
  public void createEvent_Bad_Request_Wrong_Input() throws Exception {
    EventDto eventDto = EventDto.builder()
            .name("Spring")
            .description("REST API Development with Spring")
            //이벤트 끝나는 날짜보다 시작하는 날짜가 빠른 이상한 경우
            .beginEnrollmentDateTime(LocalDateTime.of(2019,04,04,14,00))
            .closeEnrollmentDateTime(LocalDateTime.of(2019,04,03,15,00))
            .beginEventDateTime(LocalDateTime.of(2019,04,02,15,00))
            .endEventDateTime(LocalDateTime.of(2019,04,01,16,00))
            //최대값이 기본값보다 작은 이상한 경우
            .basePrice(10000)
            .maxPrice(200)
            .limitOfEnrollment(100)
            .location("강남역 스타트업 팩토리")
            .build();

    this.mockMvc.perform(post("/api/events")
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .content(this.objectMapper.writeValueAsString(eventDto)))
            .andExpect(status().isBadRequest()) //badRequest 로 받을수있는 응답에 본문에 메세지가 있길 바라고, 그 메세지를 어떻게 만드는지 확인해보겠다.
            .andDo(print())
            //에러배열중에서도 어떤필드에서 발생하는지 기본 메세지는 뭐고 에러코드는 무엇이고 입력거절당한 그 값이 무엇이였는지 등등...
            .andExpect(jsonPath("$[0].objectName").exists())
//            .andExpect(jsonPath("$[0].field").exists())
            .andExpect(jsonPath("$[0].defaultMessage").exists())
            .andExpect(jsonPath("$[0].code").exists())
//            .andExpect(jsonPath("$[0].rejectedVallue").exists())
            ;
  }
}
