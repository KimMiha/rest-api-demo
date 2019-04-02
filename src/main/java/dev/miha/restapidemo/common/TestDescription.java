package dev.miha.restapidemo.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//junit5에는 제공됨. 테스트하면 description 명으로 나온다.
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)  //얼마나 오래 가져갈것인가. 컴파일한 이후에 가져가진 않아도될듯하니 소스까지만
public @interface TestDescription {

  String value();
}
