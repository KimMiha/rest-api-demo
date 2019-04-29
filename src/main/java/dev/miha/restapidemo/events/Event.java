package dev.miha.restapidemo.events;

import dev.miha.restapidemo.accounts.Account;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

// 컴파일 시점에 추가적인 코드들이 생성된다. Event.class 에서 확인
// lombok 어노테이션은 메타 어노테이션으로 동작하지 않는다. 즉 어노테이션 줄이기 불가능

@Builder    // 빌더를 추가하면 기본생성자가 생성이 안된다. 모든 아규먼트를 가지고 있는 디폴트 생성자로 생성됨.  퍼블릭이 아니다. 다른 위치에서 이 이벤트에 대한 객체를 만들기 애매해짐
@AllArgsConstructor @NoArgsConstructor  // 기본생성자와 모든 아규먼트를 가지고 있는 생성자를 만들기 위해 추가.
@EqualsAndHashCode(of = "id") // 모든 필드를 기본적으로 사용하는데, 서로 연관관계가 상호참조하는 관계가 되버리면 Stack Overflow 발생할 수 있다. 필드를 더 추가 가능하지만 절대로 연관관계가 있는건 X
@Getter @Setter   // @Data는 @EqualsAndHashCode도 같이 구현해 주는데 모든 프로퍼티를 가지고 전부 만들어버려서 권장하지 않음.
@Entity
public class Event {

  @Id
  @GeneratedValue
  private Integer id;
  private String name;  // 이벤트 명
  private String description; // 설명
  private LocalDateTime beginEnrollmentDateTime;  // 등록 시작일시
  private LocalDateTime closeEnrollmentDateTime;  // 종료일시
  private LocalDateTime beginEventDateTime; // 이벤트 시작일시
  private LocalDateTime endEventDateTime;   // 이벤트 종료일시
  private String location;  // (optional) 이벤트 위치 없으면 온라인 모임
  private int basePrice;  // (optional) 기본 금액
  private int maxPrice; // (optional) 최고 금액
  private int limitOfEnrollment;  // 등록한도
  private boolean offline;  // 오프라인 모임인지
  private boolean free;   // 무료인지
  @Enumerated(EnumType.STRING)  // ordinal 은 순서에 따라 숫자값이 저장되는데 순서가 바뀌면 데이터가 완전히 꼬이게되므로 string으로
  private EventStatus eventStatus = EventStatus.DRAFT;
  @ManyToOne  //단방향
  private Account manager;

  public void update() {
    //update free
    this.free = (this.basePrice == 0 && this.maxPrice == 0) ? true : false ;

    //update offline
    this.offline = (this.location == null || this.location.isBlank() ) ? false : true ; //isBlank는 11에서부터 추가되었다.
  }
}
