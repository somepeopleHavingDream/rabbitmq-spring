package org.yangxin.rabbitmq.rabbitmqspring;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.yangxin.rabbitmq.rabbitmqspring.entity.Order;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yangxin
 * 1/8/21 4:31 PM
 */
@SpringBootTest
@Slf4j
class RabbitMQConfigTest {

    @Autowired
    private RabbitAdmin rabbitAdmin;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    private final ObjectMapper MAPPER = new ObjectMapper();


    @Test
    public void testRabbitMQ() {
        rabbitAdmin.declareExchange(new DirectExchange("test.direct", false, false));
        rabbitAdmin.declareExchange(new TopicExchange("test.topic", false, false));
        rabbitAdmin.declareExchange(new FanoutExchange("test.fanout", false, false));

        rabbitAdmin.declareQueue(new Queue("test.direct.queue", false));
        rabbitAdmin.declareQueue(new Queue("test.topic.queue", false));
        rabbitAdmin.declareQueue(new Queue("test.fanout.queue", false));

        rabbitAdmin.declareBinding(new Binding("test.direct.queue",
                Binding.DestinationType.QUEUE, "test.direct",
                "direct",
                new HashMap<>()));
        rabbitAdmin.declareBinding(BindingBuilder
                // 直接创建队列
                .bind(new Queue("test.topic.queue", false))
                // 直接创建交换机，建立关联关系
                .to(new TopicExchange("test.topic", false, false))
                // 指定路由key
                .with("user.#"));
        rabbitAdmin.declareBinding(BindingBuilder
            .bind(new Queue("test.fanout.queue", false))
            .to(new FanoutExchange("test.fanout", false, false)));

        // 清空队列数据
        rabbitAdmin.purgeQueue("test.topic.queue", false);
    }

    @Test
    public void testSendMessage1() {
        // 创建消息
        MessageProperties messageProperties = new MessageProperties();
        Map<String, Object> headerMap = messageProperties.getHeaders();
        if (headerMap != null) {
            headerMap.put("desc", "信息消息");
            headerMap.put("type", "自定义消息类型");
        }

        Message message = new Message("Hello RabbitMQ!".getBytes(), messageProperties);
        rabbitTemplate.convertAndSend("topic001", "spring.amqp", message, oldMessage -> {
            log.info("添加额外的设置。");

            MessageProperties properties = oldMessage.getMessageProperties();
            if (properties != null) {
                Map<String, Object> headers = properties.getHeaders();
                if (headers != null) {
                    headers.put("desc", "额外修改的信息消息");
                    headers.put("attr", "额外新加的属性");
                }
            }

            return oldMessage;
        });
    }

    @Test
    public void testSendMessage2() {
        // 创建消息
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType("text/plain");

        Message message = new Message("MQ消息".getBytes(), messageProperties);
        rabbitTemplate.send("topic001", "spring.abc", message);
        rabbitTemplate.convertAndSend("topic001", "spring.amqp", "hello object message send.");
        rabbitTemplate.convertAndSend("topic002", "rabbit.abc", "hello object message send.");
    }

    @Test
    public void testSendMessage4Text() {
        // 创建消息
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType("text/plain");

        Message message = new Message("MQ消息".getBytes(), messageProperties);
        rabbitTemplate.send("topic001", "spring.abc", message);
        rabbitTemplate.send("topic002", "rabbit.abc", message);
    }

    @Test
    public void testSendJsonMessage() throws JsonProcessingException {
        Order order = new Order();
        order.setId("001");
        order.setName("消息订单");
        order.setContent("描述信息");

//        ObjectMapper mapper = new ObjectMapper();
        String json = MAPPER.writeValueAsString(order);
        log.info("order 4 json: [{}]", json);

        MessageProperties messageProperties = new MessageProperties();
        // 这里注意一定要修改contentType为application/json
        messageProperties.setContentType("application/json");
        Message message = new Message(json.getBytes(), messageProperties);

        rabbitTemplate.send("topic001", "spring.order", message);
    }

    @Test
    public void testSendJavaMessage() throws JsonProcessingException {
        Order order = new Order();
        order.setId("001");
        order.setName("消息订单");
        order.setContent("描述信息");

        String json = MAPPER.writeValueAsString(order);
        log.info("order 4 json: [{}]", json);

        MessageProperties messageProperties = new MessageProperties();
        // 这里注意一定要修改contentType为application/json
        messageProperties.setContentType("application/json");
        Map<String, Object> headerMap = messageProperties.getHeaders();
        if (headerMap != null) {
            headerMap.put("__TypeId__", "org.yangxin.rabbitmq.rabbitmqspring.entity.Order");

            Message message = new Message(json.getBytes(), messageProperties);
            rabbitTemplate.send("topic001", "spring.order", message);
        }
    }
}