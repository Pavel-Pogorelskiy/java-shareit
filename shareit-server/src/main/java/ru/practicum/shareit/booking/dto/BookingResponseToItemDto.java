package ru.practicum.shareit.booking.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponseToItemDto {
    private Long id;
    private String end;
    private String start;
    private Long bookerId;
}
