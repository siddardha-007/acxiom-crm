package com.acxiomcrm.specification;

import com.acxiomcrm.entity.Customer;
import org.springframework.data.jpa.domain.Specification;

public final class CustomerSpecification {

    private CustomerSpecification() {
    }

    public static Specification<Customer> search(
            String search
    ) {

        return (root, query, cb) -> {

            if (search == null ||
                    search.isBlank()) {

                return cb.conjunction();
            }

            String value =
                    "%" +
                            search.toLowerCase()
                            + "%";

            return cb.or(

                    cb.like(
                            cb.lower(
                                    root.get("customerName")
                            ),
                            value
                    ),

                    cb.like(
                            cb.lower(
                                    root.get("email")
                            ),
                            value
                    ),

                    cb.like(
                            cb.lower(
                                    root.get("phone")
                            ),
                            value
                    ),

                    cb.like(
                            cb.lower(
                                    root.get("companyName")
                            ),
                            value
                    )
            );
        };
    }
}