package com.companyms.messaging;

import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.companyms.eventDTO.CompanyDeletedEvent;

@Service
public class CompanyEventPublisher {

    private RabbitTemplate rabbitTemplate;

    public CompanyEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishCompanyDeletedEvent(Long companyId) {
        CompanyDeletedEvent event = new CompanyDeletedEvent(companyId);
        rabbitTemplate.convertAndSend(RabbitMQConfiguration.COMPANY_EXCHANGE,
                                      RabbitMQConfiguration.COMPANY_DELETED_KEY,
                                      event,
                                      message -> {
                                          message.getMessageProperties()
                                                  .setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                                          return message;
                                      });
    }

}
