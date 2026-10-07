package com.acxiomcrm.specification;

import com.acxiomcrm.entity.Lead;
import com.acxiomcrm.enums.LeadStatus;
import org.springframework.data.jpa.domain.Specification;

public final class LeadSpecification {

    private LeadSpecification() {
    }

    public static Specification<Lead> search(
            String search,
            LeadStatus status,
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
                                                                "leadName"
                                                        )
                                                ),
                                                value
                                        ),

                                        cb.like(
                                                cb.lower(
                                                        root.get(
                                                                "companyName"
                                                        )
                                                ),
                                                value
                                        ),

                                        cb.like(
                                                cb.lower(
                                                        root.get(
                                                                "email"
                                                        )
                                                ),
                                                value
                                        )
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