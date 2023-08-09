package ru.practicum.shareit.booking;

import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enam.Status;

import java.time.LocalDateTime;
import java.util.Optional;

@JsonTest
class BookingDtoJsonTest {

    @Autowired
    private JacksonTester<BookingDto> jsonBookingDto;

    @Test
    @SneakyThrows
    void bookingDtoTest() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        BookingDto bookingDto = BookingDto.builder()
                .start(now)
                .end(now.plusHours(1L))
                .itemId(1L)
                .status(Status.WAITING)
                .build();

        Optional<JsonContent<BookingDto>> result = Optional.of(jsonBookingDto.write(bookingDto));


        Assertions.assertThat(result)
                .isPresent()
                .hasValueSatisfying(i -> {
                    Assertions.assertThat(i)
                            .extractingJsonPathStringValue("$.start");
                    Assertions.assertThat(i)
                            .extractingJsonPathStringValue("$.end");
                });
    }
}