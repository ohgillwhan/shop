package kr.sooragenius.shop.category.service.infra;

import kr.sooragenius.shop.category.Category;
import kr.sooragenius.shop.category.dto.CategoryDTO;
import kr.sooragenius.shop.category.service.infra.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CategoryRepositoryTest {
    private final CategoryRepository categoryRepository;
    private final EntityManager entityManager;

    @Test
    @Transactional
    @DisplayName("최상위 카테고리 추가 후 flush 그리고 다시 확인")
    void saveTopCategory() {
        // given
        CategoryDTO.Request request = CategoryDTO.Request.builder().name("TOP").build();

        // when
        Category top = categoryRepository.save(Category.of(request));
        flush();

        top = categoryRepository.findById(top.getId()).get();

        // then
        assertThat(top.getId())
                .isPositive()
                .isEqualTo(top.getParent().getId());

        assertThat(top.getName())
                .isNotEmpty()
                .isEqualTo(top.getName());
    }
    @Test
    @Transactional
    @DisplayName("부모 카테고리 포함하여 추가 후 flush 그리고 다시 확인")
    void addWithParent() {
        // given
        CategoryDTO.Request parentRequest = CategoryDTO.Request.builder().name("parent").build();
        CategoryDTO.Request childRequest = CategoryDTO.Request.builder().name("TOP").build();

        Category parent = categoryRepository.save(Category.of(parentRequest));
        Category child = Category.of(childRequest, parent);

        // when
        child = categoryRepository.save(child);
        flush();

        child = categoryRepository.findById(child.getId()).get();

        // then
        assertThat(child.getId())
                .isPositive();

        assertThat(child.getParent().getId())
                .isPositive()
                .isEqualTo(parent.getId());
    }

    private void flush() {
        entityManager.flush();
        entityManager.clear();
    }
}