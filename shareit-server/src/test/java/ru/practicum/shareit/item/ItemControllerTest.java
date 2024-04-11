package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentResearchDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private ItemService service;
    @Autowired
    private MockMvc mvc;

    @Test
    void saveItemTest() throws Exception {
        ItemDto itemDto = new ItemDto(null, "Предмет 1", "Описание 1", true,
                null, null, new ArrayList<>(), 3L);
        when(service.saveItem(any(ItemDto.class), anyLong()))
                .thenAnswer(invocationOnMock -> {
                    ItemDto itemResponse = invocationOnMock.getArgument(0, ItemDto.class);
                    itemResponse.setId(1L);
                    return itemResponse;
                });

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.nextBooking", is(itemDto.getNextBooking())))
                .andExpect(jsonPath("$.lastBooking", is(itemDto.getLastBooking())))
                .andExpect(jsonPath("$.comments", is(itemDto.getComments())))
                .andExpect(jsonPath("$.requestId", is(3L), Long.class));
    }

    @Test
    void saveItemTestThrowsValidationName() throws Exception {
        ItemDto itemDto = new ItemDto(null, "", "Описание 1", true,
                null, null, new ArrayList<>(), 3L);
        when(service.saveItem(any(ItemDto.class), anyLong()))
                .thenAnswer(invocationOnMock -> {
                    ItemDto itemResponse = invocationOnMock.getArgument(0, ItemDto.class);
                    itemResponse.setId(1L);
                    return itemResponse;
                });

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void saveItemTestThrowsValidationDescription() throws Exception {
        ItemDto itemDto = new ItemDto(null, "Предмет 1", "", true,
                null, null, new ArrayList<>(), 3L);
        when(service.saveItem(any(ItemDto.class), anyLong()))
                .thenAnswer(invocationOnMock -> {
                    ItemDto itemResponse = invocationOnMock.getArgument(0, ItemDto.class);
                    itemResponse.setId(1L);
                    return itemResponse;
                });

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void saveItemTestThrowsValidationAvailable() throws Exception {
        ItemDto itemDto = new ItemDto(null, "Предмет 1", "Описание 1", null,
                null, null, new ArrayList<>(), 3L);
        when(service.saveItem(any(ItemDto.class), anyLong()))
                .thenAnswer(invocationOnMock -> {
                    ItemDto itemResponse = invocationOnMock.getArgument(0, ItemDto.class);
                    itemResponse.setId(1L);
                    return itemResponse;
                });

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getItemTest() throws Exception {
        when(service.getItem(anyLong(), anyLong()))
                .thenAnswer(invocationOnMock -> {
                    ItemDto itemDto = new ItemDto(null, "Предмет 1", "Описание 1", true,
                            null, null, new ArrayList<>(), 1L);
                    itemDto.setId(invocationOnMock.getArgument(1, Long.class));
                    return itemDto;
                });
        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.name", is("Предмет 1")))
                .andExpect(jsonPath("$.description", is("Описание 1")))
                .andExpect(jsonPath("$.available", is(true)))
                .andExpect(jsonPath("$.comments", is(new ArrayList())))
                .andExpect(jsonPath("$.requestId", is(1L), Long.class));
    }

    @Test
    void getItemToUserTest() throws Exception {
        List<ItemDto> items = new ArrayList<>();
        items.add(new ItemDto(1L, "Предмет 1", "Описание 1", true,
                null, null, new ArrayList<>(), 1L));
        items.add(new ItemDto(2L, "Предмет 2", "Описание 2", true,
                null, null, new ArrayList<>(), 1L));
        items.add(new ItemDto(3L, "Предмет 3", "Описание 3", true,
                null, null, new ArrayList<>(), 1L));
        when(service.getItemToUser(anyLong()))
                .thenReturn(items);
        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(3)))
                .andExpect(jsonPath("$[0].id", is(items.get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(items.get(0).getName())))
                .andExpect(jsonPath("$[0].description", is(items.get(0).getDescription())))
                .andExpect(jsonPath("$[0].available", is(items.get(0).getAvailable())))
                .andExpect(jsonPath("$[0].nextBooking", is(items.get(0).getNextBooking())))
                .andExpect(jsonPath("$[0].lastBooking", is(items.get(0).getLastBooking())))
                .andExpect(jsonPath("$[0].comments", is(items.get(0).getComments())))
                .andExpect(jsonPath("$[0].requestId", is(items.get(0).getRequestId()), Long.class))
                .andExpect(jsonPath("$[1].id", is(items.get(1).getId()), Long.class))
                .andExpect(jsonPath("$[1].name", is(items.get(1).getName())))
                .andExpect(jsonPath("$[1].description", is(items.get(1).getDescription())))
                .andExpect(jsonPath("$[1].available", is(items.get(1).getAvailable())))
                .andExpect(jsonPath("$[1].nextBooking", is(items.get(1).getNextBooking())))
                .andExpect(jsonPath("$[1].lastBooking", is(items.get(1).getLastBooking())))
                .andExpect(jsonPath("$[1].comments", is(items.get(1).getComments())))
                .andExpect(jsonPath("$[1].requestId", is(items.get(1).getRequestId()), Long.class))
                .andExpect(jsonPath("$[2].id", is(items.get(2).getId()), Long.class))
                .andExpect(jsonPath("$[2].name", is(items.get(2).getName())))
                .andExpect(jsonPath("$[2].description", is(items.get(2).getDescription())))
                .andExpect(jsonPath("$[2].available", is(items.get(2).getAvailable())))
                .andExpect(jsonPath("$[2].nextBooking", is(items.get(2).getNextBooking())))
                .andExpect(jsonPath("$[2].lastBooking", is(items.get(2).getLastBooking())))
                .andExpect(jsonPath("$[2].comments", is(items.get(2).getComments())))
                .andExpect(jsonPath("$[2].requestId", is(items.get(2).getRequestId()), Long.class));
    }

    @Test
    void updateItemTest() throws Exception {
        ItemDto itemDto = new ItemDto(null, "Предмет обновленный 1", null,
                null, null, null, null, null);
        when(service.updateItem(any(ItemDto.class), anyLong(), anyLong()))
                .thenAnswer(invocationOnMock -> {
                    ItemDto itemResponse = invocationOnMock.getArgument(0, ItemDto.class);
                    itemResponse.setId(invocationOnMock.getArgument(2, Long.class));
                    itemResponse.setDescription("Описание 1");
                    itemResponse.setAvailable(true);
                    itemResponse.setComments(new ArrayList<>());
                    return itemResponse;
                });

        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is("Описание 1")))
                .andExpect(jsonPath("$.available", is(true)))
                .andExpect(jsonPath("$.nextBooking", is(itemDto.getNextBooking())))
                .andExpect(jsonPath("$.lastBooking", is(itemDto.getLastBooking())))
                .andExpect(jsonPath("$.comments", is(new ArrayList())))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId())));
    }

    @Test
    void getItemToSearchTest() throws Exception {
        ItemDto itemDto1 = new ItemDto(1L, "Предмет 1", "Описание 1",
                false, null, null, new ArrayList<>(), null);
        ItemDto itemDto2 = new ItemDto(2L, "Предмет 2", "Описание 2",
                true, null, null, new ArrayList<>(), null);

        when(service.searchItem(anyString(), anyLong()))
                .thenReturn(List.of(itemDto1, itemDto2));

        mvc.perform(get("/items/search?text=пре")
                        .accept(MediaType.ALL)
                        .header("X-Sharer-User-Id", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(itemDto1.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto1.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto1.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDto1.getAvailable())))
                .andExpect(jsonPath("$[0].nextBooking", is(itemDto1.getNextBooking())))
                .andExpect(jsonPath("$[0].lastBooking", is(itemDto1.getLastBooking())))
                .andExpect(jsonPath("$[0].comments", is(itemDto1.getComments())))
                .andExpect(jsonPath("$[0].requestId", is(itemDto1.getRequestId()), Long.class))
                .andExpect(jsonPath("$[1].id", is(itemDto2.getId()), Long.class))
                .andExpect(jsonPath("$[1].name", is(itemDto2.getName())))
                .andExpect(jsonPath("$[1].description", is(itemDto2.getDescription())))
                .andExpect(jsonPath("$[1].available", is(itemDto2.getAvailable())))
                .andExpect(jsonPath("$[1].nextBooking", is(itemDto2.getNextBooking())))
                .andExpect(jsonPath("$[1].lastBooking", is(itemDto2.getLastBooking())))
                .andExpect(jsonPath("$[1].comments", is(itemDto2.getComments())));
    }

    @Test
    void saveCommentTestThrowsValidation() throws Exception {
        CommentResearchDto commentResearchDto = new CommentResearchDto();
        commentResearchDto.setText("");
        when(service.saveComment(anyLong(), anyLong(), any(CommentResearchDto.class)))
                .thenAnswer(invocationOnMock -> {
                    CommentResponseDto commentResponseDto = invocationOnMock.getArgument(2,
                            CommentResponseDto.class);
                    return commentResponseDto;
                });

        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(commentResearchDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void saveCommentTest() throws Exception {
        CommentResearchDto commentResearchDto = new CommentResearchDto();
        commentResearchDto.setText("Комментарий 1");
        when(service.saveComment(anyLong(), anyLong(), any(CommentResearchDto.class)))
                .thenAnswer(invocationOnMock -> {
                    CommentResearchDto researchDto = invocationOnMock.getArgument(2,
                            CommentResearchDto.class);
                    CommentResponseDto commentResponseDto = new CommentResponseDto();
                    commentResponseDto.setId(1L);
                    commentResponseDto.setText(researchDto.getText());
                    commentResponseDto.setCreated(researchDto.getCreated());
                    commentResponseDto.setAuthorName("Яндекс");
                    return commentResponseDto;
                });

        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(commentResearchDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.text", is(commentResearchDto.getText())))
                .andExpect(jsonPath("$.created",  is(commentResearchDto.getCreated()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.authorName",  is("Яндекс")));
    }
}