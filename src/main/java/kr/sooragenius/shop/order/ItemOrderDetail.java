package kr.sooragenius.shop.order;

import kr.sooragenius.shop.item.Item;
import kr.sooragenius.shop.item.ItemOption;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class ItemOrderDetail {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ITEM_ORDER_DETAIL_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ITEM_ORDER_ID", referencedColumnName = "ITEM_ORDER_ID")
    private ItemOrder itemOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ITEM_OPTION_ID", referencedColumnName = "ITEM_OPTION_ID")
    private ItemOption itemOption;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ITEM_ID", referencedColumnName = "ITEM_ID")
    private Item item;

    private long amount;
    private long discountAmount;
    private long payAmount;

    protected ItemOrderDetail() {}
    protected static ItemOrderDetail of(Item item, ItemOption itemOption, ItemOrder itemOrder) {
        ItemOrderDetail itemOrderDetail = new ItemOrderDetail();
        itemOrderDetail.item = item;
        itemOrderDetail.itemOrder = itemOrder;
        itemOrderDetail.itemOption = itemOption;
        itemOrderDetail.amount = item.getAmount();
        itemOrderDetail.discountAmount = item.getDiscountAmount();
        itemOrderDetail.payAmount = item.getPayAmount();

        if(itemOption != null) {
            itemOrderDetail.amount += itemOption.getPremium();
            itemOrderDetail.payAmount += itemOption.getPremium();
        }

        return itemOrderDetail;
    }
}
