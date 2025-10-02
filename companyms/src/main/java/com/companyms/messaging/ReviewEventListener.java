package com.companyms.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.companyms.eventDTO.ReviewCreatedEvent;
import com.companyms.eventDTO.ReviewDeletedEvent;
import com.companyms.eventDTO.ReviewUpdatedEvent;
import com.companyms.service.CompanyService;

@Service
public class ReviewEventListener {

    private CompanyService companyService;

    public ReviewEventListener(CompanyService companyService) {
        this.companyService = companyService;
    }

    @RabbitListener(queues = RabbitMQConfiguration.REVIEW_CREATED_QUEUE)
    public void handleReviewCreatedEvent(ReviewCreatedEvent event) {
        companyService.updateCompanyRatingOnCreate(event);
    }

    @RabbitListener(queues = RabbitMQConfiguration.REVIEW_UPDATED_QUEUE)
    public void handleReviewUpdatedEvent(ReviewUpdatedEvent event) {
        companyService.updateCompanyRatingOnUpdate(event);
    }

    @RabbitListener(queues = RabbitMQConfiguration.REVIEW_DELETED_QUEUE)
    public void handleReviewDeletedEvent(ReviewDeletedEvent event) {
        companyService.updateCompanyRatingOnDelete(event);
    }

}
