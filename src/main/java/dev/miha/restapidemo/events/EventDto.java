package dev.miha.restapidemo.events;

import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/*
 * Event 에 너무 많은 어노테이션을 쓰게되어 입력용 Dto 생성. 단점은 중복이 생기는것?
 */
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class EventDto {

  @NotEmpty
  private String name;  // 이벤트 명
  @NotEmpty
  private String description; // 설명
  @NotNull
  private LocalDateTime beginEnrollmentDateTime;  // 등록 시작일시
  @NotNull
  private LocalDateTime closeEnrollmentDateTime;  // 종료일시
  @NotNull
  private LocalDateTime beginEventDateTime; // 이벤트 시작일시
  @NotNull
  private LocalDateTime endEventDateTime;   // 이벤트 종료일시
  private String location;  // (optional) 이벤트 위치 없으면 온라인 모임
  @Min(0)
  private int basePrice;  // (optional) 기본 금액
  @Min(0)
  private int maxPrice; // (optional) 최고 금액
  @Min(0)
  private int limitOfEnrollment;  // 등록한도

}
