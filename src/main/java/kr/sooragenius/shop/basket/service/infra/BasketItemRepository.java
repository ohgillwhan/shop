package kr.sooragenius.shop.basket.service.infra;

import kr.sooragenius.shop.basket.BasketItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BasketItemRepository extends JpaRepository<BasketItem, Long> {
}
