package dev.miha.restapidemo.events;

import dev.miha.restapidemo.accounts.Account;
import dev.miha.restapidemo.accounts.AccountRepository;
import dev.miha.restapidemo.accounts.AccountRole;
import dev.miha.restapidemo.accounts.AccountService;
import dev.miha.restapidemo.common.AppProperties;
import dev.miha.restapidemo.common.BaseControllerTest;
import dev.miha.restapidemo.common.TestDescription;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.common.util.Jackson2JsonParser;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.IntStream;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//junit4기준으로 테스트 작성
public class EventControllerTests extends BaseControllerTest {

  @Autowired
  EventRepository eventRepository;

  @Autowired
  AccountService accountService;

  @Autowired
  AccountRepository accountRepository;

  @Autowired
  AppProperties appProperties;

  // inMemoryDB긴 하지만 테스트간에는 DB를 서로 공유하기 때문에 데이터 자체가 독립작이질 못하다. 그래서 데이터를 전부 지우기.
  @Before
  public void setUp() {
    this.eventRepository.deleteAll();
    this.accountRepository.deleteAll();
  }

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
            .header(HttpHeaders.AUTHORIZATION, getBearerToken())
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
                            linkWithRel("update-event").description("link to update on existing event"),
                            linkWithRel("profile").description("link to profile")
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
                    relaxedResponseFields(  // relaxed 라는 프리픽스를 사용하면 문서의 일부분만 하겠다. 단점-정확한 문서를 만들지 못함
//                    responseFields(         // 위에서 links를 확인했지만 변화를 감지하고 제대로 테스트를 하기위해서는 relaxed는 지양하는것이 좋다.
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
                            fieldWithPath("_links.update-event.href").description("link to update existing event"),
                            fieldWithPath("_links.profile.href").description("link to profile")
                    )
            ));
    ;
  }

  private String getBearerToken() throws Exception {
    return "Bearer "+ getAccessToken();
  }

  private String getAccessToken() throws Exception {
    //Given
    Account miha = Account.builder()
            .email(appProperties.getUserUsername())
            .password(appProperties.getUserPassword())
            .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
            .build();
    this.accountService.saveAccount(miha);

    ResultActions perform = this.mockMvc.perform(MockMvcRequestBuilders.post("/oauth/token") // oauth/token 핸들러 제공
            .with(httpBasic(appProperties.getClientId(), appProperties.getClientSecret()))   // 테스트용 디펜던시 추가해야함 spring-security-test
            .param("username", appProperties.getUserUsername())
            .param("password", appProperties.getUserPassword())
            .param("grant_type", "password"));
    var responseBody = perform.andReturn().getResponse().getContentAsString();
    Jackson2JsonParser parser = new Jackson2JsonParser();
    return parser.parseMap(responseBody).get("access_token").toString();
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
                    .header(HttpHeaders.AUTHORIZATION, getBearerToken())
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
                    .header(HttpHeaders.AUTHORIZATION, getBearerToken())
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
            .header(HttpHeaders.AUTHORIZATION, getBearerToken())
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .content(this.objectMapper.writeValueAsString(eventDto)))
            .andExpect(status().isBadRequest()) //badRequest 로 받을수있는 응답에 본문에 메세지가 있길 바라고, 그 메세지를 어떻게 만드는지 확인해보겠다.
            .andDo(print())
            //에러배열중에서도 어떤필드에서 발생하는지 기본 메세지는 뭐고 에러코드는 무엇이고 입력거절당한 그 값이 무엇이였는지 등등...
            .andExpect(jsonPath("content[0].objectName").exists())
//            .andExpect(jsonPath("$[0].field").exists())
            .andExpect(jsonPath("content[0].defaultMessage").exists())
            .andExpect(jsonPath("content[0].code").exists())
            .andExpect(jsonPath("_links.index").exists())
//            .andExpect(jsonPath("$[0].rejectedVallue").exists())
            ;
  }

  @Test
  @TestDescription("30개의 이벤트를 10개씩 두번째 페이지 조회하기")
  public void queryEvents() throws Exception {
    //Given
    IntStream.range(0, 30).forEach(this::generateEvent);  //메소드 레퍼런스

    //When & Then
    this.mockMvc.perform(get("/api/events")
            .param("page","1")    // page sms 0 부터 시작
            .param("size","10")
            .param("sort","name,DESC")  //이름 역순
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("page").exists())
            .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
            .andExpect(jsonPath("_links.self").exists())
            .andExpect(jsonPath("_links.profile").exists())
            .andDo(document("query-events"))
    ;
  }

  @Test
  @TestDescription("30개의 이벤트를 10개씩 인증정보를 가지고 두번째 페이지 조회하기")
  public void queryEventsWithAuthenticztion() throws Exception {
    //Given
    IntStream.range(0, 30).forEach(this::generateEvent);  //메소드 레퍼런스

    //When & Then
    this.mockMvc.perform(get("/api/events")
            .header(HttpHeaders.AUTHORIZATION, getBearerToken())
            .param("page","1")    // page sms 0 부터 시작
            .param("size","10")
            .param("sort","name,DESC")  //이름 역순
    )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("page").exists())
            .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
            .andExpect(jsonPath("_links.self").exists())
            .andExpect(jsonPath("_links.profile").exists())
            .andExpect(jsonPath("_links.create-evet").exists())
            .andDo(document("query-events"))
    ;
  }

  @Test
  @TestDescription("기존의 이벤트를 하나 조회하기")
  public void getEvent()throws Exception {
    //Given
    Event event = this.generateEvent(100);

    //When & Then
    this.mockMvc.perform(get("/api/events/{id}", event.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("name").exists())
            .andExpect(jsonPath("id").exists())
            .andExpect(jsonPath("_links.self").exists())
            .andExpect(jsonPath("_links.profile").exists())
            .andDo(document("get-an-event"))
    ;

  }

  @Test
  @TestDescription("없는 이벤트를 조회했을 때 404 응답받기")
  public void getEvent404()throws Exception {
    //When & Then
    this.mockMvc.perform(get("/api/events/388932"))
            .andExpect(status().isNotFound());
    ;
  }

  @Test
  @TestDescription("이벤트를 정상적으로 수정하기")
  public void updateEvent() throws Exception {
    //Given
    Event event = this.generateEvent(200);
    EventDto eventDto = this.modelMapper.map(event, EventDto.class);  //modelMapper로 event에 있는걸 eventDto에 담는다
    String eventName = "Updated Event";
    eventDto.setName(eventName);

    //When & Then
    this.mockMvc.perform(put("/api/events/{id}", event.getId())
                  .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                  .contentType(MediaType.APPLICATION_JSON_UTF8)
                  .content(this.objectMapper.writeValueAsString(eventDto)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("name").value(eventName))
            .andExpect(jsonPath("_links.self").exists())
            ;
  }

  @Test
  @TestDescription("입력값이 비어있는 경우 이벤트 수정 실패")
  public void updateEvent400_Empty() throws Exception {
    //Given
    Event event = this.generateEvent(200);
    EventDto eventDto = new EventDto();

    //When & Then
    this.mockMvc.perform(put("/api/events/{id}", event.getId())
                  .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                  .contentType(MediaType.APPLICATION_JSON_UTF8)
                  .content(this.objectMapper.writeValueAsString(eventDto)))
            .andDo(print())
            .andExpect(status().isBadRequest())
    ;
  }

  @Test
  @TestDescription("입력값이 잘못된 경우 이벤트 수정 실패")
  public void updateEvent400_Wrong() throws Exception {
    //Given
    Event event = this.generateEvent(200);
    EventDto eventDto = this.modelMapper.map(event, EventDto.class);  //modelMapper로 event에 있는걸 eventDto에 담는다
    eventDto.setBasePrice(20000);
    eventDto.setMaxPrice(10000);

    //When & Then
    this.mockMvc.perform(put("/api/events/{id}", event.getId())
            .header(HttpHeaders.AUTHORIZATION, getBearerToken())
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .content(this.objectMapper.writeValueAsString(eventDto)))
            .andDo(print())
            .andExpect(status().isBadRequest())
    ;
  }

  @Test
  @TestDescription("존재하지 않는 이벤트 수정 실패")
  public void updateEvent404() throws Exception {
    //Given
    Event event = this.generateEvent(200);
    EventDto eventDto = this.modelMapper.map(event, EventDto.class);

    //When & Then
    this.mockMvc.perform(put("/api/events/123456789")
            .header(HttpHeaders.AUTHORIZATION, getBearerToken())
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .content(this.objectMapper.writeValueAsString(eventDto)))
            .andDo(print())
            .andExpect(status().isNotFound())
    ;
  }

  private Event generateEvent(int index){
    Event event = Event.builder()
            .name("event " + index)
            .description("test event")
            .beginEnrollmentDateTime(LocalDateTime.of(2019,03,26,14,00))
            .closeEnrollmentDateTime(LocalDateTime.of(2019,03,26,15,00))
            .beginEventDateTime(LocalDateTime.of(2019,03,26,15,00))
            .endEventDateTime(LocalDateTime.of(2019,03,26,16,00))
            .basePrice(100)
            .maxPrice(200)
            .limitOfEnrollment(100)
            .location("강남역 스타트업 팩토리")
            .free(false)
            .offline(true)
            .eventStatus(EventStatus.DRAFT)
            .build();

    return this.eventRepository.save(event);
  }
}
