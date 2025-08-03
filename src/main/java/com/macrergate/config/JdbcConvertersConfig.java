package com.macrergate.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.core.convert.JdbcCustomConversions;

/**
 * Конфигурация для регистрации пользовательских конвертеров JDBC
 */
@Configuration
public class JdbcConvertersConfig {

    /**
     * Регистрирует пользовательские конвертеры для работы с типами данных
     * @return JdbcCustomConversions с зарегистрированными конвертерами
     */
//    @Bean
//    public JdbcCustomConversions jdbcCustomConversions() {
//        return new JdbcCustomConversions(Arrays.asList(
//            new IntegerToBooleanConverter(),
//            new BooleanToIntegerConverter()
//        ));
//    }
}
