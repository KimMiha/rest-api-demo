package dev.miha.restapidemo.accounts;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter @Setter @EqualsAndHashCode
@Builder @NoArgsConstructor @AllArgsConstructor
public class Account {

  @Id
  @GeneratedValue
  private Integer id;

  private String email;

  private String password;

  @ElementCollection(fetch = FetchType.EAGER) // default 는 LAZY. 이 경우는 필요한 정보고 갯수가 많지 않으니 EAGER 로.
  @Enumerated(EnumType.STRING)
  private Set<AccountRole> roles;
}
