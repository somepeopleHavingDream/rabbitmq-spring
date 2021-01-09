package org.yangxin.rabbitmq.rabbitmqspring.convert;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.UUID;

/**
 * @author yangxin
 * 1/9/21 2:19 PM
 */
@Slf4j
public class ImageMessageConverter implements MessageConverter {

    @Override
    public Message toMessage(Object object, MessageProperties messageProperties) throws MessageConversionException {
        throw new MessageConversionException("convert error!");
    }

    @Override
    public Object fromMessage(Message message) throws MessageConversionException {
        log.info("Image MessageConverter.");

        MessageProperties messageProperties = message.getMessageProperties();
        if (messageProperties != null) {
            Map<String, Object> headerMap = messageProperties.getHeaders();
            if (headerMap != null) {
                Object originalExtName = headerMap.get("extName");
                String extName = originalExtName == null ? "png" : originalExtName.toString();

                byte[] body = message.getBody();
                String fileName = UUID.randomUUID().toString();
                String path = "/home/yangxin/Downloads/" + fileName + "." + extName;
                File file = new File(path);
                try {
                    Files.copy(new ByteArrayInputStream(body), file.toPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return file;
            }
        }

        return null;
    }
}
