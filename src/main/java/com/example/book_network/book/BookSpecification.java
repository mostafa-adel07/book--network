package com.example.book_network.book;

import org.springframework.data.jpa.domain.Specification;

public class BookSpecification {




    // this is same as writing a query but here we use specification
    public static Specification<Book> withOwnerId(Integer ownerId){
        return(root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("owner").get("id"), ownerId);
    }
}
