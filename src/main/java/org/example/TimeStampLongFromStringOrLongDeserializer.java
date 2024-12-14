package org.example;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonTokenId;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public class TimeStampLongFromStringOrLongDeserializer extends StdScalarDeserializer<Long> {
    public TimeStampLongFromStringOrLongDeserializer() {
        super(TypeFactory.defaultInstance().constructType(Long.class));
    }

    public TimeStampLongFromStringOrLongDeserializer(Class<?> vc) {
        super(vc);
    }

    private static void advanceToToken(JsonParser jp) throws IOException {
        do {
            if (jp.currentTokenId() == JsonTokenId.ID_START_OBJECT) {
                jp.nextValue();
                break;
            }
            if (jp.currentTokenId() == JsonTokenId.ID_STRING
                    || jp.currentTokenId() == JsonTokenId.ID_NUMBER_INT) {
                break;
            }
            jp.nextValue();
        } while (jp.hasCurrentToken());
    }

    @Override
    public Long deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        advanceToToken(jp);
        if (jp.currentTokenId() == JsonTokenId.ID_NUMBER_INT) {
            return jp.getLongValue();
        } else {
            String timestampStr = jp.getValueAsString();
            try {
                if (timestampStr.length() == 13) {
                    // Handle milliseconds (e.g., 1711097130300)
                    System.out.println("Parsed as milliseconds");
                    return Long.parseLong(timestampStr);
                } else if (timestampStr.length() == 10) {
                    // Handle seconds (e.g., 1711097130)
                    System.out.println("Parsed as seconds");
                    return Long.parseLong(timestampStr) * 1000;
                } else {
                    // Handle various date formats
                    DateTimeFormatter[] formatters = new DateTimeFormatter[]{
                            DateTimeFormatter.ISO_DATE_TIME,
                            DateTimeFormatter.RFC_1123_DATE_TIME,
                            DateTimeFormatter.ISO_ZONED_DATE_TIME, //'2011-12-03T10:15:30+01:00[Europe/ Paris]'.
                            DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH), // RFC 822, 1036, 2822
                            DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH), // RFC 822, 1036, 2822 with timezone name
                            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX"), // ISO 8601 with milliseconds
                            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX"), // ISO 8601 without milliseconds
                            new DateTimeFormatterBuilder()
                                    // date/time
                                    .append(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                                    // offset (hh:mm - "+00:00" when it's zero)
                                    .optionalStart().appendOffset("+HH:MM", "+00:00").optionalEnd()
                                    // offset (hhmm - "+0000" when it's zero)
                                    .optionalStart().appendOffset("+HHMM", "+0000").optionalEnd()
                                    // offset (hh - "Z" when it's zero)
                                    .optionalStart().appendOffset("+HH", "Z").optionalEnd()
                                    // create formatter
                                    .toFormatter(),
                            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS[xxx][xx][X]"),
                    };

                    for (int i = 0, formattersLength = formatters.length; i < formattersLength; i++) {
                        DateTimeFormatter formatter = formatters[i];
                        try {
                            System.out.println("Try formatter: " + i);
                            ZonedDateTime zonedDateTime = ZonedDateTime.parse(timestampStr, formatter);
                            System.out.println("Parsed with formatter: " + i);
                            return zonedDateTime.toInstant().toEpochMilli();
                        } catch (DateTimeParseException e) {
                            // Try the next formatter
                        }
                    }

                    // If no formatter matched, throw an exception
                    throw new IOException("Invalid timestamp format: " + timestampStr);
                }
            } catch (NumberFormatException e) {
                throw new IOException("Invalid timestamp format: " + timestampStr, e);
            }
        }
    }

}
