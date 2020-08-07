package kr.sooragenius.shop.basket.service.infra;

import kr.sooragenius.shop.basket.Basket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BasketRepository extends JpaRepository<Basket, Long> {
}
