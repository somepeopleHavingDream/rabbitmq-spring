package org.yangxin.rabbitmq.rabbitmqspring.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yangxin
 * 1/8/21 9:01 PM
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Packaged {

    private String id;

    private String name;

    private String description;
}
