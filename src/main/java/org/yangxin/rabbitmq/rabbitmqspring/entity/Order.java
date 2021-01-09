package org.yangxin.rabbitmq.rabbitmqspring.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author yangxin
 * 1/8/21 9:02 PM
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    private String id;

    private String name;

    private String content;
}
