package org.yangxin.rabbitmq.rabbitmqspring.convert;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yangxin
 * 1/9/21 3:42 PM
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ConverterBody {

    private byte[] body;
}