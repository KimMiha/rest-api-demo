package dev.miha.restapidemo.events;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.springframework.hateoas.ResourceSupport;

public class EventResource extends ResourceSupport {

  @JsonUnwrapped  // event 로 감싸진 결과가 나타나는데, 그게 싫다면 이 어노테이션을 쓴다.
  private Event event;

  public EventResource(Event event){
    this.event = event;
  }

  public  Event getEvent(){
    return event;
  }
}
