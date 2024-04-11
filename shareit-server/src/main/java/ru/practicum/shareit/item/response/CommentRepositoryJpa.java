package ru.practicum.shareit.item.response;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface CommentRepositoryJpa extends JpaRepository<Comment, Long> {
    List<Comment> findCommentByItem_Id(Long itemId);
}
