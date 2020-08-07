package kr.sooragenius.shop.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class ItemOptionDTO {
    @Data
    @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Request {
        private String name;
        private Long premium;
    }
}
