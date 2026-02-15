package Interface;

import java.util.List;

public interface service <T>{
    void add(T t);
    List<T>getAll();
    void update(T t);
    void delete(T t);
}
