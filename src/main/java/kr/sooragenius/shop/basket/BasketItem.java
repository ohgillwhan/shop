package kr.sooragenius.shop.basket;

import kr.sooragenius.shop.item.Item;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class BasketItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BASKET_ITEM_ID")
    private Long id;
    @ManyToOne
    @JoinColumn(name = "ITEM_ID", referencedColumnName = "ITEM_ID")
    private Item item;
    @ManyToOne
    @JoinColumn(name = "BASKET_ID", referencedColumnName = "BASKET_ID")
    private Basket basket;


    public static BasketItem of(Basket basket, Item item) {
        BasketItem basketItem = new BasketItem();
        basketItem.basket = basket;
        basketItem.item = item;

        return basketItem;
    }
}
