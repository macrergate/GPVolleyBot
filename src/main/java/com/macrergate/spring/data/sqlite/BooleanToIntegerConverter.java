package com.macrergate.spring.data.sqlite;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

/**
 * Конвертер для преобразования boolean в Java в INTEGER в SQLite
 */
@WritingConverter
public class BooleanToIntegerConverter implements Converter<Boolean, Integer> {
    @Override
    public Integer convert(Boolean source) {
        return source ? 1 : 0;
    }
}
