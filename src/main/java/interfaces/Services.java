package interfaces;

import java.util.List;

public interface Services<T> {

    void add(T t );
    List<T> getAll();
    void archiver(int id);
    void update(T t);
}
