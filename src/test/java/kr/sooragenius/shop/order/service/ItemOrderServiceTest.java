package kr.sooragenius.shop.order.service;

import kr.sooragenius.shop.item.Item;
import kr.sooragenius.shop.item.ItemOption;
import kr.sooragenius.shop.item.dto.ItemDTO;
import kr.sooragenius.shop.item.dto.ItemOptionDTO;
import kr.sooragenius.shop.item.service.infra.ItemOptionRepository;
import kr.sooragenius.shop.item.service.infra.ItemRepository;
import kr.sooragenius.shop.member.Member;
import kr.sooragenius.shop.member.dto.MemberDTO;
import kr.sooragenius.shop.member.enums.MemberAuthority;
import kr.sooragenius.shop.member.service.infra.MemberRepository;
import kr.sooragenius.shop.order.dto.ItemOrderDTO;
import kr.sooragenius.shop.order.dto.ItemOrderDetailDTO;
import kr.sooragenius.shop.order.service.infra.ItemOrderRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemOrderServiceTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ItemOrderRepository itemOrderRepository;
    @Mock
    private ItemOptionRepository itemOptionRepository;
    @Mock
    private MemberRepository memberRepository;

    private ItemOrderService itemOrderService;
    private final PasswordEncoder passwordEncoder;

    @BeforeEach
    public void test() {
        itemOrderService = new ItemOrderService(itemRepository, itemOrderRepository, itemOptionRepository, memberRepository);
    }
    @Test
    @DisplayName("옵션없이 주문")
    void orderWithoutOption() {
        // given
        MemberDTO.Request memberRequest = MemberDTO.Request.builder()
                .name("soora")
                .id("soora")
                .password("qwer1234")
                .authority(MemberAuthority.ROLE_ADMIN)
                .build();
        Item blackKakao = createItem(1L, "blackKakao", 1000L, 100L);
        Item whiteKakao = createItem(2L, "whiteKakao", 5000L, 333L);
        Item pinkKakao = createItem(3L, "pinkKakao", 4000L, 250L);

        // when
        when(memberRepository.findById(memberRequest.getId()))
                .thenReturn(Optional.of(Member.of(memberRequest, passwordEncoder)));
        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(blackKakao));
        when(itemRepository.findById(2L))
                .thenReturn(Optional.of(whiteKakao));
        when(itemRepository.findById(3L))
                .thenReturn(Optional.of(pinkKakao));

        ItemOrderDTO.Request request = ItemOrderDTO.Request.builder()
                .memberId(memberRequest.getId())
                .orderDetailRequests(Arrays.asList(
                        ItemOrderDetailDTO.Request.builder().itemId(1L).optionId(null).build(),
                        ItemOrderDetailDTO.Request.builder().itemId(2L).optionId(null).build(),
                        ItemOrderDetailDTO.Request.builder().itemId(3L).optionId(null).build()
                ))
                .build();

        ItemOrderDTO.Response order = itemOrderService.order(request);

        // then
        assertEquals(blackKakao.getPayAmount() + pinkKakao.getPayAmount() + whiteKakao.getPayAmount(), order.getTotalPayAmount());
        assertEquals(blackKakao.getAmount() + pinkKakao.getAmount() + whiteKakao.getAmount(), order.getTotalAmount());
        assertEquals(blackKakao.getDiscountAmount() + pinkKakao.getDiscountAmount() + whiteKakao.getDiscountAmount(), order.getTotalDiscountAmount());
        assertFalse(order.getOrderDetails().isEmpty());
        assertEquals(request.getOrderDetailRequests().size(), order.getOrderDetails().size());
        order.getOrderDetails().stream().forEach(detail -> {
            Item item = itemRepository.findById(detail.getItemId()).get();

            assertEquals(item.getDiscountAmount(), detail.getDiscountAmount());
            assertEquals(item.getPayAmount(), detail.getPayAmount());
            assertEquals(item.getAmount(), detail.getAmount());
        });
    }
    @Test
    @DisplayName("옵션 추가하여 주문")
    void orderWithOption() {
        // given
        MemberDTO.Request memberRequest = MemberDTO.Request.builder()
                .name("soora")
                .id("soora")
                .password("qwer1234")
                .authority(MemberAuthority.ROLE_ADMIN)
                .build();
        Item blackKakao = createItem(1L, "blackKakao", 1000L, 100L);
        Item whiteKakao = createItem(2L, "whiteKakao", 5000L, 333L);
        Item pinkKakao = createItem(3L, "pinkKakao", 4000L, 250L);

        ItemOption blackKakaoOption = createItemOption(blackKakao, 1L,"두배로!", 500L);

        // when
        when(memberRepository.findById(memberRequest.getId()))
                .thenReturn(Optional.of(Member.of(memberRequest, passwordEncoder)));
        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(blackKakao));
        when(itemRepository.findById(2L))
                .thenReturn(Optional.of(whiteKakao));
        when(itemRepository.findById(3L))
                .thenReturn(Optional.of(pinkKakao));
        when(itemOptionRepository.findById(1L))
                .thenReturn(Optional.of(blackKakaoOption));

        ItemOrderDTO.Request request = ItemOrderDTO.Request.builder()
                .memberId(memberRequest.getId())
                .orderDetailRequests(Arrays.asList(
                        ItemOrderDetailDTO.Request.builder().itemId(1L).optionId(1L).build(),
                        ItemOrderDetailDTO.Request.builder().itemId(2L).optionId(null).build(),
                        ItemOrderDetailDTO.Request.builder().itemId(3L).optionId(null).build()
                ))
                .build();

        ItemOrderDTO.Response order = itemOrderService.order(request);

        // then
        long itemTotalPayAmount = blackKakao.getPayAmount() + pinkKakao.getPayAmount() + whiteKakao.getPayAmount();
        long itemTotalAmount = blackKakao.getAmount() + pinkKakao.getAmount() + whiteKakao.getAmount();
        long itemTotalDiscount = blackKakao.getDiscountAmount() + pinkKakao.getDiscountAmount() + whiteKakao.getDiscountAmount();
        assertEquals(itemTotalPayAmount + blackKakaoOption.getPremium()
                , order.getTotalPayAmount());
        assertEquals(itemTotalAmount + blackKakaoOption.getPremium()
                , order.getTotalAmount());
        assertEquals(itemTotalDiscount
                , order.getTotalDiscountAmount());
        assertFalse(order.getOrderDetails().isEmpty());
        assertEquals(request.getOrderDetailRequests().size(), order.getOrderDetails().size());

        order.getOrderDetails().stream().forEach(detail -> {
            Item item = itemRepository.findById(detail.getItemId()).get();
            long discountAmount = item.getDiscountAmount();
            long payAmount = item.getPayAmount();
            long amount = item.getAmount();
            if(detail.getOptionId() != null && detail.getOptionId() > 0L) {
                ItemOption itemOption = itemOptionRepository.findById(detail.getOptionId()).get();

                payAmount += itemOption.getPremium();
                amount += itemOption.getPremium();
            }

            assertEquals(discountAmount, detail.getDiscountAmount());
            assertEquals(payAmount, detail.getPayAmount());
            assertEquals(amount, detail.getAmount());
        });
    }


    private Item createItem(Long id, String name, Long amount, Long discountAmount) {
        ItemDTO.Request build = ItemDTO.Request.builder()
                .name(name)
                .amount(amount)
                .discountAmount(discountAmount)
                .build();

        Item item = Item.of(build, null);
        ReflectionTestUtils.setField(item, "id", id);

        return item;
    }
    private ItemOption createItemOption(Item item, Long id, String name, Long premium) {
        ItemOptionDTO.Request build = ItemOptionDTO.Request.builder()
                .premium(premium)
                .name(name)
                .build();
        ItemOption itemOption = ItemOption.of(build, item);
        ReflectionTestUtils.setField(itemOption, "id", id);

        return itemOption;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}