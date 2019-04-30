package dev.miha.restapidemo.accounts;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class AccountServiceTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Autowired
  AccountService accountService;

  @Autowired
  AccountRepository accountRepository;

  @Test
  public void findByUsername() {
    // Given
    String username = "dev.mihakim@gmail.com";
    String password = "test1234";
    Account account = Account.builder()
            .email(username)
            .password(password)
            .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
            .build();
    this.accountRepository.save(account);

    // When
    UserDetailsService userDetailsService = accountService;
    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

    // Then
    assertThat(userDetails.getPassword()).isEqualTo(password);
  }

  @Test
  public void findByUsernameFail() {
    String username = "random@email.com";
    // 예외를 예상하는 것이기 때문에 먼저 예상하는 예외가 무엇인지 명시해줘야 한다.
    expectedException.expect(UsernameNotFoundException.class);
    expectedException.expectMessage(Matchers.containsString(username));

    accountService.loadUserByUsername(username);
  }
}