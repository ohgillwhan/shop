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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
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

        ItemDTO.Request itemKakaoRequest = ItemDTO.Request.builder().name("Kakao").price(1000L).discount(100L).build();
        Item itemKakao = Item.of(itemKakaoRequest, category);

        Long kakaoId = itemRepository.save(itemKakao).getId();

        ItemOptionDTO.Request kakaoDarkOption = ItemOptionDTO.Request.builder().name("DARK").premium(10000L).build();
        ItemOptionDTO.Request kakaoWhiteOption = ItemOptionDTO.Request.builder().name("WHITE").premium(20000L).build();

        ItemOption itemDarkOption = ItemOption.of(kakaoDarkOption, itemKakao);
        ItemOption itemWhiteOption = ItemOption.of(kakaoWhiteOption, itemKakao);
        // when

        Long darkOption = itemOptionRepository.save(itemDarkOption).getId();
        Long whiteOption = itemOptionRepository.save(itemWhiteOption).getId();

        flush();

        Map<Long, ItemOptionDTO.Request> optionsMap = new HashMap<>();
        optionsMap.put(darkOption, kakaoDarkOption);
        optionsMap.put(whiteOption, kakaoWhiteOption);

        // then
        optionsMap.entrySet().stream().forEach(entry -> {
            Long key = entry.getKey();
            ItemOptionDTO.Request value = entry.getValue();

            ItemOption itemOption = itemOptionRepository.findById(key).get();

            assertEquals(value.getName(), itemOption.getName());
            assertEquals(value.getPremium(), itemOption.getPremium());
            assertEquals(itemKakao.getId(), itemOption.getItem().getId());
        });
    }

    private Category addTopCategory() {
        return categoryRepository.save(Category.of(CategoryDTO.Request.builder().name("TOP").build()));
    }

    private void flush() {
        entityManager.flush();
        entityManager.clear();
    }
}