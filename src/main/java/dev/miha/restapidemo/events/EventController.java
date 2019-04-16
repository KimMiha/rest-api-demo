package dev.miha.restapidemo.events;

import dev.miha.restapidemo.common.ErrorsResource;
import org.modelmapper.ModelMapper;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
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

  private final EventValidator eventValidator;

  public EventController(EventRepository eventRepository, ModelMapper modelMapper, EventValidator eventValidator){
    this.eventRepository = eventRepository;
    this.modelMapper = modelMapper;
    this.eventValidator = eventValidator;
  }

  @PostMapping
  public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors){ //@valid 해당 인스턴스의 값들을 바인딩할때 검증을 수행한다. 결과는 객체 바로 오른쪽에 있는 errors객체에 넣어준다.
    if(errors.hasErrors()){
      return badRequest(errors);
    }

    eventValidator.validate(eventDto, errors);
    if(errors.hasErrors()){
      return badRequest(errors);
    }

    Event event = modelMapper.map(eventDto, Event.class); //eventDto -> event 로
    event.update(); // 서비스로 빼도 될듯
    Event newEvent = this.eventRepository.save(event);
    ControllerLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash(newEvent.getId());
    URI createdUri = selfLinkBuilder.toUri();
    EventResource eventResource = new EventResource(event);
    eventResource.add(linkTo(EventController.class).withRel("query-events"));
    eventResource.add(selfLinkBuilder.withRel("update-event"));
    eventResource.add(new Link("/docs/index.html#resources-events-create").withRel("profile"));
    return ResponseEntity.created(createdUri).body(eventResource);  //created로 보낼때는 url이 있어야한다.
    // .body(event) 로 event를 담아서 보낼수있었던 이유는? 자바빈 스팩을 따르기 때문에 beanSerializer를 사용해서 객체를 json으로 변환가능(objectMapper이)
    // .body(errors) 가 안되는 이유는 errors가 자바빈 스팩을 준수하지 않기때문에 beanSerializer 사용못함, 즉 json으로 변환 불. so, ErrorsSerializer를 만들어서해결해보자
  }

  private ResponseEntity badRequest(Errors errors) {
    return ResponseEntity.badRequest().body(new ErrorsResource(errors));
  }
}