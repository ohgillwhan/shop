package kr.sooragenius.shop.order.dto;

import kr.sooragenius.shop.order.ItemOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

public class ItemOrderEventDTO {
    @Data
    @Builder @NoArgsConstructor @AllArgsConstructor
    public static class NewItemOrderDetail {
        private long itemId;
        private long optionId;
        private long stock;

        public static Object of(ItemOrderDetailDTO.Request detailRequest) {
            NewItemOrderDetail newItemOrderDetail = new NewItemOrderDetail();
            newItemOrderDetail.itemId = detailRequest.getItemId();
            newItemOrderDetail.optionId = detailRequest.getOptionId();
            newItemOrderDetail.stock = detailRequest.getStock();

            return newItemOrderDetail;
        }
    }
    @Data
    @Builder @NoArgsConstructor @AllArgsConstructor
    public static class NewItemOrder  implements Serializable {
        private long orderId;

        public static NewItemOrder of(ItemOrder itemOrder) {
            NewItemOrder newItemOrder = new NewItemOrder();
            newItemOrder.orderId = itemOrder.getId();

            return newItemOrder;
        }
    }

    @Data
    @Builder @NoArgsConstructor @AllArgsConstructor
    public static class NewItemOrderRollback {
        private long itemId;
        private long optionId;
        private long stock;

        public static NewItemOrderRollback of(NewItemOrderDetail detailRequest) {
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
