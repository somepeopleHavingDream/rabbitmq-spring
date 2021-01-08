package org.yangxin.rabbitmq.rabbitmqspring;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

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
}