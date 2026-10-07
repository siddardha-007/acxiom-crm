package com.acxiomcrm.mapper;

import com.acxiomcrm.dto.CustomerResponse;
import com.acxiomcrm.entity.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    public CustomerResponse toResponse(
            Customer customer
    ) {

        return new CustomerResponse(

                customer.getId(),

                customer.getCustomerCode(),

                customer.getCustomerName(),

                customer.getEmail(),

                customer.getPhone(),

                customer.getCompanyName(),

                customer.getAddress(),

                customer.getCity(),

                customer.getState(),

                customer.getStatus(),

                customer.getAssignedTo() != null
                        ? customer.getAssignedTo().getId()
                        : null,

                customer.getAssignedTo() != null
                        ? customer.getAssignedTo().getName()
                        : null,

                customer.getCreatedBy() != null
                        ? customer.getCreatedBy().getId()
                        : null,

                customer.getCreatedBy() != null
                        ? customer.getCreatedBy().getName()
                        : null,

                customer.getCreatedDate()
        );
    }
}