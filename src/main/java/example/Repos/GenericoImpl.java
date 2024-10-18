package example.Repos;

import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public abstract class GenericoImpl<T> implements Generico<T> {
    protected EntityManager entityManager;

    abstract Class<T> getMyClass();

    @Override
    public T criar(T t) {
        entityManager.getTransaction().begin();
        T t1 = entityManager.merge(t);
        entityManager.getTransaction().commit();
        return t1;

    }

    @Override
    public List<T> ler() {
        return entityManager.createQuery("select t from " + getMyClass().getSimpleName() + " t").getResultList();
    }

    @Override
    public T lerPorId(Long id) {
        return entityManager.find(getMyClass(), id);
    }

    @Override
    public void atualiza(T t, Long id) {
        entityManager.getTransaction().begin();
        T t1 = entityManager.find(getMyClass(), id);
        if(t1 != null){
            entityManager.merge(t);
        }
        entityManager.getTransaction().commit();
    }

    @Override
    public void deleta(Long id) {
        entityManager.getTransaction().begin();
        T t1 = entityManager.find(getMyClass(), id);
        entityManager.remove(t1);
        entityManager.getTransaction().commit();
    }
}
