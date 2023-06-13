package org.zendesk.client.v2.model.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
    @Override
    public LocalDateTime deserialize(JsonParser arg0, DeserializationContext arg1) throws IOException {
        List<DateTimeFormatter> knownPatterns = new ArrayList<>();
        knownPatterns.add(DateTimeFormatter.ISO_DATE_TIME);
        knownPatterns.add(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        for (DateTimeFormatter pattern : knownPatterns) {
            try {
                return LocalDateTime.parse(arg0.getText(), pattern);
            } catch (DateTimeParseException pe) {
                // Loop on
            }
            try {
                ZonedDateTime zone = ZonedDateTime.parse(arg0.getText(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
                return LocalDateTime.ofInstant(zone.toInstant(), ZoneOffset.UTC);
            } catch (DateTimeParseException pe) {
                // Loop on
            }
        }
        // parsing field from text failed, try unix epoch
        try {
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(arg0.getLongValue()), ZoneId.of("UTC"));
        } catch (IOException e) {
            // not a long value
        }
        throw new RuntimeException(arg0.getText() + " is not valid date time format");
    }
}
