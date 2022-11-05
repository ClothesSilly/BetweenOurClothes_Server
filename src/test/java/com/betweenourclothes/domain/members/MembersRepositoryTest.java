package com.betweenourclothes.domain.members;

import com.betweenourclothes.domain.members.repository.MembersRepository;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MembersRepositoryTest {

    @Autowired
    MembersRepository membersRepository;

    //@After
    /*public void cleanup(){
        membersRepository.deleteAll();
    }*/

    @Test
    @Ignore
    public void 멤버_등록_불러오기(){
        //given
        String email = "ddd@naver.com";
        String password = "1234";
        String name = "권송아";
        String nickname = "닉넴";
        String phone = "01012345678";

        membersRepository.save(Members.builder()
                .email(email)
                .password(password)
                .name(name)
                .nickname(nickname)
                .phone(phone).build());

        List<Members> membersList = membersRepository.findAll();

        Members member = membersList.get(membersList.size()-1);
        assertThat(member.getEmail()).isEqualTo(email);
    }

}
