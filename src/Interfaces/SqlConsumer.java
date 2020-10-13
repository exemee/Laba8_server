package Interfaces;

import java.sql.SQLException;
import java.util.function.Consumer;

public interface SqlConsumer<T> {
    void accept(T t) throws SQLException;
}
