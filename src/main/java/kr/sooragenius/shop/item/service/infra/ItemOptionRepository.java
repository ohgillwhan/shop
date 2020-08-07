package kr.sooragenius.shop.item.service.infra;

import kr.sooragenius.shop.item.ItemOption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemOptionRepository extends JpaRepository<ItemOption, Long> {
}
