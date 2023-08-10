package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestCreateResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestGetResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.ServiceUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestGetResponseDto> getAllByRequestorId(int userId, int from, int size) {
        ServiceUtil.getUserOrThrowNotFound(userId, userRepository);

        return itemRequestRepository
                .findAllByRequestorIdOrderByCreatedDesc(userId, PageRequest.of(from / size, size)).stream()
                .map(ItemRequestMapper::toGetResponseDto)
                .map(this::addItemsInfo)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestGetResponseDto> getAll(int userId, int from, int size) {
        return itemRequestRepository
                .findAllByRequestorIdNotOrderByCreatedDesc(userId, PageRequest.of(from / size, size)).stream()
                .map(ItemRequestMapper::toGetResponseDto)
                .map(this::addItemsInfo)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequestGetResponseDto getById(int userId, int itemRequestId) {
        ServiceUtil.getUserOrThrowNotFound(userId, userRepository);
        ItemRequest itemRequest = ServiceUtil.getItemRequestOrThrowNotFound(itemRequestId, itemRequestRepository);
        ItemRequestGetResponseDto responseDto = ItemRequestMapper.toGetResponseDto(itemRequest);
        responseDto = addItemsInfo(responseDto);
        return responseDto;
    }

    @Override
    @Transactional
    public ItemRequestCreateResponseDto create(ItemRequestCreateDto itemRequestCreateDto, int userId) {
        User user = ServiceUtil.getUserOrThrowNotFound(userId, userRepository);

        ItemRequest itemRequest = ItemRequestMapper.fromDto(itemRequestCreateDto);
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());

        return ItemRequestMapper.toCreateResponseDto(itemRequestRepository.save(itemRequest));
    }

    private ItemRequestGetResponseDto addItemsInfo(ItemRequestGetResponseDto itemRequestGetResponseDto) {
        List<Item> items = itemRepository.findAllByItemRequestId(itemRequestGetResponseDto.getId());

        itemRequestGetResponseDto.setItems(items.isEmpty() ? new ArrayList<>() :
                items.stream()
                        .map(item -> ItemRequestGetResponseDto.RequestedItem.builder()
                                .id(item.getId())
                                .name(item.getName())
                                .description(item.getDescription())
                                .available(item.getAvailable())
                                .requestId(item.getItemRequest().getId())
                                .build()
                        )
                        .collect(Collectors.toList())
        );

        return itemRequestGetResponseDto;
    }
}