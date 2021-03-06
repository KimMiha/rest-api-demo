package dev.miha.restapidemo.events;

import dev.miha.restapidemo.accounts.Account;
import dev.miha.restapidemo.accounts.CurrentUser;
import dev.miha.restapidemo.common.ErrorsResource;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

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
  public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, //@valid 해당 인스턴스의 값들을 바인딩할때 검증을 수행한다. 결과는 객체 바로 오른쪽에 있는 errors객체에 넣어준다.
                                    Errors errors,
                                    @CurrentUser Account currentUser ){
    if(errors.hasErrors()){
      return badRequest(errors);
    }

    eventValidator.validate(eventDto, errors);
    if(errors.hasErrors()){
      return badRequest(errors);
    }

    Event event = modelMapper.map(eventDto, Event.class); //eventDto -> event 로
    event.update(); // 서비스로 빼도 될듯
    event.setManager(currentUser);
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

  @GetMapping
  public ResponseEntity queryEvents(Pageable pageable,
                                    PagedResourcesAssembler<Event> assembler,
                                    //@AuthenticationPrincipal(expression = "account") Account account //이 어노테이션은 getPrincipal 로 리턴받은 객체를 바로 주입받을 수 있다. expression - accountApater중에 account라는 속성을 가져옴
                                    @CurrentUser Account account // 윗 줄의 내용을 어노테이션을 만들어서 줄임.
                                    )  {
    Page<Event> page = this.eventRepository.findAll(pageable);
    var pagedResources = assembler.toResource(page, e -> new EventResource(e)); // 각가의 이벤트를 이벤트 리소스로 변경. 이벤트마다 이동할수 있는 링크가 생긴다.
    pagedResources.add(new Link("/docs/index.html#resources-events-list").withRel("profile"));
    if(account != null){
      pagedResources.add(linkTo(EventController.class).withRel("create-event"));
    }
    return ResponseEntity.ok(pagedResources);
  }

  @GetMapping("/{id}")
  public ResponseEntity getEvent(@PathVariable Integer id, @CurrentUser Account currentUser ){
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    Optional<Event> optionalEvent =  this.eventRepository.findById(id);
    if(optionalEvent.isEmpty()){
      return ResponseEntity.notFound().build();
    }

    Event event = optionalEvent.get();
    EventResource eventResource = new EventResource(event);
    eventResource.add(new Link("/docs/index.html#resources-events-get").withRel("profile"));
    if(event.getManager().equals(currentUser)) {
      eventResource.add(linkTo(EventController.class).slash(event.getId()).withRel("update-event"));
    }
    return ResponseEntity.ok(eventResource);
  }

  @PutMapping("/{id}")
  public ResponseEntity updateEvent(@PathVariable Integer id,
                                    @RequestBody @Valid EventDto eventDto, Errors errors,
                                    @CurrentUser Account currentUser) {
    Optional<Event> optionalEvent = this.eventRepository.findById(id);
    if(optionalEvent.isEmpty()){
      return ResponseEntity.notFound().build();
    }

    if(errors.hasErrors()){
      return badRequest(errors);
    }

    this.eventValidator.validate(eventDto, errors);
    if(errors.hasErrors()){
      return badRequest(errors);
    }

    Event existingEvent = optionalEvent.get();
    if(!existingEvent.getManager().equals(currentUser)){
      return new ResponseEntity(HttpStatus.UNAUTHORIZED);
    }

    this.modelMapper.map(eventDto, existingEvent);  // map(어디에서, 어디로) 데이터를 부음
    Event savedEvent = this.eventRepository.save(existingEvent);

    EventResource eventResource = new EventResource(savedEvent);
    eventResource.add(new Link("/docs/index.html#resources-events-update").withRel("profile"));

    return ResponseEntity.ok(eventResource);
  }

  private ResponseEntity badRequest(Errors errors) {
    return ResponseEntity.badRequest().body(new ErrorsResource(errors));
  }
}