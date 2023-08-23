package hello.servlet.domain.member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
public class Member {

    private Long id;
    private String username;
    private int age;

    Member (String username, int age) {

    }
}
