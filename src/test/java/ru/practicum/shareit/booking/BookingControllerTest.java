package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookerDto;
import ru.practicum.shareit.booking.dto.BookingResearchDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.ItemBookingResponseDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private BookingService service;
    @Autowired
    private MockMvc mvc;

    @Test
    void saveBookingTestThrowsValidationExceptionItemIdNull() throws Exception {
        BookingResearchDto bookingResearchDto = new BookingResearchDto();
        bookingResearchDto.setStart(LocalDateTime.now().plusDays(10));
        bookingResearchDto.setEnd(LocalDateTime.now().plusDays(20));
        when(service.saveBooking(any(BookingResearchDto.class), anyLong()))
                .thenAnswer(invocationOnMock -> {
                    BookingResearchDto bookingResearch = invocationOnMock.getArgument(0, BookingResearchDto.class);
                    BookingResponseDto bookingResponseDto = new BookingResponseDto();
                    bookingResponseDto.setBooker(new BookerDto(invocationOnMock.getArgument(1, Long.class)));
                    bookingResponseDto.setId(1L);
                    bookingResponseDto.setItem(new ItemBookingResponseDto(bookingResearch.getItemId(),
                            "Предмет 1"));
                    bookingResponseDto.setStatus(Status.WAITING);
                    bookingResponseDto.setStart(bookingResearch.getStart()
                            .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    bookingResponseDto.setEnd(bookingResearch.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    return bookingResponseDto;
                });

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(bookingResearchDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void saveBookingTestThrowsValidationExceptionStartNull() throws Exception {
        BookingResearchDto bookingResearchDto = new BookingResearchDto();
        bookingResearchDto.setItemId(1L);
        bookingResearchDto.setEnd(LocalDateTime.now().plusDays(20));
        when(service.saveBooking(any(BookingResearchDto.class), anyLong()))
                .thenAnswer(invocationOnMock -> {
                    BookingResearchDto bookingResearch = invocationOnMock.getArgument(0, BookingResearchDto.class);
                    BookingResponseDto bookingResponseDto = new BookingResponseDto();
                    bookingResponseDto.setBooker(new BookerDto(invocationOnMock.getArgument(1, Long.class)));
                    bookingResponseDto.setId(1L);
                    bookingResponseDto.setItem(new ItemBookingResponseDto(bookingResearch.getItemId(),
                            "Предмет 1"));
                    bookingResponseDto.setStatus(Status.WAITING);
                    bookingResponseDto.setStart(bookingResearch.getStart()
                            .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    bookingResponseDto.setEnd(bookingResearch.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    return bookingResponseDto;
                });

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(bookingResearchDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void saveBookingTestThrowsValidationExceptionEndNull() throws Exception {
        BookingResearchDto bookingResearchDto = new BookingResearchDto();
        bookingResearchDto.setItemId(1L);
        bookingResearchDto.setStart(LocalDateTime.now().plusDays(20));
        when(service.saveBooking(any(BookingResearchDto.class), anyLong()))
                .thenAnswer(invocationOnMock -> {
                    BookingResearchDto bookingResearch = invocationOnMock.getArgument(0, BookingResearchDto.class);
                    BookingResponseDto bookingResponseDto = new BookingResponseDto();
                    bookingResponseDto.setBooker(new BookerDto(invocationOnMock.getArgument(1, Long.class)));
                    bookingResponseDto.setId(1L);
                    bookingResponseDto.setItem(new ItemBookingResponseDto(bookingResearch.getItemId(),
                            "Предмет 1"));
                    bookingResponseDto.setStatus(Status.WAITING);
                    bookingResponseDto.setStart(bookingResearch.getStart()
                            .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    bookingResponseDto.setEnd(bookingResearch.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    return bookingResponseDto;
                });
    }

        @Test
        void saveBookingTestThrowsValidationExceptionStartPast() throws Exception {
            BookingResearchDto bookingResearchDto = new BookingResearchDto();
            bookingResearchDto.setItemId(1L);
            bookingResearchDto.setStart(LocalDateTime.now().minusSeconds(1));
            bookingResearchDto.setEnd(LocalDateTime.now().plusDays(1));
            when(service.saveBooking(any(BookingResearchDto.class), anyLong()))
                    .thenAnswer(invocationOnMock -> {
                        BookingResearchDto bookingResearch = invocationOnMock.getArgument(0, BookingResearchDto.class);
                        BookingResponseDto bookingResponseDto = new BookingResponseDto();
                        bookingResponseDto.setBooker(new BookerDto(invocationOnMock.getArgument(1, Long.class)));
                        bookingResponseDto.setId(1L);
                        bookingResponseDto.setItem(new ItemBookingResponseDto(bookingResearch.getItemId(),
                                "Предмет 1"));
                        bookingResponseDto.setStatus(Status.WAITING);
                        bookingResponseDto.setStart(bookingResearch.getStart()
                                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                        bookingResponseDto.setEnd(bookingResearch.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                        return bookingResponseDto;
                    });

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(bookingResearchDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void saveBookingTest() throws Exception {
        BookingResearchDto bookingResearchDto = new BookingResearchDto();
        bookingResearchDto.setItemId(1L);
        bookingResearchDto.setStart(LocalDateTime.now().plusDays(10));
        bookingResearchDto.setEnd(LocalDateTime.now().plusDays(20));
        when(service.saveBooking(any(BookingResearchDto.class), anyLong()))
                .thenAnswer(invocationOnMock -> {
                    BookingResearchDto bookingResearch = invocationOnMock.getArgument(0, BookingResearchDto.class);
                    BookingResponseDto bookingResponseDto = new BookingResponseDto();
                    bookingResponseDto.setBooker(new BookerDto(invocationOnMock.getArgument(1, Long.class)));
                    bookingResponseDto.setId(1L);
                    bookingResponseDto.setItem(new ItemBookingResponseDto(bookingResearch.getItemId(),
                            "Предмет 1"));
                    bookingResponseDto.setStatus(Status.WAITING);
                    bookingResponseDto.setStart(bookingResearch.getStart()
                            .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    bookingResponseDto.setEnd(bookingResearch.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    return bookingResponseDto;
                });

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(bookingResearchDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.end", is(bookingResearchDto.getEnd()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.start", is(bookingResearchDto.getStart()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.item.id", is(1L), Long.class))
                .andExpect(jsonPath("$.item.name", is("Предмет 1")))
                .andExpect(jsonPath("$.booker.id", is(1L), Long.class))
                .andExpect(jsonPath("$.status", is(Status.WAITING.toString())));
    }

    @Test
    void approvedOrRejectBookingTestApproved() throws Exception {
        BookingResponseDto bookingResponseDto = new BookingResponseDto();
        bookingResponseDto.setBooker(new BookerDto(1L));
        bookingResponseDto.setId(1L);
        bookingResponseDto.setItem(new ItemBookingResponseDto(1L,
                "Предмет 1"));
        bookingResponseDto.setStart(LocalDateTime.now().plusDays(10)
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        bookingResponseDto.setEnd(LocalDateTime.now().plusDays(20).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        when(service.approvedOrRejectBooking(anyLong(), anyLong(), any(Boolean.class)))
                .thenAnswer(invocationOnMock -> {
                    if (invocationOnMock.getArgument(2, Boolean.class)) {
                        bookingResponseDto.setStatus(Status.APPROVED);
                    } else {
                        bookingResponseDto.setStatus(Status.REJECTED);
                    }
                    return bookingResponseDto;
                });

        mvc.perform(patch("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id", 2)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.end", is(bookingResponseDto.getEnd())))
                .andExpect(jsonPath("$.start", is(bookingResponseDto.getStart())))
                .andExpect(jsonPath("$.item.id", is(1L), Long.class))
                .andExpect(jsonPath("$.item.name", is("Предмет 1")))
                .andExpect(jsonPath("$.booker.id", is(1L), Long.class))
                .andExpect(jsonPath("$.status", is(Status.APPROVED.toString())));
    }

    @Test
    void approvedOrRejectBookingTestRejected() throws Exception {
        BookingResponseDto bookingResponseDto = new BookingResponseDto();
        bookingResponseDto.setBooker(new BookerDto(1L));
        bookingResponseDto.setId(1L);
        bookingResponseDto.setItem(new ItemBookingResponseDto(1L,
                "Предмет 1"));
        bookingResponseDto.setStart(LocalDateTime.now().plusDays(10)
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        bookingResponseDto.setEnd(LocalDateTime.now().plusDays(20).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        when(service.approvedOrRejectBooking(anyLong(), anyLong(), any(Boolean.class)))
                .thenAnswer(invocationOnMock -> {
                    if (invocationOnMock.getArgument(2, Boolean.class)) {
                        bookingResponseDto.setStatus(Status.APPROVED);
                    } else {
                        bookingResponseDto.setStatus(Status.REJECTED);
                    }
                    return bookingResponseDto;
                });

        mvc.perform(patch("/bookings/1?approved=false")
                        .header("X-Sharer-User-Id", 2)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.end", is(bookingResponseDto.getEnd())))
                .andExpect(jsonPath("$.start", is(bookingResponseDto.getStart())))
                .andExpect(jsonPath("$.item.id", is(1L), Long.class))
                .andExpect(jsonPath("$.item.name", is("Предмет 1")))
                .andExpect(jsonPath("$.booker.id", is(1L), Long.class))
                .andExpect(jsonPath("$.status", is(Status.REJECTED.toString())));
    }

    @Test
    void getBookingTest() throws Exception {
        BookingResponseDto bookingResponseDto = new BookingResponseDto();
        bookingResponseDto.setBooker(new BookerDto(1L));
        bookingResponseDto.setId(1L);
        bookingResponseDto.setItem(new ItemBookingResponseDto(1L,
                "Предмет 1"));
        bookingResponseDto.setStart(LocalDateTime.now().plusDays(10)
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        bookingResponseDto.setEnd(LocalDateTime.now().plusDays(20).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        bookingResponseDto.setStatus(Status.APPROVED);
        when(service.getBooking(anyLong(), anyLong()))
                .thenReturn(bookingResponseDto);

        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.end", is(bookingResponseDto.getEnd())))
                .andExpect(jsonPath("$.start", is(bookingResponseDto.getStart())))
                .andExpect(jsonPath("$.item.id", is(1L), Long.class))
                .andExpect(jsonPath("$.item.name", is("Предмет 1")))
                .andExpect(jsonPath("$.booker.id", is(1L), Long.class))
                .andExpect(jsonPath("$.status", is(Status.APPROVED.toString())));
    }

    @Test
    void getBookingsToBookerTestNullSize() throws Exception {
        BookingResponseDto bookingResponseDto1 = new BookingResponseDto();
        bookingResponseDto1.setBooker(new BookerDto(1L));
        bookingResponseDto1.setId(1L);
        bookingResponseDto1.setItem(new ItemBookingResponseDto(1L,
                "Предмет 1"));
        bookingResponseDto1.setStart(LocalDateTime.now().plusDays(10)
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        bookingResponseDto1.setEnd(LocalDateTime.now().plusDays(20).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        bookingResponseDto1.setStatus(Status.APPROVED);
        BookingResponseDto bookingResponseDto2 = new BookingResponseDto();
        bookingResponseDto2.setBooker(new BookerDto(1L));
        bookingResponseDto2.setId(2L);
        bookingResponseDto2.setItem(new ItemBookingResponseDto(2L,
                "Предмет 2"));
        bookingResponseDto2.setStart(LocalDateTime.now().plusDays(20)
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        bookingResponseDto2.setEnd(LocalDateTime.now().plusDays(30).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        bookingResponseDto2.setStatus(Status.REJECTED);
        when(service.getBookingToUser(anyLong(), anyString(), anyLong(), anyLong()))
                .thenAnswer(invocationOnMock -> {
                    List<BookingResponseDto> bookingsResponse;
                    if (invocationOnMock.getArgument(3, Long.class) == Long.MAX_VALUE) {
                        bookingsResponse = List.of(bookingResponseDto1, bookingResponseDto2);
                    } else {
                        bookingsResponse = List.of(bookingResponseDto1);
                    }
                    return bookingsResponse;
                });

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$.[0].id", is(bookingResponseDto1.getId()),
                        Long.class))
                .andExpect(jsonPath("$.[0].end", is(bookingResponseDto1.getEnd())))
                .andExpect(jsonPath("$.[0].start", is(bookingResponseDto1.getStart())))
                .andExpect(jsonPath("$.[0].item.id", is(bookingResponseDto1.getItem().getId()),
                        Long.class))
                .andExpect(jsonPath("$.[0].item.name", is(bookingResponseDto1.getItem().getName())))
                .andExpect(jsonPath("$.[0].booker.id", is(bookingResponseDto1.getBooker().getId()),
                        Long.class))
                .andExpect(jsonPath("$.[0].status", is(bookingResponseDto1.getStatus().toString())))
                .andExpect(jsonPath("$.[1].id", is(bookingResponseDto2.getId()), Long.class))
                .andExpect(jsonPath("$.[1].end", is(bookingResponseDto2.getEnd())))
                .andExpect(jsonPath("$.[1].start", is(bookingResponseDto2.getStart())))
                .andExpect(jsonPath("$.[1].item.id", is(bookingResponseDto2.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.[1].item.name", is(bookingResponseDto2.getItem().getName())))
                .andExpect(jsonPath("$.[1].booker.id", is(bookingResponseDto2.getBooker().getId()),
                        Long.class))
                .andExpect(jsonPath("$.[1].status", is(bookingResponseDto2.getStatus().toString())));
    }

    @Test
    void getBookingsToBookerTestSize1() throws Exception {
        BookingResponseDto bookingResponseDto1 = new BookingResponseDto();
        bookingResponseDto1.setBooker(new BookerDto(1L));
        bookingResponseDto1.setId(1L);
        bookingResponseDto1.setItem(new ItemBookingResponseDto(1L,
                "Предмет 1"));
        bookingResponseDto1.setStart(LocalDateTime.now().plusDays(10)
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        bookingResponseDto1.setEnd(LocalDateTime.now().plusDays(20).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        bookingResponseDto1.setStatus(Status.APPROVED);
        BookingResponseDto bookingResponseDto2 = new BookingResponseDto();
        bookingResponseDto2.setBooker(new BookerDto(1L));
        bookingResponseDto2.setId(2L);
        bookingResponseDto2.setItem(new ItemBookingResponseDto(2L,
                "Предмет 2"));
        bookingResponseDto2.setStart(LocalDateTime.now().plusDays(20)
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        bookingResponseDto2.setEnd(LocalDateTime.now().plusDays(30).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        bookingResponseDto2.setStatus(Status.REJECTED);
        when(service.getBookingToUser(anyLong(), anyString(), anyLong(), anyLong()))
                .thenAnswer(invocationOnMock -> {
                    List<BookingResponseDto> bookingsResponse;
                    if (invocationOnMock.getArgument(3, Long.class) == null) {
                        bookingsResponse = List.of(bookingResponseDto1, bookingResponseDto2);
                    } else {
                        bookingsResponse = List.of(bookingResponseDto1);
                    }
                    return bookingsResponse;
                });

        mvc.perform(get("/bookings?from=0&size=1")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(jsonPath("$.[0].id", is(bookingResponseDto1.getId()),
                        Long.class))
                .andExpect(jsonPath("$.[0].end", is(bookingResponseDto1.getEnd())))
                .andExpect(jsonPath("$.[0].start", is(bookingResponseDto1.getStart())))
                .andExpect(jsonPath("$.[0].item.id", is(bookingResponseDto1.getItem().getId()),
                        Long.class))
                .andExpect(jsonPath("$.[0].item.name", is(bookingResponseDto1.getItem().getName())))
                .andExpect(jsonPath("$.[0].booker.id", is(bookingResponseDto1.getBooker().getId()),
                        Long.class))
                .andExpect(jsonPath("$.[0].status", is(bookingResponseDto1.getStatus().toString())));
    }

    @Test
    void getBookingsToBookerTestThrowsValidationExceptionFrom() throws Exception {
        mvc.perform(get("/bookings?from=-1&size=1")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBookingsToBookerTestThrowsValidationExceptionSize() throws Exception {
        mvc.perform(get("/bookings?from=0&size=-1")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBookingsToOwnerTestNullSize() throws Exception {
        BookingResponseDto bookingResponseDto1 = new BookingResponseDto();
        bookingResponseDto1.setBooker(new BookerDto(1L));
        bookingResponseDto1.setId(1L);
        bookingResponseDto1.setItem(new ItemBookingResponseDto(1L,
                "Предмет 1"));
        bookingResponseDto1.setStart(LocalDateTime.now().plusDays(10)
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        bookingResponseDto1.setEnd(LocalDateTime.now().plusDays(20).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        bookingResponseDto1.setStatus(Status.APPROVED);
        BookingResponseDto bookingResponseDto2 = new BookingResponseDto();
        bookingResponseDto2.setBooker(new BookerDto(1L));
        bookingResponseDto2.setId(2L);
        bookingResponseDto2.setItem(new ItemBookingResponseDto(2L,
                "Предмет 2"));
        bookingResponseDto2.setStart(LocalDateTime.now().plusDays(20)
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        bookingResponseDto2.setEnd(LocalDateTime.now().plusDays(30).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        bookingResponseDto2.setStatus(Status.REJECTED);
        when(service.getBookingToOwner(anyLong(), anyString(), anyLong(), anyLong()))
                .thenAnswer(invocationOnMock -> {
                    List<BookingResponseDto> bookingsResponse;
                    if (invocationOnMock.getArgument(3, Long.class) == Long.MAX_VALUE) {
                        bookingsResponse = List.of(bookingResponseDto1, bookingResponseDto2);
                    } else {
                        bookingsResponse = List.of(bookingResponseDto1);
                    }
                    return bookingsResponse;
                });

        mvc.perform(get("/bookings/owner?state=ALL")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$.[0].id", is(bookingResponseDto1.getId()),
                        Long.class))
                .andExpect(jsonPath("$.[0].end", is(bookingResponseDto1.getEnd())))
                .andExpect(jsonPath("$.[0].start", is(bookingResponseDto1.getStart())))
                .andExpect(jsonPath("$.[0].item.id", is(bookingResponseDto1.getItem().getId()),
                        Long.class))
                .andExpect(jsonPath("$.[0].item.name", is(bookingResponseDto1.getItem().getName())))
                .andExpect(jsonPath("$.[0].booker.id", is(bookingResponseDto1.getBooker().getId()),
                        Long.class))
                .andExpect(jsonPath("$.[0].status", is(bookingResponseDto1.getStatus().toString())))
                .andExpect(jsonPath("$.[1].id", is(bookingResponseDto2.getId()), Long.class))
                .andExpect(jsonPath("$.[1].end", is(bookingResponseDto2.getEnd())))
                .andExpect(jsonPath("$.[1].start", is(bookingResponseDto2.getStart())))
                .andExpect(jsonPath("$.[1].item.id", is(bookingResponseDto2.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.[1].item.name", is(bookingResponseDto2.getItem().getName())))
                .andExpect(jsonPath("$.[1].booker.id", is(bookingResponseDto2.getBooker().getId()),
                        Long.class))
                .andExpect(jsonPath("$.[1].status", is(bookingResponseDto2.getStatus().toString())));
    }

    @Test
    void getBookingsToOwnerTestSize1() throws Exception {
        BookingResponseDto bookingResponseDto1 = new BookingResponseDto();
        bookingResponseDto1.setBooker(new BookerDto(1L));
        bookingResponseDto1.setId(1L);
        bookingResponseDto1.setItem(new ItemBookingResponseDto(1L,
                "Предмет 1"));
        bookingResponseDto1.setStart(LocalDateTime.now().plusDays(10)
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        bookingResponseDto1.setEnd(LocalDateTime.now().plusDays(20).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        bookingResponseDto1.setStatus(Status.APPROVED);
        BookingResponseDto bookingResponseDto2 = new BookingResponseDto();
        bookingResponseDto2.setBooker(new BookerDto(1L));
        bookingResponseDto2.setId(2L);
        bookingResponseDto2.setItem(new ItemBookingResponseDto(2L,
                "Предмет 2"));
        bookingResponseDto2.setStart(LocalDateTime.now().plusDays(20)
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        bookingResponseDto2.setEnd(LocalDateTime.now().plusDays(30).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        bookingResponseDto2.setStatus(Status.REJECTED);
        when(service.getBookingToOwner(anyLong(), anyString(), anyLong(), anyLong()))
                .thenAnswer(invocationOnMock -> {
                    List<BookingResponseDto> bookingsResponse;
                    if (invocationOnMock.getArgument(3, Long.class) == null) {
                        bookingsResponse = List.of(bookingResponseDto1, bookingResponseDto2);
                    } else {
                        bookingsResponse = List.of(bookingResponseDto1);
                    }
                    return bookingsResponse;
                });

        mvc.perform(get("/bookings/owner?from=0&size=1")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(jsonPath("$.[0].id", is(bookingResponseDto1.getId()),
                        Long.class))
                .andExpect(jsonPath("$.[0].end", is(bookingResponseDto1.getEnd())))
                .andExpect(jsonPath("$.[0].start", is(bookingResponseDto1.getStart())))
                .andExpect(jsonPath("$.[0].item.id", is(bookingResponseDto1.getItem().getId()),
                        Long.class))
                .andExpect(jsonPath("$.[0].item.name", is(bookingResponseDto1.getItem().getName())))
                .andExpect(jsonPath("$.[0].booker.id", is(bookingResponseDto1.getBooker().getId()),
                        Long.class))
                .andExpect(jsonPath("$.[0].status", is(bookingResponseDto1.getStatus().toString())));
    }

    @Test
    void getBookingsToOwnerTestThrowsValidationExceptionFrom() throws Exception {
        mvc.perform(get("/bookings/owner?from=-1&size=1")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBookingsToOwnerTestThrowsValidationExceptionSize() throws Exception {
        mvc.perform(get("/bookings/owner?from=0&size=-1")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}