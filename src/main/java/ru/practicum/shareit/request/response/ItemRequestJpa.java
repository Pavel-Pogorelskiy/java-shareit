package ru.practicum.shareit.request.response;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestJpa extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findItemRequestByOwner_IdOrderByCreatedDesc(Long ownerId);

    @Query("select it from ItemRequest as it where it.owner.id != :ownerId order by it.created desc")
    List<ItemRequest> findItemRequestNotByOwner_IdOrderByCreatedDesc(Long ownerId);
}
