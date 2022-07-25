package com.dev.nbbang.member.global.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQProducerConfig {
    private final String MEMBER_LEAVE_EXCHANGE = "member.leave.exchange";
    private final String MEMBER_LEAVE_ROUTING_KEY = "member.leave.#";
    private final String MEMBER_LEAVE_PARTY_QUEUE = "member.leave.party.queue";
    private final String MEMBER_LEAVE_ALARM_QUEUE = "member.leave.alarm.queue";

    @Bean
    public Queue partyQueue() {
        return new Queue(MEMBER_LEAVE_PARTY_QUEUE, true);
    }

    @Bean
    public Queue alarmQueue() {
        return new Queue(MEMBER_LEAVE_ALARM_QUEUE, true);
    }
    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(MEMBER_LEAVE_EXCHANGE);
    }

    @Bean
    public Binding partyBind(Queue partyQueue, TopicExchange exchange) {
        return BindingBuilder.bind(partyQueue).to(exchange).with(MEMBER_LEAVE_ROUTING_KEY);
    }

    @Bean
    public Binding alarmBind(Queue alarmQueue, TopicExchange exchange) {
        return BindingBuilder.bind(alarmQueue).to(exchange).with(MEMBER_LEAVE_ROUTING_KEY);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());     // ObjectMapper 사용안하고 사용자 Dto 바로 Json 변환 (application/json)

        return rabbitTemplate;
    }
}
