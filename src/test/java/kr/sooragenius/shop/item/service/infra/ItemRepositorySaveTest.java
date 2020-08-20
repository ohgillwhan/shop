package kr.sooragenius.shop.item.service.infra;

import kr.sooragenius.shop.item.Item;
import kr.sooragenius.shop.item.ItemOption;
import kr.sooragenius.shop.item.dto.ItemDTO;
import kr.sooragenius.shop.category.Category;
import kr.sooragenius.shop.category.dto.CategoryDTO;
import kr.sooragenius.shop.category.service.infra.CategoryRepository;
import kr.sooragenius.shop.item.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRepositorySaveTest {
    private final CategoryRepository categoryRepository;
    private final ItemRepository itemRepository;
    private final ItemOptionRepository itemOptionRepository;
    private final EntityManager entityManager;

    @Test
    @Transactional
    @DisplayName("상품 저장 후 flush 그리고 정상저장 확인")
    void addItem() {
        // given
        Category category = addTopCategory();

        ItemDTO.Request itemKakaoRequest = ItemDTO.Request.builder().name("Kakao").amount(1000L).discountAmount(100L).stock(1L).build();
        ItemDTO.Request itemClockRequest = ItemDTO.Request.builder().name("Clock").amount(1000L).discountAmount(100L).stock(1L).build();

        Item itemKakao = Item.of(itemKakaoRequest, category);
        Item itemClock = Item.of(itemClockRequest, category);

        // when
        itemKakao = itemRepository.save(itemKakao);
        itemClock = itemRepository.save(itemClock);

        flush();

        // then
        Map<Long, ItemDTO.Request> itemMaps = new HashMap<>();
        itemMaps.put(itemKakao.getId(), itemKakaoRequest);
        itemMaps.put(itemClock.getId(), itemClockRequest);

        for(Map.Entry<Long, ItemDTO.Request> entry : itemMaps.entrySet()) {
            Long key = entry.getKey();
            ItemDTO.Request value = entry.getValue();

            Item item = itemRepository.findById(key).get();

            assertThat(item.getId())
                    .isPositive()
                    .isEqualTo(key);

            assertThat(item.getName())
                    .isNotEmpty()
                    .isEqualTo(value.getName());

            assertThat(item.getCategory().getId())
                    .isGreaterThan(0L)
                    .isEqualTo(category.getId());

            assertThat(item.getPayAmount())
                    .isEqualTo(value.getAmount() - value.getDiscountAmount());
        }
    }
    @Test
    @Transactional
    @DisplayName("상품 저장 후 flush None옵션 확인")
    public void addItemAndNoneOption() {
        // given
        Category category = addTopCategory();

        ItemDTO.Request itemKakaoRequest = ItemDTO.Request.builder().name("Kakao").amount(1000L).discountAmount(100L).stock(1L).build();

        Item itemKakao = Item.of(itemKakaoRequest, category);

        // when
        itemKakao = itemRepository.save(itemKakao);
        flush();

        itemKakao = itemRepository.findById(itemKakao.getId()).get();
        ItemOption itemOption = itemOptionRepository.findById(itemKakao.getNoneOptionId()).get();
        // then
        assertThat(itemKakao.getId())
                .isPositive();

        assertThat(itemOption.getId())
                .isPositive();

        assertThat(itemOption.isNoneOptionAt())
                .isTrue();

        assertThat(itemOption.getPremium())
                .isEqualTo(0L);
    }

    private Category addTopCategory() {
        return categoryRepository.save(Category.of(CategoryDTO.Request.builder().name("TOP").build()));
    }
    private void flush() {
        entityManager.flush();
        entityManager.clear();
    }
}