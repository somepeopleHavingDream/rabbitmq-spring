package org.yangxin.rabbitmq.rabbitmqspring.convert;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;

/**
 * @author yangxin
 * 1/8/21 8:00 PM
 */
public class TextMessageConverter implements MessageConverter {

    @Override
    public Message toMessage(Object object, MessageProperties messageProperties) throws MessageConversionException {
        return new Message(object.toString().getBytes(), messageProperties);
    }

    @Override
    public Object fromMessage(Message message) throws MessageConversionException {
        MessageProperties messageProperties = message.getMessageProperties();
        if (messageProperties != null) {
            String contentType = messageProperties.getContentType();
            if (contentType != null && contentType.contains("text")) {
                return new String(message.getBody());
            }
        }

        return message.getBody();
    }
}
