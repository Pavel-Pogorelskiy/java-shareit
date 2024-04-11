package ru.practicum.shareit.item.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDto {
    @NotNull
    private Long id;
    @NotNull
    private String text;
    @NotNull
    private String authorName;
    private LocalDateTime created;
}
