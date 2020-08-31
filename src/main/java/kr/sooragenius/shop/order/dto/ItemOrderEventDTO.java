package kr.sooragenius.shop.order.dto;

import kr.sooragenius.shop.order.ItemOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.ApplicationEvent;

public class ItemOrderEventDTO {
    @Data
    @Builder @NoArgsConstructor @AllArgsConstructor
    public static class NewItemOrder {
        private long itemId;
        private long optionId;
        private long stock;

        public static Object of(ItemOrderDetailDTO.Request detailRequest) {
            NewItemOrder newItemOrder = new NewItemOrder();
            newItemOrder.itemId = detailRequest.getItemId();
            newItemOrder.optionId = detailRequest.getOptionId();
            newItemOrder.stock = detailRequest.getStock();

            return newItemOrder;
        }
    }

    @Data
    @Builder @NoArgsConstructor @AllArgsConstructor
    public static class NewItemOrderRollback {
        private long itemId;
        private long optionId;
        private long stock;

        public static NewItemOrderRollback of(ItemOrderDetailDTO.Request detailRequest) {
            NewItemOrderRollback newItemOrder = new NewItemOrderRollback();
            newItemOrder.itemId = detailRequest.getItemId();
            newItemOrder.optionId = detailRequest.getOptionId();
            newItemOrder.stock = detailRequest.getStock();

            return newItemOrder;
        }

    }

    @Data
    @Builder @NoArgsConstructor @AllArgsConstructor
    public static class ItemCancel {
        private long itemId;
        private long optionId;
        private long stock;
        public static ItemCancel of(ItemOrderDetailDTO.Response response) {
            ItemCancel itemCancel = new ItemCancel();
            itemCancel.itemId = response.getItemId();
            itemCancel.optionId = response.getOptionId();
            itemCancel.stock = response.getStock();

            return itemCancel;
        }
    }
}
