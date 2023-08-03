package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @Test
    void createTest() throws Exception {
        int userId = 1;

        ItemDto requestDto = getRequestDto();
        ItemDto responseDto = getItemResponseDto(10);

        when(itemService.create(any(ItemDto.class), eq(userId))).thenReturn(responseDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDto.getId()));

        verify(itemService, times(1)).create(any(ItemDto.class), eq(userId));
        verifyNoMoreInteractions(itemService);
    }

    @Test
    void createCommentTest() throws Exception {
        int userId = 1;
        int itemId = 10;

        CommentDto requestDto = CommentDto.builder()
                .text("Коммент на щетку")
                .build();

        CommentDto responseDto = CommentDto.builder()
                .id(100)
                .build();

        when(itemService.createComment(any(CommentDto.class), eq(userId), eq(itemId))).thenReturn(responseDto);

        mockMvc.perform(post("/items/" + itemId + "/comment")
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDto.getId()));

        verify(itemService, times(1)).createComment(any(CommentDto.class), eq(userId), eq(itemId));
        verifyNoMoreInteractions(itemService);
    }

    @Test
    void getByIdTest() throws Exception {
        int userId = 1;

        ItemDto responseDto = getItemResponseDto(10);

        when(itemService.getById(eq(userId), eq(responseDto.getId()))).thenReturn(responseDto);

        mockMvc.perform(get("/items/" + responseDto.getId())
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDto.getId()));

        verify(itemService, times(1)).getById(eq(userId), eq(responseDto.getId()));
        verifyNoMoreInteractions(itemService);
    }

    @Test
    void getAllByOwnerId() throws Exception {
        int userId = 1;

        ItemDto responseDto1 = getItemResponseDto(10);
        ItemDto responseDto2 = getItemResponseDto(11);

        List<ItemDto> responseDtoList = Arrays.asList(
                responseDto1,
                responseDto2
        );

        when(itemService.getAllByOwnerId(eq(userId), anyInt(), anyInt())).thenReturn(responseDtoList);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(responseDto1.getId()))
                .andExpect(jsonPath("$[1].id").value(responseDto2.getId()));

        verify(itemService, times(1)).getAllByOwnerId(eq(userId), anyInt(), anyInt());
        verifyNoMoreInteractions(itemService);
    }

    @Test
    void getAllBySearchTextTest() throws Exception {
        ItemDto responseDto1 = getItemResponseDto(10);
        ItemDto responseDto2 = getItemResponseDto(11);

        List<ItemDto> responseDtoList = Arrays.asList(
                responseDto1,
                responseDto2
        );

        when(itemService.getAllBySearchText(anyString(), anyInt(), anyInt())).thenReturn(responseDtoList);

        mockMvc.perform(get("/items/search")
                        .param("text", "someText"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(responseDto1.getId()))
                .andExpect(jsonPath("$[1].id").value(responseDto2.getId()));

        verify(itemService, times(1)).getAllBySearchText(eq("someText"), anyInt(), anyInt());
        verifyNoMoreInteractions(itemService);
    }

    @Test
    void update() throws Exception {
        int userId = 1;
        int itemId = 10;

        ItemDto requestDto = getRequestDto();
        ItemDto responseDto = getItemResponseDto(10);

        when(itemService.update(any(ItemDto.class), eq(itemId), eq(userId))).thenReturn(responseDto);

        mockMvc.perform(patch("/items/" + itemId)
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDto.getId()));

        verify(itemService, times(1)).update(any(ItemDto.class), eq(itemId), eq(userId));
        verifyNoMoreInteractions(itemService);
    }

    @Test
    void deleteTest() throws Exception {
        int itemId = 1;

        mockMvc.perform(delete("/items/" + itemId))
                .andExpect(status().isOk());

        verify(itemService, times(1)).delete(eq(itemId));
        verifyNoMoreInteractions(itemService);
    }

    private ItemDto getRequestDto() {
        return ItemDto.builder()
                .name("Зубная щетка")
                .description("Почти новая")
                .available(true)
                .build();
    }

    private ItemDto getItemResponseDto(int id) {
        return ItemDto.builder()
                .id(id)
                .build();
    }
}
