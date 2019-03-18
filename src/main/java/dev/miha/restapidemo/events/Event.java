package dev.miha.restapidemo.events;

import lombok.*;

import java.time.LocalDateTime;

// 컴파일 시점에 추가적인 코드들이 생성된다. Event.class 에서 확인
// lombok 어노테이션은 메타 어노테이션으로 동작하지 않는다. 즉 어노테이션 줄이기 불가능

@Builder    // 빌더를 추가하면 기본생성자가 생성이 안된다. 모든 아규먼트를 가지고 있는 디폴트 생성자로 생성됨.  퍼블릭이 아니다. 다른 위치에서 이 이벤트에 대한 객체를 만들기 애매해짐
@AllArgsConstructor @NoArgsConstructor  // 기본생성자와 모든 아규먼트를 가지고 있는 생성자를 만들기 위해 추가.
@EqualsAndHashCode(of = "id") // 모든 필드를 기본적으로 사용하는데, 서로 연관관계가 상호참조하는 관계가 되버리면 Stack Overflow 발생할 수 있다. 필드를 더 추가 가능하지만 절대로 연관관계가 있는건 X
@Getter @Setter   // @Data는 @EqualsAndHashCode도 같이 구현해 주는데 모든 프로퍼티를 가지고 전부 만들어버려서 권장하지 않음.
public class Event {

  private Integer id;
  private String name;
  private String description;
  private LocalDateTime beginEnrollmentDateTime;
  private LocalDateTime closeEnrollmentDateTime;
  private LocalDateTime beginEventDateTime;
  private LocalDateTime endEventDateTime;
  private String location; // (optional) 이게 없으면 온라인 모임
  private int basePrice; // (optional)
  private int maxPrice; // (optional)
  private int limitOfEnrollment;
  private boolean offline;
  private boolean free;
  private EventStatus eventStatus;

}
