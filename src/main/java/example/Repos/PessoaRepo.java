package example.Repos;

import example.Classes.Pessoa;
import jakarta.persistence.EntityManager;

public class PessoaRepo extends GenericoImpl<Pessoa> {

    public PessoaRepo(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    Class<Pessoa> getMyClass() {
        return Pessoa.class;
    }
}
