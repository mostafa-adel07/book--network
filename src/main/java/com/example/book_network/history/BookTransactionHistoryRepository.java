package com.example.book_network.history;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BookTransactionHistoryRepository extends JpaRepository<BookTransactionHistory, Integer> {
    @Query("""
        Select history
        from BookTransactionHistory history
        where history.user.id = :userId

""")
    Page<BookTransactionHistory> findAllBorrowedBooks(Pageable pageable, Integer userId);
    @Query("""
        Select history
        from BookTransactionHistory history
        where history.book.owner.id = :userId

""")
    Page<BookTransactionHistory> findAllReturnedBooks(Pageable pageable, Integer userId);



    @Query("""
        Select transaction
        from BookTransactionHistory transaction
        where transaction.user.id = :userId
        and transaction.book.id = :bookId
        and transaction.returned = false
        and transaction.returnApproved = false

""")
    Optional<BookTransactionHistory> findBookIdAndUserId(Integer bookId, Integer userId);

    @Query("""
        Select transaction
        from BookTransactionHistory transaction
        where transaction.book.owner.id = :ownerId
        and transaction.book.id = :bookId
        and transaction.returned = true
        and transaction.returnApproved = false

""")
    Optional<BookTransactionHistory> findBookIdAndOwnerId(Integer bookId, Integer ownerId);
}
