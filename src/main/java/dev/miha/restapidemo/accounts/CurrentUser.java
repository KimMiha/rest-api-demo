package dev.miha.restapidemo.accounts;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER) //마라미터에 붙일수있고
@Retention(RetentionPolicy.RUNTIME) //언제까지 이 어노테이션정보를 유지할 것인가 ? 런타임까지 필요하다.
//@AuthenticationPrincipal(expression = "account")  //  account getter 인 셈. 인증정보가 있어야만
@AuthenticationPrincipal(expression = "#this == 'anonymousUser' ? null : account") // anonymousUser 는 단순한 String이라 거기에 맞는 대응. 인증정보가 있던 없던 잘 동작. (SpEL의 유연함)
public @interface CurrentUser {
}
