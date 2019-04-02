package dev.miha.restapidemo.events;

import org.modelmapper.ModelMapper;
import org.modelmapper.internal.Errors;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.net.URI;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@Controller
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_UTF8_VALUE) //모든 핸들러는 이런HAL 응답으로 보내게 된다.
public class EventController {

  private final EventRepository eventRepository;

  private final ModelMapper modelMapper;

  public EventController(EventRepository eventRepository, ModelMapper modelMapper){
    this.eventRepository = eventRepository;
    this.modelMapper = modelMapper;
  }

  @PostMapping
  public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors){ //@valid 해당 인스턴스의 값들을 바인딩할때 검증을 수행한다. 결과는 객체 바로 오른쪽에 있는 errors객체에 넣어준다.
    if(errors.hasErrors()){
      return ResponseEntity.badRequest().build();
    }
    Event event = modelMapper.map(eventDto, Event.class); //eventDto -> event 로
    Event newEvent = this.eventRepository.save(event);
    URI createdUri = linkTo(EventController.class).slash(newEvent.getId()).toUri();
    return ResponseEntity.created(createdUri).body(event);  //created로 보낼때는 url이 있어야한다.
  }
}