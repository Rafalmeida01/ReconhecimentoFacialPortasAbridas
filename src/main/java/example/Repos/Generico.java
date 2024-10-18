package example.Repos;

import java.util.List;

public interface Generico<T> {
    T criar(T t);
    List<T> ler();
    T lerPorId(Long id);
    void atualiza(T t, Long id);
    void deleta(Long id);
}
