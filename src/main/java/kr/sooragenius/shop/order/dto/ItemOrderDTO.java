package kr.sooragenius.shop.order.dto;

import kr.sooragenius.shop.order.ItemOrder;
import kr.sooragenius.shop.order.ItemOrderDetail;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ItemOrderDTO {
    @Data
    @AllArgsConstructor @NoArgsConstructor @Builder
    public static class Request {
        private String memberId;
        private List<ItemOrderDetailDTO.Request> orderDetailRequests;
    }
    @Data
    public static class Response {
        private String memberId;
        private long totalAmount;
        private long totalDiscountAmount;
        private long totalPayAmount;
        private List<ItemOrderDetailDTO.ResponseFromOrder> orderDetails;

        public static Response of(ItemOrder itemOrder, List<ItemOrderDetail> orderDetails) {
            Response response = new Response();

            response.memberId = itemOrder.getMember().getId();
            response.totalAmount = itemOrder.getTotalAmount();
            response.totalPayAmount = itemOrder.getTotalPayAmount();
            response.totalDiscountAmount = itemOrder.getTotalDiscountAmount();
            response.orderDetails = orderDetails.stream().map(ItemOrderDetailDTO.ResponseFromOrder::of).collect(Collectors.toList());

            return response;
        }
    }
}
