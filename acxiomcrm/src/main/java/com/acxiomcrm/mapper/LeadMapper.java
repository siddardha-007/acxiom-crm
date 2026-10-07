package com.acxiomcrm.mapper;

import com.acxiomcrm.dto.LeadResponse;
import com.acxiomcrm.entity.Lead;
import org.springframework.stereotype.Component;

@Component
public class LeadMapper {

    public LeadResponse toResponse(
            Lead lead
    ) {

        return new LeadResponse(

                lead.getId(),

                lead.getLeadCode(),

                lead.getLeadName(),

                lead.getEmail(),

                lead.getPhone(),

                lead.getCompanyName(),

                lead.getSource(),

                lead.getStatus(),

                lead.getPriority() != null
                        ? lead.getPriority().toString()
                        : null,

                lead.getExpectedValue(),

                lead.getAssignedTo() != null
                        ? lead.getAssignedTo().getId()
                        : null,

                lead.getAssignedTo() != null
                        ? lead.getAssignedTo().getName()
                        : null,

                lead.getCreatedDate()
        );
    }
}