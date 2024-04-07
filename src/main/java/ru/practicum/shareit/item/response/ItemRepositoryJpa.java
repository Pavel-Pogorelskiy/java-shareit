package ru.practicum.shareit.item.response;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepositoryJpa extends JpaRepository<Item, Long> {
    @Query("select it from Item as it where (upper(it.name) like CONCAT('%',UPPER(:name),'%') " +
            "or upper(it.description) like CONCAT('%',UPPER(:description),'%')) AND it.available = true")
    List<Item> findByNameAndDescription(@Param("name") String name, @Param("description") String description);

    List<Item> findByOwnerIdOrderByIdAsc(Long id);

    List<Item> findByRequest_Id(Long id);
}
