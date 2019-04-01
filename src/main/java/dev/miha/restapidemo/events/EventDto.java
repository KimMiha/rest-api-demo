package dev.miha.restapidemo.events;

import lombok.*;

import java.time.LocalDateTime;

/*
 * Event 에 너무 많은 어노테이션을 쓰게되어 입력용 Dto 생성. 단점은 중복이 생기는것?
 */
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class EventDto {

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

}
