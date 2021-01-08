package org.yangxin.rabbitmq.rabbitmqspring.adapter;

import lombok.extern.slf4j.Slf4j;

/**
 * @author yangxin
 * 1/8/21 7:44 PM
 */
@Slf4j
public class MessageDelegate {

    public void handleMessage(byte[] messageBody) {
        log.info("默认方法，消息内容：[{}]", new String(messageBody));
    }

    public void consumeMessage(byte[] messageBody) {
        log.info("字节数组方法，消息内容：[{}]", new String(messageBody));
    }

    public void consumeMessage(String messageBody) {
        log.info("字符串方法，消息内容：[{}]", messageBody);
    }

    public void method1(String messageBody) {
        log.info("method1收到消息内容：[{}]", messageBody);
    }

    public void method2(String messageBody) {
        log.info("method2收到消息内容：[{}]", messageBody);
    }
}
