package ar.edu.itba.paw.converters;

import ar.edu.itba.paw.enums.Genre;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class GenreAttributeConverter implements AttributeConverter<Genre, Integer> {

    @Override
    public Integer convertToDatabaseColumn(Genre genre) {
        return genre != null ? genre.getId() : null;
    }

    @Override
    public Genre convertToEntityAttribute(Integer id) {
        return id != null ? Genre.valueFrom(id) : null;
    }
}
