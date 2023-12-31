package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestCreateResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestGetResponseDto;
import ru.practicum.shareit.util.TestUtil;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemRequestJsonTest {
    @Autowired
    private JacksonTester<ItemRequestCreateResponseDto> itemRequestCreateResponseDtoJacksonTester;

    @Autowired
    private JacksonTester<ItemRequestGetResponseDto> itemRequestGetResponseDtoJacksonTester;

    @Test
    void itemRequestCreateResponseDtoTest() throws Exception {
        LocalDateTime timestamp = TestUtil.getCurrentTimeWithoutNano();

        ItemRequestCreateResponseDto itemRequestCreateResponseDto = ItemRequestCreateResponseDto.builder()
                .id(5)
                .description("Ищу гараж")
                .created(timestamp)
                .build();

        JsonContent<ItemRequestCreateResponseDto> jsonContent = itemRequestCreateResponseDtoJacksonTester.write(itemRequestCreateResponseDto);

        assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(5);
        assertThat(jsonContent).extractingJsonPathStringValue("$.description").isEqualTo("Ищу гараж");
        assertThat(jsonContent).extractingJsonPathStringValue("$.created").isEqualTo(TestUtil.JSON_TEST_DT_FORMATTER.format(timestamp));
    }

    @Test
    void itemRequestGetResponseDtoTest() throws Exception {
        LocalDateTime timestamp = TestUtil.getCurrentTimeWithoutNano();

        ItemRequestGetResponseDto itemRequestGetResponseDto = ItemRequestGetResponseDto.builder()
                .id(5)
                .description("Ищу гараж")
                .created(timestamp)
                .items(List.of(
                        ItemRequestGetResponseDto.RequestedItem.builder()
                                .id(10)
                                .name("Гараж")
                                .description("Расшарю гараж")
                                .available(true)
                                .requestId(5)
                                .build()
                ))
                .build();

        JsonContent<ItemRequestGetResponseDto> jsonContent = itemRequestGetResponseDtoJacksonTester.write(itemRequestGetResponseDto);

        assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(5);
        assertThat(jsonContent).extractingJsonPathStringValue("$.description").isEqualTo("Ищу гараж");
        assertThat(jsonContent).extractingJsonPathStringValue("$.created").isEqualTo(TestUtil.JSON_TEST_DT_FORMATTER.format(timestamp));
        assertThat(jsonContent).extractingJsonPathArrayValue("$.items").hasSize(1);
        assertThat(jsonContent).extractingJsonPathNumberValue("$.items[0].id").isEqualTo(10);
        assertThat(jsonContent).extractingJsonPathStringValue("$.items[0].name").isEqualTo("Гараж");
        assertThat(jsonContent).extractingJsonPathStringValue("$.items[0].description").isEqualTo("Расшарю гараж");
        assertThat(jsonContent).extractingJsonPathBooleanValue("$.items[0].available").isEqualTo(true);
        assertThat(jsonContent).extractingJsonPathNumberValue("$.items[0].requestId").isEqualTo(5);
    }
}