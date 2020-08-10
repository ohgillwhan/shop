package kr.sooragenius.shop.order.service.infra;

import kr.sooragenius.shop.order.ItemOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemOrderRepository extends JpaRepository<ItemOrder,Long> {
}
