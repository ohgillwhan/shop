package kr.sooragenius.shop.basket.service.infra;

import kr.sooragenius.shop.basket.Basket;
import kr.sooragenius.shop.member.Member;
import kr.sooragenius.shop.member.dto.MemberDTO;
import kr.sooragenius.shop.member.enums.MemberAuthority;
import kr.sooragenius.shop.member.service.infra.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BasketRepositoryTest {
    private final BasketRepository basketRepository;
    private final MemberRepository memberRepository;
    private final EntityManager entityManager;




    @Test
    @Transactional
    @DisplayName("장바구니 추가 후 flush 그리고 재확인")
    public void addBasket() {
        // given
        Member member = addMember();
        // when
        Basket basket = basketRepository.save(Basket.of(member));
        flush();

        basket = basketRepository.findById(basket.getId()).get();
        // then
        assertThat(basket.getId())
                .isPositive();
        assertThat(basket.getMember().getId())
                .isNotEmpty()
                .isEqualTo(member.getId());
    }

    private PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    private Member addMember() {
        MemberDTO.Request request = MemberDTO.Request.builder().authority(MemberAuthority.ROLE_ADMIN).id("A1").name("A1").password("A1").build();

        Member save = memberRepository.save(Member.of(request, passwordEncoder()));
        return save;
    }
    private void flush() {
        entityManager.flush();
        entityManager.clear();
    }
}