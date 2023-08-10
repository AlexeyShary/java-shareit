package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.RequestBookingStatus;
import ru.practicum.shareit.booking.service.BookingService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    @Test
    void getByIdTest() throws Exception {
        int userId = 1;

        BookingResponseDto responseDto = getBookingResponseDto(10);

        when(bookingService.getById(eq(responseDto.getId()), eq(userId))).thenReturn(responseDto);

        mockMvc.perform(get("/bookings/" + responseDto.getId())
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDto.getId()));

        verify(bookingService, times(1)).getById(eq(responseDto.getId()), eq(userId));
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    void getAllByStateTest() throws Exception {
        int userId = 1;

        BookingResponseDto responseDto1 = getBookingResponseDto(10);
        BookingResponseDto responseDto2 = getBookingResponseDto(11);

        List<BookingResponseDto> responseDtoList = Arrays.asList(
                responseDto1,
                responseDto2
        );

        when(bookingService.getAllByState(any(), eq(userId), anyInt(), anyInt())).thenReturn(responseDtoList);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(responseDto1.getId()))
                .andExpect(jsonPath("$[1].id").value(responseDto2.getId()));

        verify(bookingService, times(1)).getAllByState(eq(RequestBookingStatus.ALL), eq(userId), anyInt(), anyInt());
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    void getAllByStateForOwnerTest() throws Exception {
        int userId = 1;

        BookingResponseDto responseDto1 = getBookingResponseDto(10);
        BookingResponseDto responseDto2 = getBookingResponseDto(11);

        List<BookingResponseDto> responseDtoList = Arrays.asList(
                responseDto1,
                responseDto2
        );

        when(bookingService.getAllByStateForOwner(any(), eq(userId), anyInt(), anyInt())).thenReturn(responseDtoList);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(responseDto1.getId()))
                .andExpect(jsonPath("$[1].id").value(responseDto2.getId()));

        verify(bookingService, times(1)).getAllByStateForOwner(eq(RequestBookingStatus.ALL), eq(userId), anyInt(), anyInt());
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    void createTest() throws Exception {
        int userId = 1;

        BookingRequestDto requestDto = BookingRequestDto.builder()
                .start(LocalDateTime.now().plusDays(10))
                .end(LocalDateTime.now().plusDays(20))
                .itemId(1)
                .build();

        BookingResponseDto responseDto = getBookingResponseDto(10);

        when(bookingService.create(any(BookingRequestDto.class), eq(userId))).thenReturn(responseDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDto.getId()));

        verify(bookingService, times(1)).create(any(BookingRequestDto.class), eq(userId));
        verifyNoMoreInteractions(bookingService);
    }

    @Test
    void approveTest() throws Exception {
        int userId = 1;

        BookingResponseDto responseDto = getBookingResponseDto(10);

        when(bookingService.approve(eq(responseDto.getId()), anyBoolean(), eq(userId))).thenReturn(responseDto);

        mockMvc.perform(patch("/bookings/" + responseDto.getId())
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDto.getId()));

        verify(bookingService, times(1)).approve(eq(responseDto.getId()), eq(false), eq(userId));
        verifyNoMoreInteractions(bookingService);
    }

    private BookingResponseDto getBookingResponseDto(int id) {
        return BookingResponseDto.builder()
                .id(id)
                .build();
    }
}