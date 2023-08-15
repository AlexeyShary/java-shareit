package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.util.TestUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemJsonTest {
    @Autowired
    private JacksonTester<ItemDto> itemDtoJacksonTester;

    @Autowired
    private JacksonTester<CommentDto> commentDtoJacksonTester;

    @Test
    void itemDtoTest() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .id(5)
                .name("Картридж с паутиной")
                .description("Вставляется в руку если вы человек паук")
                .available(true)
                .nextBooking(ItemDto.ItemBooking.builder()
                        .id(10)
                        .bookerId(15)
                        .build())
                .lastBooking(null)
                .comments(new ArrayList<>())
                .requestId(20)
                .build();

        JsonContent<ItemDto> jsonContent = itemDtoJacksonTester.write(itemDto);

        assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(5);
        assertThat(jsonContent).extractingJsonPathStringValue("$.name").isEqualTo("Картридж с паутиной");
        assertThat(jsonContent).extractingJsonPathStringValue("$.description").isEqualTo("Вставляется в руку если вы человек паук");
        assertThat(jsonContent).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(jsonContent).extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo(10);
        assertThat(jsonContent).extractingJsonPathNumberValue("$.nextBooking.bookerId").isEqualTo(15);
        assertThat(jsonContent).extractingJsonPathStringValue("$.lastBooking").isEqualTo(null);
        assertThat(jsonContent).extractingJsonPathArrayValue("$.comments").isEmpty();
        assertThat(jsonContent).extractingJsonPathNumberValue("$.requestId").isEqualTo(20);
    }

    @Test
    void commentDtoTest() throws Exception {
        LocalDateTime timestamp = TestUtil.getCurrentTimeWithoutNano();

        CommentDto commentDto = CommentDto.builder()
                .id(5)
                .text("Мне не подошли")
                .authorName("Дедпул")
                .created(timestamp)
                .build();

        JsonContent<CommentDto> jsonContent = commentDtoJacksonTester.write(commentDto);

        assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(5);
        assertThat(jsonContent).extractingJsonPathStringValue("$.text").isEqualTo("Мне не подошли");
        assertThat(jsonContent).extractingJsonPathStringValue("$.authorName").isEqualTo("Дедпул");
        assertThat(jsonContent).extractingJsonPathStringValue("$.created").isEqualTo(TestUtil.JSON_TEST_DT_FORMATTER.format(timestamp));
    }
}