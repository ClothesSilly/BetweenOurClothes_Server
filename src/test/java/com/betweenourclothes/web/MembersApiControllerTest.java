package com.betweenourclothes.web;

import com.betweenourclothes.domain.members.Members;
import com.betweenourclothes.domain.members.MembersRepository;
import com.betweenourclothes.web.dto.MembersSaveRequestDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MembersApiControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MembersRepository membersRepository;

    //@After
    /*public void cleanup(){
        membersRepository.deleteAll();
    }*/

    @Test
    public void 회원등록() throws Exception{
        String email = "abcd12345@naver.com";
        String password = "qwer1234";
        String name = "이름2";
        String nickname = "닉넴2";
        String phone = "00033334444";

        MembersSaveRequestDto requestDto = MembersSaveRequestDto.builder().email(email).password(password).name(name).nickname(nickname).phone(phone).build();

        String url = "http://localhost:" + port + "api/v1/members";

        ResponseEntity<Long> responseEntity = restTemplate.postForEntity(url, requestDto, Long.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isGreaterThan(0L);

        List<Members> all = membersRepository.findAll();
        assertThat(all.get(all.size()-1).getEmail()).isEqualTo(email);
    }
}
