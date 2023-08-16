package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestCreateResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestGetResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTest {
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Test
    void getAllByRequestorIdTest() {
        User owner = getUser(1);
        User requestor = getUser(2);

        ItemRequest itemRequest1 = getItemRequest(10);
        itemRequest1.setRequestor(requestor);

        ItemRequest itemRequest2 = getItemRequest(11);
        itemRequest2.setRequestor(requestor);

        Item item1 = getItem(100);
        item1.setOwner(owner);
        item1.setItemRequest(itemRequest1);

        Item item2 = getItem(101);
        item2.setOwner(owner);
        item2.setItemRequest(itemRequest2);

        List<ItemRequest> itemRequestList = Arrays.asList(
                itemRequest1,
                itemRequest2
        );

        when(userRepository.findById(requestor.getId())).thenReturn(Optional.ofNullable(requestor));
        when(itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(eq(requestor.getId()), any(Pageable.class))).thenReturn(itemRequestList);
        when(itemRepository.findAllByItemRequestId(eq(itemRequest1.getId()))).thenReturn(Arrays.asList(item1));
        when(itemRepository.findAllByItemRequestId(eq(itemRequest2.getId()))).thenReturn(Arrays.asList(item2));

        List<ItemRequestGetResponseDto> resultDtoList = itemRequestService.getAllByRequestorId(requestor.getId(), 0, 10);

        assertThat(resultDtoList.size(), equalTo(2));

        assertThat(resultDtoList.get(0).getId(), equalTo(itemRequest1.getId()));
        assertThat(resultDtoList.get(0).getDescription(), equalTo(itemRequest1.getDescription()));
        assertThat(resultDtoList.get(0).getItems().size(), equalTo(1));
        assertThat(resultDtoList.get(0).getItems().get(0).getId(), equalTo(item1.getId()));
        assertThat(resultDtoList.get(0).getItems().get(0).getName(), equalTo(item1.getName()));
        assertThat(resultDtoList.get(0).getItems().get(0).getDescription(), equalTo(item1.getDescription()));
        assertThat(resultDtoList.get(0).getItems().get(0).getAvailable(), equalTo(item1.getAvailable()));
        assertThat(resultDtoList.get(0).getItems().get(0).getRequestId(), equalTo(item1.getItemRequest().getId()));

        assertThat(resultDtoList.get(1).getId(), equalTo(itemRequest2.getId()));
        assertThat(resultDtoList.get(1).getDescription(), equalTo(itemRequest2.getDescription()));
        assertThat(resultDtoList.get(1).getItems().size(), equalTo(1));
        assertThat(resultDtoList.get(1).getItems().get(0).getId(), equalTo(item2.getId()));
        assertThat(resultDtoList.get(1).getItems().get(0).getName(), equalTo(item2.getName()));
        assertThat(resultDtoList.get(1).getItems().get(0).getDescription(), equalTo(item2.getDescription()));
        assertThat(resultDtoList.get(1).getItems().get(0).getAvailable(), equalTo(item2.getAvailable()));
        assertThat(resultDtoList.get(1).getItems().get(0).getRequestId(), equalTo(item2.getItemRequest().getId()));

        verify(userRepository, times(1)).findById(eq(requestor.getId()));
        verify(itemRequestRepository, times(1)).findAllByRequestorIdOrderByCreatedDesc(eq(requestor.getId()), any(Pageable.class));
        verify(itemRepository, times(1)).findAllByItemRequestId(itemRequest1.getId());
        verify(itemRepository, times(1)).findAllByItemRequestId(itemRequest2.getId());
        verifyNoMoreInteractions(itemRequestRepository, userRepository, itemRepository);
    }

    @Test
    void getAllTest() {
        User owner = getUser(1);
        User requestor = getUser(2);

        ItemRequest itemRequest1 = getItemRequest(10);
        itemRequest1.setRequestor(requestor);

        ItemRequest itemRequest2 = getItemRequest(11);
        itemRequest2.setRequestor(requestor);

        Item item1 = getItem(100);
        item1.setOwner(owner);
        item1.setItemRequest(itemRequest1);

        Item item2 = getItem(101);
        item2.setOwner(owner);
        item2.setItemRequest(itemRequest2);

        List<ItemRequest> itemRequestList = Arrays.asList(
                itemRequest1,
                itemRequest2
        );

        when(itemRequestRepository.findAllByRequestorIdNotOrderByCreatedDesc(eq(owner.getId()), any(Pageable.class))).thenReturn(itemRequestList);
        when(itemRepository.findAllByItemRequestId(eq(itemRequest1.getId()))).thenReturn(Arrays.asList(item1));
        when(itemRepository.findAllByItemRequestId(eq(itemRequest2.getId()))).thenReturn(Arrays.asList(item2));

        List<ItemRequestGetResponseDto> resultDtoList = itemRequestService.getAll(owner.getId(), 0, 10);

        assertThat(resultDtoList.size(), equalTo(2));

        assertThat(resultDtoList.get(0).getId(), equalTo(itemRequest1.getId()));
        assertThat(resultDtoList.get(0).getDescription(), equalTo(itemRequest1.getDescription()));
        assertThat(resultDtoList.get(0).getItems().size(), equalTo(1));
        assertThat(resultDtoList.get(0).getItems().get(0).getId(), equalTo(item1.getId()));
        assertThat(resultDtoList.get(0).getItems().get(0).getName(), equalTo(item1.getName()));
        assertThat(resultDtoList.get(0).getItems().get(0).getDescription(), equalTo(item1.getDescription()));
        assertThat(resultDtoList.get(0).getItems().get(0).getAvailable(), equalTo(item1.getAvailable()));
        assertThat(resultDtoList.get(0).getItems().get(0).getRequestId(), equalTo(item1.getItemRequest().getId()));

        assertThat(resultDtoList.get(1).getId(), equalTo(itemRequest2.getId()));
        assertThat(resultDtoList.get(1).getDescription(), equalTo(itemRequest2.getDescription()));
        assertThat(resultDtoList.get(1).getItems().size(), equalTo(1));
        assertThat(resultDtoList.get(1).getItems().get(0).getId(), equalTo(item2.getId()));
        assertThat(resultDtoList.get(1).getItems().get(0).getName(), equalTo(item2.getName()));
        assertThat(resultDtoList.get(1).getItems().get(0).getDescription(), equalTo(item2.getDescription()));
        assertThat(resultDtoList.get(1).getItems().get(0).getAvailable(), equalTo(item2.getAvailable()));
        assertThat(resultDtoList.get(1).getItems().get(0).getRequestId(), equalTo(item2.getItemRequest().getId()));

        verify(itemRequestRepository, times(1)).findAllByRequestorIdNotOrderByCreatedDesc(eq(owner.getId()), any(Pageable.class));
        verify(itemRepository, times(1)).findAllByItemRequestId(itemRequest1.getId());
        verify(itemRepository, times(1)).findAllByItemRequestId(itemRequest2.getId());
        verifyNoMoreInteractions(itemRequestRepository, userRepository, itemRepository);
    }

    @Test
    void getByIdTest() {
        User owner = getUser(1);
        User requestor = getUser(2);

        ItemRequest itemRequest = getItemRequest(10);
        itemRequest.setRequestor(requestor);

        Item item = getItem(100);
        item.setOwner(owner);
        item.setItemRequest(itemRequest);

        when(userRepository.findById(eq(requestor.getId()))).thenReturn(Optional.ofNullable(requestor));
        when(itemRequestRepository.findById(eq(itemRequest.getId()))).thenReturn(Optional.ofNullable(itemRequest));
        when(itemRepository.findAllByItemRequestId(eq(itemRequest.getId()))).thenReturn(Arrays.asList(item));

        ItemRequestGetResponseDto resultDto = itemRequestService.getById(requestor.getId(), itemRequest.getId());

        assertThat(resultDto.getId(), equalTo(itemRequest.getId()));
        assertThat(resultDto.getDescription(), equalTo(itemRequest.getDescription()));
        assertThat(resultDto.getItems().size(), equalTo(1));
        assertThat(resultDto.getItems().get(0).getId(), equalTo(item.getId()));
        assertThat(resultDto.getItems().get(0).getName(), equalTo(item.getName()));
        assertThat(resultDto.getItems().get(0).getDescription(), equalTo(item.getDescription()));
        assertThat(resultDto.getItems().get(0).getAvailable(), equalTo(item.getAvailable()));
        assertThat(resultDto.getItems().get(0).getRequestId(), equalTo(item.getItemRequest().getId()));

        verify(userRepository, times(1)).findById(eq(requestor.getId()));
        verify(itemRequestRepository, times(1)).findById(eq(itemRequest.getId()));
        verify(itemRepository, times(1)).findAllByItemRequestId(eq(itemRequest.getId()));
        verifyNoMoreInteractions(itemRequestRepository, userRepository, itemRepository);
    }

    @Test
    void createTest() {
        User user = getUser(1);
        ItemRequest itemRequest = getItemRequest(10);

        ItemRequestCreateDto itemRequestCreateDto = ItemRequestCreateDto.builder().build();

        when(userRepository.findById(eq(user.getId()))).thenReturn(Optional.ofNullable(user));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);

        ItemRequestCreateResponseDto resultDto = itemRequestService.create(itemRequestCreateDto, user.getId());

        assertThat(resultDto.getId(), equalTo(itemRequest.getId()));
        assertThat(resultDto.getDescription(), equalTo(itemRequest.getDescription()));

        verify(userRepository, times(1)).findById(eq(user.getId()));
        verify(itemRequestRepository, times(1)).save(any(ItemRequest.class));
        verifyNoMoreInteractions(itemRequestRepository, userRepository, itemRepository);
    }

    private ItemRequest getItemRequest(int id) {
        return ItemRequest.builder()
                .id(id)
                .description("Request " + id)
                .build();
    }

    private User getUser(int id) {
        return User.builder()
                .id(id)
                .name("User " + id)
                .email("user" + id + "@user.com")
                .build();
    }

    private Item getItem(int id) {
        return Item.builder()
                .id(id)
                .name("Item " + id)
                .description("ItemDescr " + id)
                .available(true)
                .build();
    }
}
