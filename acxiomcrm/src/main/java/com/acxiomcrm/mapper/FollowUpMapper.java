package com.acxiomcrm.mapper;

import com.acxiomcrm.dto.FollowUpResponse;
import com.acxiomcrm.entity.FollowUp;
import org.springframework.stereotype.Component;

@Component
public class FollowUpMapper {

    public FollowUpResponse toResponse(
            FollowUp followUp
    ) {

        return new FollowUpResponse(

                followUp.getId(),

                followUp.getCustomer() != null
                        ? followUp.getCustomer().getId()
                        : null,

                followUp.getCustomer() != null
                        ? followUp.getCustomer()
                        .getCustomerName()
                        : null,

                followUp.getLead() != null
                        ? followUp.getLead().getId()
                        : null,

                followUp.getLead() != null
                        ? followUp.getLead().getLeadName()
                        : null,

                followUp.getFollowUpDate(),

                followUp.getFollowUpType(),

                followUp.getSubject(),

                followUp.getRemarks(),

                followUp.getStatus(),

                followUp.getAssignedTo() != null
                        ? followUp.getAssignedTo().getId()
                        : null,

                followUp.getAssignedTo() != null
                        ? followUp.getAssignedTo().getName()
                        : null,

                followUp.getCreatedDate()
        );
    }
}