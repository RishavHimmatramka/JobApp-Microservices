package com.companyms.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfiguration {

    public static final String REVIEW_EXCHANGE = "review.exchange";
    public static final String COMPANY_EXCHANGE = "company.exchange";

    public static final String REVIEW_CREATED_KEY = "review.created";
    public static final String REVIEW_UPDATED_KEY = "review.updated";
    public static final String REVIEW_DELETED_KEY = "review.deleted";
    public static final String COMPANY_DELETED_KEY = "company.deleted";

    public static final String REVIEW_CREATED_QUEUE = "review.created.queue.companies";
    public static final String REVIEW_UPDATED_QUEUE = "review.updated.queue.companies";
    public static final String REVIEW_DELETED_QUEUE = "review.deleted.queue.companies";

    @Bean
    DirectExchange reviewExchange() {
        return new DirectExchange(REVIEW_EXCHANGE);
    }

    @Bean
    DirectExchange companyExchange() {
        return new DirectExchange(COMPANY_EXCHANGE);
    }

    @Bean
    public Queue reviewCreatedQueue() {
        return new Queue(REVIEW_CREATED_QUEUE, true);
    }

    @Bean
    public Queue reviewUpdatedQueue() {
        return new Queue(REVIEW_UPDATED_QUEUE, true);
    }

    @Bean
    public Queue reviewDeletedQueue() {
        return new Queue(REVIEW_DELETED_QUEUE, true);
    }

    @Bean
    Binding reviewCreatedBinding(final Queue reviewCreatedQueue, final DirectExchange reviewExchange) {
        return BindingBuilder
                .bind(reviewCreatedQueue)
                .to(reviewExchange)
                .with(REVIEW_CREATED_KEY);
    }

    @Bean
    Binding reviewUpdatedBinding(final Queue reviewUpdatedQueue, final DirectExchange reviewExchange) {
        return BindingBuilder
                .bind(reviewUpdatedQueue)
                .to(reviewExchange)
                .with(REVIEW_UPDATED_KEY);
    }

    @Bean
    Binding reviewDeletedBinding(final Queue reviewDeletedQueue, final DirectExchange reviewExchange) {
        return BindingBuilder
                .bind(reviewDeletedQueue)
                .to(reviewExchange)
                .with(REVIEW_DELETED_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

}
