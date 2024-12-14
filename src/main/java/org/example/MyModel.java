package org.example;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

public class MyModel {
    @JsonDeserialize(using = TimeStampLongFromStringOrLongDeserializer.class)
    private Long timestamp;

    // Getters and setters
    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
