package com.etask.saga.payments.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.cloud.stream.bindings")
@Data
public class StreamBindingsProperties {

    private OutputBinding paymentOrdersOut0 = new OutputBinding();
    private OutputBinding paymentOrdersOut1 = new OutputBinding();
    private OutputBinding paymentOrdersOut2 = new OutputBinding();

    @Data
    public static class OutputBinding {
        private String destination;
    }
}
