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
import java.util.UUID;

/**
 * @author yangxin
 * 1/8/21 9:03 PM
 */
@Slf4j
public class PDFMessageConverter implements MessageConverter {

    @Override
    public Message toMessage(Object object, MessageProperties messageProperties) throws MessageConversionException {
        throw new MessageConversionException("convert error!");
    }

    @Override
    public Object fromMessage(Message message) throws MessageConversionException {
        log.info("PDF MessageConverter.");

        byte[] body = message.getBody();
        String fileName = UUID.randomUUID().toString();
        String path = "/home/yangxin/Downloads/" + File.separator + fileName + ".pdf";
        File file = new File(path);
        try {
            Files.copy(new ByteArrayInputStream(body), file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
}
