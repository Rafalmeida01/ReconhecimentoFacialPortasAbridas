package example.Classes;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import lombok.Data;

@Data
@Entity
public class Pessoa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String nome;

    private String dataDeNascimento;

    private String cursoDeInteresse;

    private int x;

    private int y;

    private int z;

    @Column(nullable = true)
    private String imagem; // Campo para o caminho da imagem
}
