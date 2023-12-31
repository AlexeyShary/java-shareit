package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class UserJsonTest {
    @Autowired
    private JacksonTester<UserDto> userDtoJacksonTester;

    @Test
    void testUserDto() throws Exception {
        UserDto userDto = UserDto.builder()
                .id(5)
                .name("PeterParker")
                .email("spider@man.com")
                .build();

        JsonContent<UserDto> jsonContent = userDtoJacksonTester.write(userDto);

        assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(5);
        assertThat(jsonContent).extractingJsonPathStringValue("$.name").isEqualTo("PeterParker");
        assertThat(jsonContent).extractingJsonPathStringValue("$.email").isEqualTo("spider@man.com");
    }
}
