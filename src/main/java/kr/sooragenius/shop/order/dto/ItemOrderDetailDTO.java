package kr.sooragenius.shop.order.dto;

import kr.sooragenius.shop.order.ItemOrderDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class ItemOrderDetailDTO {
    @Data
    @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Request {
        private Long optionId;
        private long itemId;
    }
    @Data
    public static class ResponseFromOrder {
        private Long optionId;
        private long itemId;
        private long amount;
        private long discountAmount;
        private long payAmount;

        public static ResponseFromOrder of(ItemOrderDetail itemOrderDetail) {
            ResponseFromOrder responseFromOrder = new ResponseFromOrder();

            responseFromOrder.itemId = itemOrderDetail.getItem().getId();
            responseFromOrder.amount = itemOrderDetail.getAmount();
            responseFromOrder.discountAmount = itemOrderDetail.getDiscountAmount();
            responseFromOrder.payAmount = itemOrderDetail.getPayAmount();

            if(itemOrderDetail.getItemOption() != null) {
                responseFromOrder.optionId = itemOrderDetail.getItemOption().getId();
            }

            return responseFromOrder;
        }
    }
}
