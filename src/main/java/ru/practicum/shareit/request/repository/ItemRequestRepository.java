package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Integer> {
    Page<ItemRequest> findAllByRequestorIdNotOrderByCreatedDesc(int userId, Pageable pageable);

    Page<ItemRequest> findAllByRequestorIdOrderByCreatedDesc(int requestorId, Pageable pageable);
}