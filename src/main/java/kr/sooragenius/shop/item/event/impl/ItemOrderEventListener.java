package kr.sooragenius.shop.item.event.impl;

import kr.sooragenius.shop.order.dto.ItemOrderDTO;
import kr.sooragenius.shop.order.dto.ItemOrderEventDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ItemOrderEventListener {
    private final RedisTemplate redisTemplate;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final KafkaTemplate kafkaTemplate;
    @EventListener
    public void itemOrderEvent(ItemOrderEventDTO.NewItemOrderDetail newItemOrderDetail) throws RuntimeException{

        DefaultRedisScript<String> holdScript = new DefaultRedisScript<>();
        holdScript.setLocation(new ClassPathResource("luascript/item/option/decreaseStock.lua"));
        holdScript.setResultType(String.class);


        List<String> keys = Arrays.asList(String.format("item::%d::%d::stock", newItemOrderDetail.getItemId(), newItemOrderDetail.getOptionId()));
        List<String> values = Arrays.asList(String.valueOf(newItemOrderDetail.getStock()));
//
        String execute = (String) redisTemplate.execute(holdScript, keys, values.toArray());

        applicationEventPublisher.publishEvent(ItemOrderEventDTO.NewItemOrderRollback.of(newItemOrderDetail));

        if(execute.equals("fail")) throw new RuntimeException("재고가 부족합니다.");

        kafkaTemplate.send("ItemOrderDetail", newItemOrderDetail);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void cancelEvent(ItemOrderEventDTO.NewItemOrderRollback newItemOrder) {

        DefaultRedisScript<String> holdScript = new DefaultRedisScript<>();
        holdScript.setLocation(new ClassPathResource("luascript/item/option/increaseStock.lua"));
        holdScript.setResultType(String.class);


        List<String> keys = Arrays.asList(String.format("item::%d::%d::stock", newItemOrder.getItemId(), newItemOrder.getOptionId()));
        List<String> values = Arrays.asList(String.valueOf(newItemOrder.getStock()));
//
        String execute = (String) redisTemplate.execute(holdScript, keys, values.toArray());
    }
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void commitEventOrder(ItemOrderEventDTO.NewItemOrder newItemOrder) {
        kafkaTemplate.send("ItemOrderTopic2", newItemOrder);
    }
    @EventListener
    public void cancelEvent(ItemOrderEventDTO.ItemCancel itemCancel) {
        ItemOrderEventDTO.NewItemOrderRollback build = ItemOrderEventDTO.NewItemOrderRollback.builder()
                .itemId(itemCancel.getItemId())
                .optionId(itemCancel.getOptionId())
                .stock(itemCancel.getStock())
                .build();
        cancelEvent(build);
    }
}
