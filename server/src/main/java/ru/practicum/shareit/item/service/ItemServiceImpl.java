package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.ServiceUtil;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    @Transactional
    public ItemDto create(ItemDto itemDto, int userId) {
        User user = ServiceUtil.getUserOrThrowNotFound(userId, userRepository);

        Item item = ItemMapper.fromDto(itemDto);
        item.setOwner(user);

        if (itemDto.getRequestId() != null) {
            ItemRequest itemRequest = ServiceUtil.getItemRequestOrThrowNotFound(itemDto.getRequestId(), itemRequestRepository);
            item.setItemRequest(itemRequest);
        }

        return ItemMapper.toDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public CommentDto createComment(CommentDto commentDto, int userId, int itemId) {
        User user = ServiceUtil.getUserOrThrowNotFound(userId, userRepository);
        Item item = ServiceUtil.getItemOrThrowNotFound(itemId, itemRepository);

        Comment comment = CommentMapper.fromDto(commentDto);

        if (bookingRepository.findAllApprovedByItemIdAndUserId(itemId, userId, LocalDateTime.now()).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Комментарии можно оставлять только к тем вещам, на которые было бронирование");
        }

        comment.setAuthor(user);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());

        return CommentMapper.toDto(commentRepository.save(comment));
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto getById(int userId, int itemId) {
        Item item = ServiceUtil.getItemOrThrowNotFound(itemId, itemRepository);

        ItemDto itemDto = ItemMapper.toDto(item);
        itemDto = (item.getOwner().getId() == userId) ? addBookingInfo(itemDto) : itemDto;
        itemDto = addComments(itemDto);
        return itemDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getAllByOwnerId(int userId, int from, int size) {
        return itemRepository.findAllByOwnerId(userId, PageRequest.of(from / size, size)).stream()
                .map(ItemMapper::toDto)
                .map(this::addBookingInfo)
                .map(this::addComments)
                .sorted(Comparator.comparing(ItemDto::getId))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getAllBySearchText(String searchText, int from, int size) {
        if (searchText.isBlank()) {
            return Collections.EMPTY_LIST;
        }

        return itemRepository.findBySearchText(searchText, PageRequest.of(from, size)).stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ItemDto update(ItemDto itemDto, int itemId, int userId) {
        Item stored = ServiceUtil.getItemOrThrowNotFound(itemId, itemRepository);

        if (!stored.getOwner().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Редактирование вещи доступно только владельцу");
        }

        Optional.ofNullable(itemDto.getName()).ifPresent(stored::setName);
        Optional.ofNullable(itemDto.getDescription()).ifPresent(stored::setDescription);
        Optional.ofNullable(itemDto.getAvailable()).ifPresent(stored::setAvailable);

        try {
            return ItemMapper.toDto(itemRepository.save(stored));
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @Override
    @Transactional
    public void delete(int itemId) {
        itemRepository.deleteById(itemId);
    }

    private ItemDto addBookingInfo(ItemDto itemDto) {
        List<Booking> bookings = bookingRepository.findAllByItemId(itemDto.getId());

        Booking nextBooking = bookings.stream()
                .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                .filter(booking -> booking.getStatus().equals(BookingStatus.APPROVED))
                .min(Comparator.comparing(Booking::getStart))
                .orElse(null);
        Booking lastBooking = bookings.stream()
                .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()))
                .max(Comparator.comparing(Booking::getEnd))
                .orElse(null);

        itemDto.setNextBooking(nextBooking != null ? ItemDto.ItemBooking.builder()
                .id(nextBooking.getId())
                .bookerId(nextBooking.getUser().getId())
                .build() : null);
        itemDto.setLastBooking(lastBooking != null ? ItemDto.ItemBooking.builder()
                .id(lastBooking.getId())
                .bookerId(lastBooking.getUser().getId())
                .build() : null);

        return itemDto;
    }

    private ItemDto addComments(ItemDto itemDto) {
        itemDto.setComments(commentRepository.findAllByItemId(itemDto.getId()).stream()
                .map(CommentMapper::toDto)
                .collect(Collectors.toList()));

        return itemDto;
    }
}
