package kr.sooragenius.shop.review.service.infra;

import kr.sooragenius.shop.item.Item;
import kr.sooragenius.shop.item.dto.ItemDTO;
import kr.sooragenius.shop.category.Category;
import kr.sooragenius.shop.category.dto.CategoryDTO;
import kr.sooragenius.shop.category.service.infra.CategoryRepository;
import kr.sooragenius.shop.item.dto.ItemOptionDTO;
import kr.sooragenius.shop.item.service.infra.ItemRepository;
import kr.sooragenius.shop.review.Review;
import kr.sooragenius.shop.review.dto.ReviewDTO;
import kr.sooragenius.shop.review.enums.ScoreEnums;
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
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ReviewRepositoryTest {
    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;
    private final ReviewRepository reviewRepository;
    private final EntityManager entityManager;

    @Test
    @Transactional
    @DisplayName("상품의 리뷰 추가 그리고 flush 후 재검증")
    void addReview() {
        //given
        Category category = addTopCategory();
        Item item = addKakaoItem(category);

        ReviewDTO.Request niceRequest = ReviewDTO.Request.builder().contents("nice").score(ScoreEnums.GOOD).build();
        ReviewDTO.Request badRequest = ReviewDTO.Request.builder().contents("bad").score(ScoreEnums.BAD).build();
        ReviewDTO.Request normalRequest = ReviewDTO.Request.builder().contents("normal").score(ScoreEnums.NORMAL).build();

        Review nice = Review.of(niceRequest, item);
        Review bad = Review.of(badRequest, item);
        Review normal = Review.of(normalRequest, item);

        // when
        nice = reviewRepository.save(nice);
        bad = reviewRepository.save(bad);
        normal = reviewRepository.save(normal);

        flush();

        // then
        Map<Long, ReviewDTO.Request> itemMaps = new HashMap<>();
        itemMaps.put(nice.getId(), niceRequest);
        itemMaps.put(bad.getId(), badRequest);
        itemMaps.put(normal.getId(), normalRequest);

        for(Map.Entry<Long, ReviewDTO.Request> entry : itemMaps.entrySet()) {
            Long key = entry.getKey();
            ReviewDTO.Request value = entry.getValue();

            Review review = reviewRepository.findById(key).get();

            assertThat(review.getId())
                    .isPositive()
                    .isEqualTo(key);

            assertThat(review.getContents())
                    .isNotEmpty()
                    .isEqualTo(value.getContents());

            assertThat(review.getItem().getId())
                    .isNotNull()
                    .isEqualTo(item.getId());

            assertThat(review.getScore())
                    .isNotNull()
                    .isEqualTo(value.getScore());

        }
    }
    private Category addTopCategory() {
        return categoryRepository.save(Category.of(CategoryDTO.Request.builder().name("TOP").build()));
    }
    private Item addKakaoItem(Category category) {
        return itemRepository.save(Item.of(ItemDTO.Request.builder().name("Kakao").amount(1000L).discountAmount(100L).stock(1L).build(), category));
    }
    private void flush() {
        entityManager.flush();
        entityManager.clear();
    }
}