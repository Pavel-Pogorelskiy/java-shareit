package ru.practicum.shareit.booking.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingResearchDto {
    private Long id;
    @NotNull
    private LocalDateTime end;
    @NotNull
    @FutureOrPresent
    private LocalDateTime start;
    @NotNull
    private Long itemId;
}
