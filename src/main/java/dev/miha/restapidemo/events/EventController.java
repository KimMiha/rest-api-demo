package dev.miha.restapidemo.events;

import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.URI;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@Controller
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_UTF8_VALUE) //모든 핸들러는 이런HAL 응답으로 보내게 된다.
public class EventController {

  @PostMapping
  public ResponseEntity createEvent(@RequestBody Event event){
    URI createdUri = linkTo(EventController.class).slash("{id}").toUri();
    event.setId(10);
    return ResponseEntity.created(createdUri).body(event);  //created로 보낼때는 url이 있어야한다.
  }
}