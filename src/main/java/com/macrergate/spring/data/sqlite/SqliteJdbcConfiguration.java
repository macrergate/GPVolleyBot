package com.macrergate.spring.data.sqlite;

import java.util.ArrayList;
import java.util.List;

import org.komamitsu.spring.data.sqlite.EnableSqliteRepositories;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@Configuration
@EnableSqliteRepositories
public class SqliteJdbcConfiguration extends org.komamitsu.spring.data.sqlite.SqliteJdbcConfiguration {
    @Override
    protected List<?> userConverters() {
        ArrayList<Object> list = new ArrayList<>(super.userConverters());
        list.add(new IntegerToBooleanConverter());
        list.add(new BooleanToIntegerConverter());
        return list;
    }
}
