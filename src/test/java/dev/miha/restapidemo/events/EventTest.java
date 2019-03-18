package dev.miha.restapidemo.events;


import org.junit.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class EventTest {

  @Test
  public void builder(){
    Event event = Event.builder()
            .name("Inflearn Spring REST API")
            .description("빌더의 좋은점-무엇에 대한 문자열인지 알기 쉬움. 이건 description!")
            .build();    //빌더가 있는지 먼저 본다.
    assertThat(event).isNotNull();
  }

  // 원하는것: 자바 빈 스펙도 준수해야 한다. 디폴트 생성자도 만들수 있어야한다.
  @Test
  public void javaBean(){
    // given
    String name = "Event";
    String description = "Spring";

    // when
    Event event = new Event();
    event.setName(name);
    event.setDescription(description);

    // then
    assertThat(event.getName()).isEqualTo(name);
    assertThat(event.getName()).isEqualTo(description);
  }

}