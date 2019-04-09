package dev.miha.restapidemo.events;


import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(JUnitParamsRunner.class) // 파라미터를 사용한 테스트코드를 만들기 쉽게 해주는 라이브러리.  JUnit에서 메소드 파라미터를 사용할수있게 해준다.
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

  @Test
  @Parameters({
          "0, 0, true",
          "100, 0, false",
          "0, 100, false",
          "100, 100, false"
  })
  public void testFree(int basePrice, int maxPrice, boolean isFree){
    //given
    Event event = Event.builder()
            .basePrice(basePrice)
            .maxPrice(maxPrice)
            .build();

    //when
    event.update();

    //then
    assertThat(event.isFree()).isEqualTo(isFree);
  }

  @Test
  @Parameters
  public void testOffline(String location, boolean isOffline){
    //given
    Event event = Event.builder()
            .location(location)
            .build();

    //when
    event.update();

    //then
    assertThat(event.isOffline()).isEqualTo(isOffline);

  }

  // parametersFor 라는게 컨벤션인데 테스트메소드 명 앞에 프리픽스로 붙여주면 알아서 찾아가 사용함.
  private Object[] parametersForTestOffline(){  // 조금더 type safe 하게
    return new Object[] {
            new Object[] {"next stage", true},
            new Object[] {null, false},
            new Object[] {"   ", false}
    };
  }

}