package com.acxiomcrm.mapper;

import com.acxiomcrm.dto.OpportunityResponse;
import com.acxiomcrm.entity.Opportunity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class OpportunityMapper {

    public OpportunityResponse toResponse(
            Opportunity opportunity
    ) {

        BigDecimal weighted =
                BigDecimal.ZERO;

        if (opportunity.getAmount() != null &&
                opportunity.getProbability() != null) {

            weighted =
                    opportunity.getAmount()
                            .multiply(
                                    BigDecimal.valueOf(
                                            opportunity
                                                    .getProbability()
                                    )
                            )
                            .divide(
                                    BigDecimal.valueOf(100),
                                    2,
                                    RoundingMode.HALF_UP
                            );
        }

        return new OpportunityResponse(

                opportunity.getId(),

                opportunity.getOpportunityName(),

                opportunity.getCustomer() != null
                        ? opportunity.getCustomer().getId()
                        : null,

                opportunity.getCustomer() != null
                        ? opportunity.getCustomer()
                        .getCustomerName()
                        : null,

                opportunity.getLead() != null
                        ? opportunity.getLead().getId()
                        : null,

                opportunity.getLead() != null
                        ? opportunity.getLead().getLeadName()
                        : null,

                opportunity.getAssignedTo() != null
                        ? opportunity.getAssignedTo().getId()
                        : null,

                opportunity.getAssignedTo() != null
                        ? opportunity.getAssignedTo().getName()
                        : null,

                opportunity.getAmount(),

                opportunity.getStage(),

                opportunity.getProbability(),

                weighted,

                opportunity.getExpectedCloseDate(),

                opportunity.getStatus(),

                opportunity.getNotes(),

                opportunity.getCreatedDate()
        );
    }
}