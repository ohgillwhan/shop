package kr.sooragenius.shop.item.service.infra;

import kr.sooragenius.shop.category.Category;
import kr.sooragenius.shop.category.dto.CategoryDTO;
import kr.sooragenius.shop.category.service.infra.CategoryRepository;
import kr.sooragenius.shop.item.Item;
import kr.sooragenius.shop.item.ItemOption;
import kr.sooragenius.shop.item.dto.ItemDTO;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemOptionRepositoryStockTest {
    private final CategoryRepository categoryRepository;
    private final ItemRepository itemRepository;
    private final ItemOptionRepository itemOptionRepository;
    private final EntityManager entityManager;

    @Test
    @Transactional
    @DisplayName("재고 부족으로 셋팅 후 flush 그리고 에러 검증")
    public void errorMinusStockByIdWithLock() {
        // given
        Category category = addTopCategory();

        ItemDTO.Request itemKakaoRequest = ItemDTO.Request.builder().name("Kakao").amount(1000L).discountAmount(100L).stock(1L).build();

        Item itemKakao = itemRepository.save(Item.of(itemKakaoRequest, category));
        // when
        Item item = itemRepository.findById(itemKakao.getId()).get();

        int updatedCount = itemOptionRepository.minusStockByIdWithLock(item.getNoneOptionId(), 2L);

        // then
        assertThat(updatedCount)
                .isEqualTo(0L);
    }

    @Test
    @Transactional
    @DisplayName("재고 충분으로 셋팅 후 flush 그리고 검증")
    public void minusStockByIdWithLock() {
        // given
        Category category = addTopCategory();

        ItemDTO.Request itemKakaoRequest = ItemDTO.Request.builder().name("Kakao").amount(1000L).discountAmount(100L).stock(2L).build();

        Item item = itemRepository.save(Item.of(itemKakaoRequest, category));
        flush();
        // when
        item = itemRepository.findById(item.getId()).get();

        int updatedCount = itemOptionRepository.minusStockByIdWithLock(item.getNoneOptionId(), 2L);

        // then
        assertThat(updatedCount)
                .isEqualTo(1L);
    }

    private Category addTopCategory() {
        return categoryRepository.save(Category.of(CategoryDTO.Request.builder().name("TOP").build()));
    }
    private void flush() {
        entityManager.flush();
        entityManager.clear();
    }
}