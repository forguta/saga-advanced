package com.forguta.libs.saga.core.broker.rabbit.element;

import com.forguta.libs.saga.core.exception.rabbit.RabbitmqConfigurationException;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Map;

@Builder
@Data
@Slf4j
public class BindingElement implements RabbitElement {

    private String exchange;
    private String queue;
    private String routingKey;
    @Singular
    private Map<String, Object> arguments;

    @Override
    public boolean validate() {
        boolean valid = true;
        if (StringUtils.hasText(getExchange())) {
            log.error("Invalid Exchange : Exchange must be provided for a binding");
            valid = false;
        }
        if (StringUtils.hasText(getQueue())) {
            log.error("Invalid Queue : Queue must be provided for a binding");
            valid = false;
        }
        if (valid) {
            log.info("Binding configuration validated successfully for Binding '{}'", this);
        }
        return valid;
    }

    public Binding bind(Exchange exchange, Queue queue) {
        if (ExchangeTypeEnum.HEADERS.getValue().equals(exchange.getType()) && CollectionUtils.isEmpty(getArguments())) {
            throw new RabbitmqConfigurationException(String.format("Invalid Arguments : Arguments must be provided for a header exchange for binding {%s}", this));
        } else if (!StringUtils.hasText(getRoutingKey())) {
            throw new RabbitmqConfigurationException(String.format("Invalid RoutingKey : RoutingKey must be provided for a non header exchange for binding {%s}", this));
        }
        return BindingBuilder.bind(queue).to(exchange).with(getRoutingKey()).and(getArguments());
    }
}
