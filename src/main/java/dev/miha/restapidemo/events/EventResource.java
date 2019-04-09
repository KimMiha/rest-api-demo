package dev.miha.restapidemo.events;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

public class EventResource extends Resource<Event> {      //ResourceSupport 하위에 있는 클래스. get에 @JsonUnwrapped 가 이미 선언되어있음.

  public EventResource(Event event, Link... links) {
    super(event, links);
    add(linkTo(EventController.class).slash(event.getId()).withSelfRel());
  }
}
