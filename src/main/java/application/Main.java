package application;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import services.Frame;

public class Main {
    public static void main(String[] args) {
        // Configuração do EntityManager
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("minhaUnidadeDePersistencia");
        EntityManager entityManager = emf.createEntityManager();

        // Criar a instância do Frame
        Frame frame = new Frame(entityManager);
    }
}
