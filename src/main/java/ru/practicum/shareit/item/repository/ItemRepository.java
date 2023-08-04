package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends PagingAndSortingRepository<Item, Long> {
    List<Item> findAllByOwnerIdOrderByIdAsc(Pageable pageable, Long ownerId);

    List<Item> findAllByRequestId(Long requestId);


    Boolean existsItemByOwnerId(Long ownerId);

    List<Item> findAllByNameIgnoreCaseContainingOrDescriptionIgnoreCaseContainingAndAvailableTrue(Pageable pageable,
                                                                                                  String name,
                                                                                                  String description);

    @Query("SELECT i.id FROM Item AS i " +
            "JOIN User AS u ON i.owner.id=u.id " +
            "WHERE i.owner.id = :ownerId")
    List<Long> findAllItemIdByOwnerId(@Param("ownerId") Long ownerId);
}