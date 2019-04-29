package dev.miha.restapidemo.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)  //다른 스프링 빈 설정파일을 읽어와서 사용하는 방법중 하나.
@ActiveProfiles("test") // test라는 Profiles로 실행하겠다고 알려주면 기본으로 설정해논것 + test시 사용할 Profiles 도 같이 사용하게 된다.
@Ignore // 테스트를 가지고있는 클래스가 아니므로
public class BaseControllerTest {
  //mockMvc를 사용하면 mocking이 되어있는 dispatcher servlet을 상대로 가짜요청과 응답을, 가짜 요청을 만들어서 dispatcher servlet에 보내고 그 응답을 확인할수있는 테스트를 만들수있다.
  //웹과 관련된것(빈)만 등록하기때문에, 슬라이싱 테스트라고도 한다. 계층별로..나눠져서 테스트용 빈들을 등록해서 조금더 빠르다. 구역을 나눠서 테스트하는 거라고 보면되지만, 단위테스트라고 하기에는 어렵다. 너무 많은게 개임되어있어서.
  @Autowired
  protected MockMvc mockMvc;  // mockMvc 클래스는 요청을 만들수있고 응답을 검증할수있는 Spring MVC Test에 있어 핵심적인 클래스중하나
  // 웹서버를 띄우지 않기때문에 빠르 dispatcher servlet이란것까지 만들어야해서 단위테스트보다 빠르진 않다

  @Autowired
  protected ObjectMapper objectMapper;

  @Autowired
  protected ModelMapper modelMapper;

}
