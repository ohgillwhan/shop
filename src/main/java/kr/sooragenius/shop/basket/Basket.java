package kr.sooragenius.shop.basket;

import kr.sooragenius.shop.member.Member;
import lombok.Getter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
public class Basket {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BASKET_ID")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID", referencedColumnName = "MEMBER_ID")
    private Member member;

    @OneToMany(mappedBy = "basket")
    private List<BasketItem> basketItems;

    public static Basket of(Member member) {
        Basket basket = new Basket();
        basket.member = member;
        return basket;
    }
}
