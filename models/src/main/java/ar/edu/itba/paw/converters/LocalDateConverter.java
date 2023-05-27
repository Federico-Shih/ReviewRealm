package ar.edu.itba.paw.converters;


import javax.persistence.AttributeConverter;
import java.sql.Timestamp;
import java.time.LocalDate;

public class LocalDateConverter implements AttributeConverter<LocalDate, Timestamp> {
    @Override
    public Timestamp convertToDatabaseColumn(LocalDate localDateTime) {
        return localDateTime != null ? Timestamp.valueOf(localDateTime.atStartOfDay()) : null;
    }

    @Override
    public LocalDate convertToEntityAttribute(Timestamp timestamp) {
        return timestamp != null ? timestamp.toLocalDateTime().toLocalDate() : null;
    }
}
