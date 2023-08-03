package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.RequestBookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
public class BookingServiceTest {
    @Autowired
    private BookingService bookingService;

    @MockBean
    private BookingRepository bookingRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ItemRepository itemRepository;

    @Test
    void getByIdTest() {
        User owner = getUser(1);
        User booker = getUser(2);

        Item item = getItem(10, owner);

        Booking booking = getBooking(100, booker, item);

        when(bookingRepository.findById(eq(booking.getId()))).thenReturn(Optional.ofNullable(booking));
        when(userRepository.findById(eq(owner.getId()))).thenReturn(Optional.ofNullable(owner));

        BookingResponseDto responseDto = bookingService.getById(booking.getId(), owner.getId());

        assertThat(responseDto.getId(), equalTo(booking.getId()));
        assertThat(responseDto.getStatus(), equalTo(booking.getStatus()));
        assertThat(responseDto.getBooker().getId(), equalTo(booker.getId()));
        assertThat(responseDto.getItem().getId(), equalTo(item.getId()));
        assertThat(responseDto.getItem().getName(), equalTo(item.getName()));

        verify(bookingRepository, times(1)).findById(eq(booking.getId()));
        verify(userRepository, times(1)).findById(eq(owner.getId()));
        verifyNoMoreInteractions(itemRepository, userRepository, bookingRepository);
    }

    @Test
    void getByIdTest_UnrelatedUser() {
        User owner = getUser(1);
        User booker = getUser(2);
        User unrelated = getUser(3);

        Item item = getItem(10, owner);

        Booking booking = getBooking(100, booker, item);

        when(bookingRepository.findById(eq(booking.getId()))).thenReturn(Optional.ofNullable(booking));
        when(userRepository.findById(eq(unrelated.getId()))).thenReturn(Optional.ofNullable(unrelated));

        ResponseStatusException e = assertThrows(ResponseStatusException.class, () -> {
            bookingService.getById(booking.getId(), unrelated.getId());
        });

        assertThat(e.getStatus(), equalTo(HttpStatus.NOT_FOUND));

        verify(bookingRepository, times(1)).findById(eq(booking.getId()));
        verify(userRepository, times(1)).findById(eq(unrelated.getId()));
        verifyNoMoreInteractions(itemRepository, userRepository, bookingRepository);
    }

    @Test
    void getAllByStateTest() {
        User owner = getUser(1);
        User booker = getUser(2);

        Item item1 = getItem(10, owner);
        Item item2 = getItem(11, owner);

        Booking booking1 = getBooking(100, booker, item1);
        booking1.setStart(LocalDateTime.now().minusDays(10));
        booking1.setEnd(LocalDateTime.now().minusDays(9));
        Booking booking2 = getBooking(101, booker, item2);
        booking2.setStart(LocalDateTime.now().minusDays(8));
        booking2.setEnd(LocalDateTime.now().minusDays(7));

        Page<Booking> bookingPage = new PageImpl<>(Arrays.asList(
                booking1,
                booking2
        ));

        when(userRepository.findById(eq(booker.getId()))).thenReturn(Optional.ofNullable(booker));
        when(bookingRepository.findAllByUserIdOrderByStartDesc(eq(booker.getId()), any(Pageable.class))).thenReturn(bookingPage);
        when(bookingRepository.findAllByUserIdAndEndBeforeOrderByStartDesc(eq(booker.getId()), any(LocalDateTime.class), any(Pageable.class))).thenReturn(bookingPage);
        when(bookingRepository.findAllByUserIdAndStartAfterOrderByStartDesc(eq(booker.getId()), any(LocalDateTime.class), any(Pageable.class))).thenReturn(bookingPage);
        when(bookingRepository.findAllByUserIdAndStartBeforeAndEndAfterOrderByStartDesc(eq(booker.getId()), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class))).thenReturn(bookingPage);
        when(bookingRepository.findAllByUserIdAndStatusOrderByStartDesc(eq(booker.getId()), eq(BookingStatus.WAITING), any(Pageable.class))).thenReturn(bookingPage);
        when(bookingRepository.findAllByUserIdAndStatusOrderByStartDesc(eq(booker.getId()), eq(BookingStatus.REJECTED), any(Pageable.class))).thenReturn(bookingPage);


        List<BookingResponseDto> responseDtoList;

        responseDtoList = bookingService.getAllByState(RequestBookingStatus.ALL, booker.getId(), 0, 10);
        bookingService.getAllByState(RequestBookingStatus.PAST, booker.getId(), 0, 10);
        bookingService.getAllByState(RequestBookingStatus.FUTURE, booker.getId(), 0, 10);
        bookingService.getAllByState(RequestBookingStatus.CURRENT, booker.getId(), 0, 10);
        bookingService.getAllByState(RequestBookingStatus.WAITING, booker.getId(), 0, 10);
        bookingService.getAllByState(RequestBookingStatus.REJECTED, booker.getId(), 0, 10);

        assertThat(responseDtoList.get(0).getId(), equalTo(booking1.getId()));
        assertThat(responseDtoList.get(1).getId(), equalTo(booking2.getId()));

        verify(userRepository, times(6)).findById(eq(booker.getId()));
        verify(bookingRepository, times(1)).findAllByUserIdOrderByStartDesc(eq(booker.getId()), any(Pageable.class));
        verify(bookingRepository, times(1)).findAllByUserIdAndEndBeforeOrderByStartDesc(eq(booker.getId()), any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, times(1)).findAllByUserIdAndStartAfterOrderByStartDesc(eq(booker.getId()), any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, times(1)).findAllByUserIdAndStartBeforeAndEndAfterOrderByStartDesc(eq(booker.getId()), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, times(1)).findAllByUserIdAndStatusOrderByStartDesc(eq(booker.getId()), eq(BookingStatus.WAITING), any(Pageable.class));
        verify(bookingRepository, times(1)).findAllByUserIdAndStatusOrderByStartDesc(eq(booker.getId()), eq(BookingStatus.REJECTED), any(Pageable.class));

        verifyNoMoreInteractions(itemRepository, userRepository, bookingRepository);
    }

    @Test
    void getAllByStateForOwnerTest() {
        User owner = getUser(1);
        User booker = getUser(2);

        Item item1 = getItem(10, owner);
        Item item2 = getItem(11, owner);

        Booking booking1 = getBooking(100, booker, item1);
        booking1.setStart(LocalDateTime.now().minusDays(10));
        booking1.setEnd(LocalDateTime.now().minusDays(9));
        Booking booking2 = getBooking(101, booker, item2);
        booking2.setStart(LocalDateTime.now().minusDays(8));
        booking2.setEnd(LocalDateTime.now().minusDays(7));

        Page<Booking> bookingPage = new PageImpl<>(Arrays.asList(
                booking1,
                booking2
        ));

        when(userRepository.findById(eq(owner.getId()))).thenReturn(Optional.ofNullable(owner));
        when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(eq(owner.getId()), any(Pageable.class))).thenReturn(bookingPage);
        when(bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(eq(owner.getId()), any(LocalDateTime.class), any(Pageable.class))).thenReturn(bookingPage);
        when(bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(eq(owner.getId()), any(LocalDateTime.class), any(Pageable.class))).thenReturn(bookingPage);
        when(bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(eq(owner.getId()), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class))).thenReturn(bookingPage);
        when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(eq(owner.getId()), eq(BookingStatus.WAITING), any(Pageable.class))).thenReturn(bookingPage);
        when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(eq(owner.getId()), eq(BookingStatus.REJECTED), any(Pageable.class))).thenReturn(bookingPage);


        List<BookingResponseDto> responseDtoList;

        responseDtoList = bookingService.getAllByStateForOwner(RequestBookingStatus.ALL, owner.getId(), 0, 10);
        bookingService.getAllByStateForOwner(RequestBookingStatus.PAST, owner.getId(), 0, 10);
        bookingService.getAllByStateForOwner(RequestBookingStatus.FUTURE, owner.getId(), 0, 10);
        bookingService.getAllByStateForOwner(RequestBookingStatus.CURRENT, owner.getId(), 0, 10);
        bookingService.getAllByStateForOwner(RequestBookingStatus.WAITING, owner.getId(), 0, 10);
        bookingService.getAllByStateForOwner(RequestBookingStatus.REJECTED, owner.getId(), 0, 10);

        assertThat(responseDtoList.get(0).getId(), equalTo(booking1.getId()));
        assertThat(responseDtoList.get(1).getId(), equalTo(booking2.getId()));

        verify(userRepository, times(6)).findById(eq(owner.getId()));
        verify(bookingRepository, times(1)).findAllByItemOwnerIdOrderByStartDesc(eq(owner.getId()), any(Pageable.class));
        verify(bookingRepository, times(1)).findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(eq(owner.getId()), any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, times(1)).findAllByItemOwnerIdAndStartAfterOrderByStartDesc(eq(owner.getId()), any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, times(1)).findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(eq(owner.getId()), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, times(1)).findAllByItemOwnerIdAndStatusOrderByStartDesc(eq(owner.getId()), eq(BookingStatus.WAITING), any(Pageable.class));
        verify(bookingRepository, times(1)).findAllByItemOwnerIdAndStatusOrderByStartDesc(eq(owner.getId()), eq(BookingStatus.REJECTED), any(Pageable.class));

        verifyNoMoreInteractions(itemRepository, userRepository, bookingRepository);
    }

    @Test
    void createTest() {
        User owner = getUser(1);
        User booker = getUser(2);

        Item item = getItem(10, owner);

        Booking booking = getBooking(100, booker, item);

        BookingRequestDto requestDto = BookingRequestDto.builder()
                .itemId(item.getId())
                .build();

        when(userRepository.findById(eq(booker.getId()))).thenReturn(Optional.ofNullable(booker));
        when(itemRepository.findById(eq(item.getId()))).thenReturn(Optional.ofNullable(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingResponseDto responseDto = bookingService.create(requestDto, booker.getId());

        assertThat(responseDto.getId(), equalTo(booking.getId()));
        assertThat(responseDto.getStatus(), equalTo(booking.getStatus()));
        assertThat(responseDto.getBooker().getId(), equalTo(booker.getId()));
        assertThat(responseDto.getItem().getId(), equalTo(item.getId()));
        assertThat(responseDto.getItem().getName(), equalTo(item.getName()));

        verify(userRepository, times(1)).findById(eq(booker.getId()));
        verify(itemRepository, times(1)).findById(eq(item.getId()));
        verify(bookingRepository, times(1)).save(any(Booking.class));
        verifyNoMoreInteractions(itemRepository, userRepository, bookingRepository);
    }

    @Test
    void createTest_NotAvailableItem() {
        User owner = getUser(1);
        User booker = getUser(2);

        Item item = getItem(10, owner);
        item.setAvailable(false);

        BookingRequestDto requestDto = BookingRequestDto.builder()
                .itemId(item.getId())
                .build();

        when(userRepository.findById(eq(booker.getId()))).thenReturn(Optional.ofNullable(booker));
        when(itemRepository.findById(eq(item.getId()))).thenReturn(Optional.ofNullable(item));

        ResponseStatusException e = assertThrows(ResponseStatusException.class, () -> {
            bookingService.create(requestDto, booker.getId());
        });

        assertThat(e.getStatus(), equalTo(HttpStatus.BAD_REQUEST));

        verify(userRepository, times(1)).findById(eq(booker.getId()));
        verify(itemRepository, times(1)).findById(eq(item.getId()));
        verifyNoMoreInteractions(itemRepository, userRepository, bookingRepository);
    }

    @Test
    void createTest_BookOwnItem() {
        User owner = getUser(1);

        Item item = getItem(10, owner);

        BookingRequestDto requestDto = BookingRequestDto.builder()
                .itemId(item.getId())
                .build();

        when(userRepository.findById(eq(owner.getId()))).thenReturn(Optional.ofNullable(owner));
        when(itemRepository.findById(eq(item.getId()))).thenReturn(Optional.ofNullable(item));

        ResponseStatusException e = assertThrows(ResponseStatusException.class, () -> {
            bookingService.create(requestDto, owner.getId());
        });

        assertThat(e.getStatus(), equalTo(HttpStatus.NOT_FOUND));

        verify(userRepository, times(1)).findById(eq(owner.getId()));
        verify(itemRepository, times(1)).findById(eq(item.getId()));
        verifyNoMoreInteractions(itemRepository, userRepository, bookingRepository);
    }

    @Test
    void approveTest() {
        User owner = getUser(1);
        User booker = getUser(2);

        Item item = getItem(10, owner);

        Booking booking = getBooking(100, booker, item);
        booking.setStatus(BookingStatus.WAITING);

        when(bookingRepository.findById(eq(booking.getId()))).thenReturn(Optional.ofNullable(booking));
        when(userRepository.findById(eq(owner.getId()))).thenReturn(Optional.ofNullable(owner));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingResponseDto responseDto = bookingService.approve(booking.getId(), true, owner.getId());

        assertThat(responseDto.getId(), equalTo(booking.getId()));
        assertThat(responseDto.getStatus(), equalTo(booking.getStatus()));
        assertThat(responseDto.getBooker().getId(), equalTo(booker.getId()));
        assertThat(responseDto.getItem().getId(), equalTo(item.getId()));
        assertThat(responseDto.getItem().getName(), equalTo(item.getName()));

        verify(bookingRepository, times(1)).findById(eq(booking.getId()));
        verify(userRepository, times(1)).findById(eq(owner.getId()));
        verify(bookingRepository, times(1)).save(any(Booking.class));
        verifyNoMoreInteractions(itemRepository, userRepository, bookingRepository);
    }

    @Test
    void approveTest_ByNotOwner() {
        User owner = getUser(1);
        User booker = getUser(2);

        Item item = getItem(10, owner);

        Booking booking = getBooking(100, booker, item);
        booking.setStatus(BookingStatus.WAITING);

        when(bookingRepository.findById(eq(booking.getId()))).thenReturn(Optional.ofNullable(booking));
        when(userRepository.findById(eq(booker.getId()))).thenReturn(Optional.ofNullable(booker));

        ResponseStatusException e = assertThrows(ResponseStatusException.class, () -> {
            bookingService.approve(booking.getId(), true, booker.getId());
        });

        assertThat(e.getStatus(), equalTo(HttpStatus.NOT_FOUND));

        verify(bookingRepository, times(1)).findById(eq(booking.getId()));
        verify(userRepository, times(1)).findById(eq(booker.getId()));
        verifyNoMoreInteractions(itemRepository, userRepository, bookingRepository);
    }

    @Test
    void approveTest_ForNotWaitingBooking() {
        User owner = getUser(1);
        User booker = getUser(2);

        Item item = getItem(10, owner);

        Booking booking = getBooking(100, booker, item);
        booking.setStatus(BookingStatus.APPROVED);

        when(bookingRepository.findById(eq(booking.getId()))).thenReturn(Optional.ofNullable(booking));
        when(userRepository.findById(eq(owner.getId()))).thenReturn(Optional.ofNullable(owner));

        ResponseStatusException e = assertThrows(ResponseStatusException.class, () -> {
            bookingService.approve(booking.getId(), true, owner.getId());
        });

        assertThat(e.getStatus(), equalTo(HttpStatus.BAD_REQUEST));

        verify(bookingRepository, times(1)).findById(eq(booking.getId()));
        verify(userRepository, times(1)).findById(eq(owner.getId()));
        verifyNoMoreInteractions(itemRepository, userRepository, bookingRepository);
    }

    private User getUser(int id) {
        return User.builder()
                .id(id)
                .name("User " + id)
                .email("user" + id + "@user.com")
                .build();
    }

    private Item getItem(int id, User owner) {
        return Item.builder()
                .id(id)
                .name("Item " + id)
                .description("ItemDescr " + id)
                .available(true)
                .owner(owner)
                .build();
    }

    private Booking getBooking(int id, User booker, Item item) {
        return Booking.builder()
                .id(id)
                .status(BookingStatus.APPROVED)
                .user(booker)
                .item(item)
                .build();
    }
}
