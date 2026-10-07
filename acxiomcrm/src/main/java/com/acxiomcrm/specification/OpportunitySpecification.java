package com.acxiomcrm.specification;

import com.acxiomcrm.entity.Opportunity;
import com.acxiomcrm.enums.OpportunityStage;
import com.acxiomcrm.enums.OpportunityStatus;
import org.springframework.data.jpa.domain.Specification;

public final class OpportunitySpecification {

    private OpportunitySpecification() {
    }

    public static Specification<Opportunity> search(

            String search,

            OpportunityStage stage,

            OpportunityStatus status,

            Long assignedToId
    ) {

        return (root, query, cb) -> {

            var predicates =
                    cb.conjunction();

            if (search != null &&
                    !search.isBlank()) {

                String value =
                        "%" +
                                search.toLowerCase()
                                + "%";

                predicates =
                        cb.and(
                                predicates,
                                cb.or(

                                        cb.like(
                                                cb.lower(
                                                        root.get(
                                                                "opportunityName"
                                                        )
                                                ),
                                                value
                                        ),

                                        cb.like(
                                                cb.lower(
                                                        root.get(
                                                                        "customer"
                                                                )
                                                                .get(
                                                                        "customerName"
                                                                )
                                                ),
                                                value
                                        )
                                )
                        );
            }

            if (stage != null) {

                predicates =
                        cb.and(
                                predicates,
                                cb.equal(
                                        root.get("stage"),
                                        stage
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

            return predicates;
        };
    }
}