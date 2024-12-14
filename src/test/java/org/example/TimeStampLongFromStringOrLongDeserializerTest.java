package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TimeStampLongFromStringOrLongDeserializerTest {

    final SimpleModule module = new SimpleModule();
    private final ObjectMapper mapper = new ObjectMapper();
    private TimeStampLongFromStringOrLongDeserializer deserializer = new TimeStampLongFromStringOrLongDeserializer(Long.class);

    {
        module.addDeserializer(Long.class, deserializer);
        mapper.registerModule(module);
    }

    @Test
    public void givenMillisecondsTimestamp_whenDeserialize_thenReturnCorrectLong() throws Exception {
        // Arrange
        paramerizedTest("{\"timestamp\": \"1711097130300\"}", 1711097130300L);

    }


    @Test
    public void givenSecondsTimestamp_whenDeserialize_thenReturnCorrectLong() throws Exception {
        paramerizedTest("{\"timestamp\": \"1711097130\"}", 1711097130000L);
    }

    private void paramerizedTest(String json, long expected) throws JsonProcessingException {

        // Act
        MyModel model = mapper.readValue(json, MyModel.class);

        // Assert
        assertEquals(expected, model.getTimestamp());
    }

    @Test
    public void givenIso8601Timestamp_whenDeserialize_thenReturnCorrectLong() throws Exception {
        // Arrange
        paramerizedTest("{\"timestamp\": \"2024-12-13T00:41:35Z\"}", 1734050495000L);

        // Arrange: ISO 8601 format with nanoseconds
        paramerizedTest("{\"timestamp\": \"2024-04-16T07:17:46.5060256Z\"}", 1713251866506L);


        // Arrange: ISO 8601 format with milliseconds and timezone offset
        paramerizedTest("{\"timestamp\": \"2022-03-17T23:00:00.000+0000\"}", 1647558000000L);

        // Arrange: ISO 8601 format with milliseconds and timezone offset
        paramerizedTest("{\"timestamp\": \"2022-03-17T23:00:00.000+00\"}", 1647558000000L);

// Arrange: ISO 8601 format with milliseconds and timezone offset
        paramerizedTest("{\"timestamp\": \"2022-03-17T23:00:00.000+00:00\"}", 1647558000000L);

// Arrange: ISO 8601 format with timezone offset
        paramerizedTest("{\"timestamp\": \"2022-03-17T23:00:00.000+00\"}", 1647558000000L);


        paramerizedTest("{\"timestamp\": \"2022-02-24T08:00:00+08:00\"}", 1645660800000L);
    }

    @Test
    public void givenRfc822Timestamp_whenDeserialize_thenReturnCorrectLong() throws Exception {
        // Arrange
        paramerizedTest("{\"timestamp\": \"Fri, 13 Dec 2024 00:41:35 +0000\"}", 1734050495000L);
    }

}
