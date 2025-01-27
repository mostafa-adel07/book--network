package com.example.book_network.feedback;


import com.example.book_network.book.Book;
import com.example.book_network.book.BookRepository;
import com.example.book_network.book.PageResponse;
import com.example.book_network.book.exception.OperationNotPremittedException;
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

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FeedbackService {

public final BookRepository bookRepository;
public final FeedbackMapper feedbackMapper;
public final FeedbackRepository feedbackRepository;

    public Integer save(@Valid FeedbackRequest request, Authentication connectedUser) {

        Book book= bookRepository.findById(request.bookId())
                .orElseThrow(() -> new EntityNotFoundException("No book found with the id: " + request.bookId()));

        if(book.isArchived() || !book.isShareable())
        {
            throw new OperationNotPremittedException("You cannot give a feedback for an archived or not shareable book");

        }
        User user = (User) connectedUser.getPrincipal();
        if(Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPremittedException("You cannot give a feedback to your own book");
        }
        Feedback feedback = feedbackMapper.toFeedbackRequest(request);
        return feedbackRepository.save(feedback).getId();


    }


    public PageResponse<FeedbackResponse> findAllFeedbacksByBook(Integer bookId, int page, int size, Authentication connectedUser) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        User user = (User) connectedUser.getPrincipal();
        Page<Feedback> feedbacks = feedbackRepository.findAllByBookId(bookId, pageable);
        List<FeedbackResponse> feedbackResponses = feedbacks.stream()
                .map(f -> feedbackMapper.toFeedbackResponse(f, user.getId()))
                .toList();

        return new PageResponse<>(
                feedbackResponses,
                feedbacks.getNumber(),
                feedbacks.getSize(),
                feedbacks.getTotalElements(),
                feedbacks.getTotalPages(),
                feedbacks.isFirst(),
                feedbacks.isLast()
        );
    }
}
