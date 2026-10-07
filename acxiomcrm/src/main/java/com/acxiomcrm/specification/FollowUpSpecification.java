package com.acxiomcrm.specification;

import com.acxiomcrm.entity.FollowUp;
import com.acxiomcrm.enums.FollowUpStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public final class FollowUpSpecification {

    private FollowUpSpecification() {
    }

    public static Specification<FollowUp> search(

            LocalDate date,

            FollowUpStatus status,

            Long assignedToId,

            Long customerId,

            Long leadId
    ) {

        return (root, query, cb) -> {

            var predicates =
                    cb.conjunction();

            if (date != null) {

                predicates =
                        cb.and(
                                predicates,
                                cb.equal(
                                        root.get(
                                                "followUpDate"
                                        ),
                                        date
                                )
                        );
            }

            if (status != null) {

                predicates =
                        cb.and(
                                predicates,
                                cb.equal(
                                        root.get("status"),
                                        status
                                )
                        );
            }

            if (assignedToId != null) {

                predicates =
                        cb.and(
                                predicates,
                                cb.equal(
                                        root.get("assignedTo")
                                                .get("id"),
                                        assignedToId
                                )
                        );
            }

            if (customerId != null) {

                predicates =
                        cb.and(
                                predicates,
                                cb.equal(
                                        root.get("customer")
                                                .get("id"),
                                        customerId
                                )
                        );
            }

            if (leadId != null) {

                predicates =
                        cb.and(
                                predicates,
                                cb.equal(
                                        root.get("lead")
                                                .get("id"),
                                        leadId
                                )
                        );
            }

            return predicates;
        };
    }
}