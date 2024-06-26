package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentResearchDto;
import ru.practicum.shareit.item.dto.ItemDto;


@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> saveItem(ItemDto requestDto, long userId) {
        return post("", userId, requestDto);
    }

    public ResponseEntity<Object> saveComment(long itemId, long userId, CommentResearchDto requestDto) {
        return post("/" + itemId + "/comment", userId, requestDto);
    }

    public ResponseEntity<Object> getItem(long itemId, Long userId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getItemToUser(long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> searchItem(String text, long userId) {
        return get("/search?text=" + text, userId);
    }

    public ResponseEntity<Object> updateItem(ItemDto requestDto, long userId, long itemId) {
        return patch("/" + itemId, userId, requestDto);
    }
}
