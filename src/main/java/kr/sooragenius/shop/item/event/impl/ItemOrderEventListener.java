package kr.sooragenius.shop.item.event.impl;

import kr.sooragenius.shop.item.dto.ItemDTO;
import kr.sooragenius.shop.item.dto.ItemOptionDTO;
import kr.sooragenius.shop.item.service.ItemOptionService;
import kr.sooragenius.shop.item.service.ItemService;
import kr.sooragenius.shop.order.dto.ItemOrderEventDTO;
import kr.sooragenius.shop.order.service.ItemOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemOrderEventListener {
    private final RedisTemplate redisTemplate;
    @EventListener
    public void itemOrderEvent(ItemOrderEventDTO.NewItemOrder newItemOrder) throws RuntimeException{

        DefaultRedisScript<String> holdScript = new DefaultRedisScript<>();
        holdScript.setLocation(new ClassPathResource("luascript/item/option/decreaseStock.lua"));
        holdScript.setResultType(String.class);


        List<String> keys = Arrays.asList(String.format("item::%d::%d::stock", newItemOrder.getItemId(), newItemOrder.getOptionId()));
        List<String> values = Arrays.asList(String.valueOf(newItemOrder.getStock()));
//
        String execute = (String) redisTemplate.execute(holdScript, keys, values.toArray());

        if(execute.equals("fail")) throw new RuntimeException("재고가 부족합니다.");
    }
//
//    @EventListener
//    public void cancelEvent(ItemOrderEventDTO.ItemCancel itemCancel) {
//        ItemOptionDTO.StockUpdate stockUpdate = new ItemOptionDTO.StockUpdate(itemCancel.getOptionId(), itemCancel.getStock());
//        itemOptionService.plusStockById(stockUpdate);
//    }
}
