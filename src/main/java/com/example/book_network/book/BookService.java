package com.example.book_network.book;

import com.example.book_network.book.exception.OperationNotPremittedException;
import com.example.book_network.history.BookTransactionHistory;
import com.example.book_network.history.BookTransactionHistoryRepository;
import com.example.book_network.user.User;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookMapper bookMapper;
    private final BookRepository bookRepository;
    private final BookTransactionHistoryRepository bookTransactionHistoryRepository;

    @Transactional
    public BookResponse save(@Valid BookRequest request, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();

        Book book = bookMapper.toBook(request);
        book.setOwner(user);

        Book savedBook = bookRepository.save(book);

        return bookMapper.toBookResponse(savedBook);
    }

    public BookResponse findById(Integer bookId) {
        return bookRepository.findById(bookId)
                .map(bookMapper::toBookResponse)
                .orElseThrow(() -> new EntityNotFoundException("No book found with the id: " + bookId))

                ;
    }

    public PageResponse<BookResponse> findAllBooks(int page, int size, Authentication connectedUser) {

        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAllDisplayableBooks(pageable, user.getId());
        List<BookResponse> bookResponses = books.stream()
                .map(bookMapper::toBookResponse)
                .toList();
        return new PageResponse<>(
                bookResponses,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast());

    }

    public PageResponse<BookResponse> findAllBooksByOwner(int page, int size, Authentication connectedUser) {


        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAll(BookSpecification.withOwnerId(user.getId()), pageable);
        List<BookResponse> bookResponses = books.stream()
                .map(bookMapper::toBookResponse)
                .toList();
        return new PageResponse<>(
                bookResponses,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast());

    }


    public PageResponse<BorrowedBookResponse> findAllBorrowerBooks(int page, int size, Authentication connectedUser) {

        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());


        Page<BookTransactionHistory> allBorrowedBooks = bookTransactionHistoryRepository.findAllBorrowedBooks(pageable, user.getId());
        List<BorrowedBookResponse> bookResponses = allBorrowedBooks.stream()
                .map(bookMapper::toBorrowedBookResponse)
                .toList();
        return new PageResponse<>(
                bookResponses,
                allBorrowedBooks.getNumber(),
                allBorrowedBooks.getSize(),
                allBorrowedBooks.getTotalElements(),
                allBorrowedBooks.getTotalPages(),
                allBorrowedBooks.isFirst(),
                allBorrowedBooks.isLast());

    }

    public PageResponse<BorrowedBookResponse> findAllReturnedBooks(int page, int size, Authentication connectedUser) {


        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());


        Page<BookTransactionHistory> allBorrowedBooks = bookTransactionHistoryRepository.findAllReturnedBooks(pageable, user.getId());
        List<BorrowedBookResponse> bookResponses = allBorrowedBooks.stream()
                .map(bookMapper::toBorrowedBookResponse)
                .toList();
        return new PageResponse<>(
                bookResponses,
                allBorrowedBooks.getNumber(),
                allBorrowedBooks.getSize(),
                allBorrowedBooks.getTotalElements(),
                allBorrowedBooks.getTotalPages(),
                allBorrowedBooks.isFirst(),
                allBorrowedBooks.isLast());
    }

    public Integer updateShareableStatus(Integer bookId, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Book book= bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with the id: " + bookId));
        //only the owner can update his book
        if(!Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPremittedException("You cannot update books shareable status");


        }
        book.setShareable(!book.isShareable());
        bookRepository.save(book);
        return bookId;
    }

    public Integer updateArchivedStatus(Integer bookId, Authentication connectedUser) {


        User user = (User) connectedUser.getPrincipal();
        Book book= bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with the id: " + bookId));
        //only the owner can update his book
        if(!Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPremittedException("You cannot update books archived status");


        }
        book.setArchived(!book.isShareable());
        bookRepository.save(book);
        return bookId;
    }
}
