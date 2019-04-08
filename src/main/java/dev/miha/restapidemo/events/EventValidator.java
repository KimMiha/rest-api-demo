package dev.miha.restapidemo.events;


import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.time.LocalDateTime;

@Component  //빈으로 등록
public class EventValidator {

  public void validate(EventDto eventDto, Errors errors){
    if(eventDto.getBasePrice() > eventDto.getMaxPrice()   // 무제한 경매인 경우
            && eventDto.getMaxPrice() > 0) {  //위배 상황
//      errors.rejectValue("basePrice","wrongValue", "BasePrice is wrong.");  //filed error
//      errors.rejectValue("maxPrice","wrongValue", "MaxPrice is wrong.");
      errors.reject("wrongPrices", "Values of prices are wrong");   // global error
    }

    LocalDateTime endEventDateTime = eventDto.getEndEventDateTime();
    if(endEventDateTime.isBefore(eventDto.getBeginEventDateTime()) ||
    endEventDateTime.isBefore(eventDto.getCloseEnrollmentDateTime()) ||
    endEventDateTime.isBefore(eventDto.getBeginEnrollmentDateTime())){
      errors.rejectValue("endEventDateTime", "wrongValue", "endEventDateTime is wrong.");
    }

    // TODO BeginEventDateTime
    // TODO CloseEnrollmentDateTime

  }

}
