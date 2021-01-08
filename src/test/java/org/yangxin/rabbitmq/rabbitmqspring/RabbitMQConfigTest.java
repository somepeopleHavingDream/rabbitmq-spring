package org.yangxin.rabbitmq.rabbitmqspring;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author yangxin
 * 1/8/21 4:31 PM
 */
@SpringBootTest
class RabbitMQConfigTest {

    @Autowired
    private RabbitAdmin rabbitAdmin;

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
}