package kr.sooragenius.shop.item.service.infra;

import kr.sooragenius.shop.category.Category;
import kr.sooragenius.shop.category.dto.CategoryDTO;
import kr.sooragenius.shop.category.service.infra.CategoryRepository;
import kr.sooragenius.shop.item.Item;
import kr.sooragenius.shop.item.ItemOption;
import kr.sooragenius.shop.item.dto.ItemDTO;
import kr.sooragenius.shop.item.dto.ItemOptionDTO;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemOptionRepositoryTest {
    private final CategoryRepository categoryRepository;
    private final ItemRepository itemRepository;
    private final ItemOptionRepository itemOptionRepository;
    private final EntityManager entityManager;

    @Test
    @Transactional
    @DisplayName("상품 옵션 저장")
    void addItem() {
        // given
        Category category = addTopCategory();

        ItemDTO.Request itemKakaoRequest = ItemDTO.Request.builder().name("Kakao").amount(1000L).discountAmount(100L).stock(1L).build();

        Item itemKakao = itemRepository.save(Item.of(itemKakaoRequest, category));

        ItemOptionDTO.Request kakaoDarkOption = ItemOptionDTO.Request.builder().name("DARK").premium(10000L).build();
        ItemOptionDTO.Request kakaoWhiteOption = ItemOptionDTO.Request.builder().name("WHITE").premium(20000L).build();

        ItemOption itemDarkOption = ItemOption.of(kakaoDarkOption, itemKakao);
        ItemOption itemWhiteOption = ItemOption.of(kakaoWhiteOption, itemKakao);

        // when
        ItemOption darkOption = itemOptionRepository.save(itemDarkOption);
        ItemOption whiteOption = itemOptionRepository.save(itemWhiteOption);

        flush();

        Map<Long, ItemOptionDTO.Request> optionsMap = new HashMap<>();

        optionsMap.put(darkOption.getId(), kakaoDarkOption);
        optionsMap.put(whiteOption.getId(), kakaoWhiteOption);

        // then
        for(Map.Entry<Long,ItemOptionDTO.Request> entry : optionsMap.entrySet()) {
            Long key = entry.getKey();
            ItemOptionDTO.Request value = entry.getValue();

            ItemOption itemOption = itemOptionRepository.findById(key).get();

            assertThat(itemOption.getName())
                    .isNotEmpty()
                    .isEqualTo(value.getName());

            assertThat(itemOption.getPremium())
                    .isPositive()
                    .isEqualTo(value.getPremium());

            assertThat(itemOption.getItem().getId())
                    .isNotNull()
                    .isEqualTo(itemKakao.getId());
        }
    }

    private Category addTopCategory() {
        return categoryRepository.save(Category.of(CategoryDTO.Request.builder().name("TOP").build()));
    }

    private void flush() {
        entityManager.flush();
        entityManager.clear();
    }
}