package org.yangxin.rabbitmq.rabbitmqspring.adapter;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Map;

/**
 * @author yangxin
 * 1/8/21 7:44 PM
 */
@SuppressWarnings({"CommentedOutCode", "AlibabaRemoveCommentedCode"})
@Slf4j
public class MessageDelegate {

//    public void handleMessage(byte[] messageBody) {
//        log.info("默认方法，消息内容：[{}]", new String(messageBody));
//    }

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

    public void consumeMessage(Map<String, Object> messageBody) {
        log.info("map方法，消息内容：[{}]", messageBody);
    }

//    public void consumeMessage(Order order) {
//        log.info("order消息，消息内容，id:[{}], name: [{}], content: [{}]",
//                order.getId(), order.getName(), order.getContent());
//    }
//
//    public void consumeMessage(Packaged pack) {
//        log.info("package对象，消息内容，id: [{}], name: [{}], content: [{}]",
//                pack.getId(), pack.getName(), pack.getDescription());
//    }

    public void consumeMessage(File file) {
        log.info("文件对象，方法，消息内容：[{}]", file.getName());
    }
}