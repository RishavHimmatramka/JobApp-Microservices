package com.reviewms.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.reviewms.bean.Review;
import com.reviewms.eventDTO.ReviewCreatedEvent;
import com.reviewms.eventDTO.ReviewDeletedEvent;
import com.reviewms.eventDTO.ReviewUpdatedEvent;

@Service
public class ReviewEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private static final Logger logger = LoggerFactory.getLogger(ReviewEventPublisher.class);

    public ReviewEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishReviewCreatedEvent(Review review) {
        ReviewCreatedEvent event = new ReviewCreatedEvent(review.getId(), review.getRating(), review.getCompanyId());
        logger.info("Publishing review created event: {}", event);
        rabbitTemplate.convertAndSend(RabbitMQConfiguration.REVIEW_EXCHANGE, RabbitMQConfiguration.REVIEW_CREATED_KEY, event, message -> {
            message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
            return message;
        });
    }

    public void publishReviewUpdatedEvent(Review review, double oldRating) {
        ReviewUpdatedEvent event = new ReviewUpdatedEvent(review.getId(), oldRating, review.getRating(), review.getCompanyId());
        logger.info("Publishing review updated event: {}", event);
        rabbitTemplate.convertAndSend(RabbitMQConfiguration.REVIEW_EXCHANGE, RabbitMQConfiguration.REVIEW_UPDATED_KEY, event, message -> {
            message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
            return message;
        });
    }

    public void publishReviewDeletedEvent(Review review) {
        ReviewDeletedEvent event = new ReviewDeletedEvent(review.getId(), review.getRating(), review.getCompanyId());
        logger.info("Publishing review deleted event: {}", event);
        rabbitTemplate.convertAndSend(RabbitMQConfiguration.REVIEW_EXCHANGE, RabbitMQConfiguration.REVIEW_DELETED_KEY, event, message -> {
            message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
            return message;
        });
    }
}
