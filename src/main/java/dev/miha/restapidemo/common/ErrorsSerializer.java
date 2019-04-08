package dev.miha.restapidemo.common;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.validation.Errors;

import java.io.IOException;

@JsonComponent  //objectMapper에 이 클래스를 등록하는 어노테이션. 언제? Errors라는 객체를 Serialization할때 objectMapper가 이걸 쓴다.
public class ErrorsSerializer extends JsonSerializer<Errors> {

  @Override
  public void serialize(Errors errors, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
    jsonGenerator.writeStartArray();
    //필드에러
    errors.getFieldErrors().forEach( e -> {
     try {
       jsonGenerator.writeStartObject();  //json object 를 만들고 (start)
       jsonGenerator.writeStringField("filed", e.getField());
       jsonGenerator.writeStringField("objectName", e.getObjectName());
       jsonGenerator.writeStringField("code", e.getCode());
       jsonGenerator.writeStringField("defaultMessage", e.getDefaultMessage());
       Object rejectedValue = e.getRejectedValue(); // Rejected Value 있을수도 있고 없을 수도 있으니까
       if(rejectedValue != null){
         jsonGenerator.writeStringField("rejectedValue", rejectedValue.toString());
       }
       jsonGenerator.writeEndObject();    //닫아준다 (end)
     }catch (IOException e1){
       e1.printStackTrace();
     }
    });

    //글로벌 에러
    errors.getGlobalErrors().forEach( e -> {
      try {
        jsonGenerator.writeStartObject();  //json object 를 만들고 (start)
        jsonGenerator.writeStringField("objectName", e.getObjectName());
        jsonGenerator.writeStringField("code", e.getCode());
        jsonGenerator.writeStringField("defaultMessage", e.getDefaultMessage());
        jsonGenerator.writeEndObject();    //닫아준다 (end)
      } catch (IOException e1) {
        e1.printStackTrace();
      }
    });
    jsonGenerator.writeEndArray();
  }
}
