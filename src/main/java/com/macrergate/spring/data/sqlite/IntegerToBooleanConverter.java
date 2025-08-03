package com.macrergate.spring.data.sqlite;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

/**
 * Конвертер для преобразования INTEGER из SQLite в boolean в Java
 */
@ReadingConverter
public class IntegerToBooleanConverter implements Converter<Integer, Boolean> {
    @Override
    public Boolean convert(Integer source) {
        return source != null && source == 1;
    }
}
