package org.yangxin.rabbitmq.rabbitmqspring;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.amqp.support.converter.ContentTypeDelegatingMessageConverter;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.yangxin.rabbitmq.rabbitmqspring.adapter.MessageDelegate;
import org.yangxin.rabbitmq.rabbitmqspring.convert.ImageMessageConverter;
import org.yangxin.rabbitmq.rabbitmqspring.convert.PDFMessageConverter;
import org.yangxin.rabbitmq.rabbitmqspring.convert.TextMessageConverter;

import java.util.UUID;

/**
 * @author yangxin
 * 1/8/21 3:56 PM
 */
@Slf4j
@Configuration
@ComponentScan({"org.yangxin.rabbitmq.rabbitmqspring"})
public class RabbitMQConfig {

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost("192.168.3.3");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");
        connectionFactory.setUsername("admin");
        connectionFactory.setPassword("123456");
        return connectionFactory;
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;
    }


    /**
     * 针对消费者配置
     * 1. 设置交换机类型
     * 2. 将队列绑定到交换机
     * FanoutExchange: 将消息分发到所有的绑定队列，无routingkey的概念
     * HeadersExchange ：通过添加属性key-value匹配
     * DirectExchange:按照routingkey分发到指定队列
     * TopicExchange:多关键字匹配
     */
    @Bean
    public TopicExchange exchange001() {
        return new TopicExchange("topic001", true, false);
    }

    @Bean
    public Queue queue001() {
        return new Queue("queue001", true); //队列持久
    }

    @Bean
    public Binding binding001() {
        return BindingBuilder.bind(queue001()).to(exchange001()).with("spring.*");
    }

    @Bean
    public TopicExchange exchange002() {
        return new TopicExchange("topic002", true, false);
    }

    @Bean
    public Queue queue002() {
        return new Queue("queue002", true); //队列持久
    }

    @Bean
    public Binding binding002() {
        return BindingBuilder.bind(queue002()).to(exchange002()).with("rabbit.*");
    }

    @Bean
    public Queue queue003() {
        return new Queue("queue003", true); //队列持久
    }

    @Bean
    public Binding binding003() {
        return BindingBuilder.bind(queue003()).to(exchange001()).with("mq.*");
    }

    @Bean
    public Queue queue_image() {
        return new Queue("image_queue", true); //队列持久
    }

    @Bean
    public Queue queue_pdf() {
        return new Queue("pdf_queue", true); //队列持久
    }


    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }

    @Bean
    public SimpleMessageListenerContainer messageContainer(ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setQueues(queue001(), queue002(), queue003(), queue_image(), queue_pdf());
        container.setConcurrentConsumers(1);
        container.setMaxConcurrentConsumers(5);
        container.setDefaultRequeueRejected(false);
        container.setAcknowledgeMode(AcknowledgeMode.AUTO);
        container.setExposeListenerChannel(true);
        container.setConsumerTagStrategy(queue -> queue + "_" + UUID.randomUUID().toString());
//        container.setMessageListener((ChannelAwareMessageListener) (message, channel) -> {
//            String msg = new String(message.getBody());
//            log.info("消费者：[{}]", msg);
//        });


//      // 适配器方式1：默认是有自己的方法名字的，handleMessage
//        MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
//        // 可以自己指定一个方法的名字：consumeMessage（注意，如果自己指定监听方法，则适配类中不能有handleMessage方法）
//        adapter.setDefaultListenerMethod("consumeMessage");
//        // 也可以添加一个转换器，从字节数组转换为String
//        adapter.setMessageConverter(new TextMessageConverter());
//        container.setMessageListener(adapter);

        // 适配器方式2：我们的队列名称和方法名称也可以进行一一的匹配
//        MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
//        adapter.setMessageConverter(new TextMessageConverter());
//        Map<String, String> queueOrTag2MethodName = new HashMap<>();
//        queueOrTag2MethodName.put("queue001", "method1");
//        queueOrTag2MethodName.put("queue002", "method2");
//        adapter.setQueueOrTagToMethodName(queueOrTag2MethodName);
//        container.setMessageListener(adapter);

        // 1.1 支持json格式的转换器
//        MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
//        adapter.setDefaultListenerMethod("consumeMessage");
//
//        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
//        adapter.setMessageConverter(jackson2JsonMessageConverter);
//        container.setMessageListener(adapter);

        // 1.2 DefaultJackson2JavaTypeMapper & Jackson2JsonMessageConverter支持Java对象转换
//        MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
//        adapter.setDefaultListenerMethod("consumeMessage");
//
//        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
//
//        DefaultJackson2JavaTypeMapper defaultJackson2JavaTypeMapper = new DefaultJackson2JavaTypeMapper();
//        // 添加全部包为受信任的包
//        defaultJackson2JavaTypeMapper.setTrustedPackages("*");
////        defaultJackson2JavaTypeMapper.addTrustedPackages("*");
//        jackson2JsonMessageConverter.setJavaTypeMapper(defaultJackson2JavaTypeMapper);
//
//        adapter.setMessageConverter(jackson2JsonMessageConverter);
//        container.setMessageListener(adapter);

        // 1.3 DefaultJackson2JavaTypeMapper & Jackson2JsonMessageConverter 支持java对象多映射转换
//        MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
//        adapter.setDefaultListenerMethod("consumeMessage");
//        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
//        DefaultJackson2JavaTypeMapper defaultJackson2JavaTypeMapper = new DefaultJackson2JavaTypeMapper();
//
//        Map<String, Class<?>> id2Class = new HashMap<>();
//        id2Class.put("order", Order.class);
//        id2Class.put("packaged", Packaged.class);
//
//        defaultJackson2JavaTypeMapper.setIdClassMapping(id2Class);
//        defaultJackson2JavaTypeMapper.setTrustedPackages("*");
//
//        jackson2JsonMessageConverter.setJavaTypeMapper(defaultJackson2JavaTypeMapper);
//        adapter.setMessageConverter(jackson2JsonMessageConverter);
//        container.setMessageListener(adapter);

        // 1.4 ext convert
        MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
        adapter.setDefaultListenerMethod("consumeMessage");

        // 全局的转换器
        ContentTypeDelegatingMessageConverter contentTypeDelegatingMessageConverter = new ContentTypeDelegatingMessageConverter();

        TextMessageConverter textMessageConverter = new TextMessageConverter();
        contentTypeDelegatingMessageConverter.addDelegate("text", textMessageConverter);
        contentTypeDelegatingMessageConverter.addDelegate("html/text", textMessageConverter);
        contentTypeDelegatingMessageConverter.addDelegate("xml/text", textMessageConverter);
        contentTypeDelegatingMessageConverter.addDelegate("text/plain", textMessageConverter);

        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
        contentTypeDelegatingMessageConverter.addDelegate("json", jackson2JsonMessageConverter);
        contentTypeDelegatingMessageConverter.addDelegate("application/json", jackson2JsonMessageConverter);

        ImageMessageConverter imageMessageConverter = new ImageMessageConverter();
        contentTypeDelegatingMessageConverter.addDelegate("image/png", imageMessageConverter);
        contentTypeDelegatingMessageConverter.addDelegate("image/jpg", imageMessageConverter);
        contentTypeDelegatingMessageConverter.addDelegate("image", imageMessageConverter);

        PDFMessageConverter pdfMessageConverter = new PDFMessageConverter();
        contentTypeDelegatingMessageConverter.addDelegate("application/pdf", pdfMessageConverter);

        adapter.setMessageConverter(contentTypeDelegatingMessageConverter);
        container.setMessageListener(adapter);

        return container;
    }
}
