package kr.sooragenius.shop.order;

import kr.sooragenius.shop.item.Item;
import kr.sooragenius.shop.item.ItemOption;
import kr.sooragenius.shop.member.Member;
import kr.sooragenius.shop.order.dto.ItemOrderDetailDTO;
import lombok.Getter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class ItemOrder {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ITEM_ORDER_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID", referencedColumnName = "MEMBER_ID")
    private Member member;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, mappedBy = "itemOrder", orphanRemoval = true)
    private List<ItemOrderDetail> itemOrderDetails = new ArrayList<>();

    private long totalAmount;
    private long totalDiscountAmount;
    private long totalPayAmount;

    public static ItemOrder of(Member member) {
        ItemOrder itemOrder = new ItemOrder();
        itemOrder.member = member;

        return itemOrder;
    }
    public ItemOrderDetailDTO.ResponseFromOrder addOrderDetails(Item item, ItemOption option) {
        ItemOrderDetail detail = ItemOrderDetail.of(item, option, this);
        getItemOrderDetails().add(detail);

        totalAmount += detail.getAmount();
        totalDiscountAmount += detail.getDiscountAmount();
        totalPayAmount += detail.getPayAmount();

        return ItemOrderDetailDTO.ResponseFromOrder.of(detail);
    }
    public void removeOrderDetails(ItemOrderDetail detail) {
        getItemOrderDetails().remove(detail);

        totalAmount -= detail.getAmount();
        totalDiscountAmount -= detail.getDiscountAmount();
        totalPayAmount -= detail.getPayAmount();
    }
}
