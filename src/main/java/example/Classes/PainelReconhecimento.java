package example.Classes;

import example.Repos.PessoaRepo;
import jakarta.persistence.EntityManager;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.video.capture.VideoCapture;
import services.ImageServices;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

public class PainelReconhecimento extends JPanel {
    private JLabel label;
    private VideoCapture webCam;
    public void rodar() {
        new Thread(() -> {
            while (true) {
                try {
                    MBFImage frameImage = webCam.getNextFrame();
                    BufferedImage bufferedImage = ImageUtilities.createBufferedImageForDisplay(frameImage);
                    label.setIcon(new ImageIcon(bufferedImage));
                    Thread.sleep(33);  // Aproximadamente 30fps
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
    }

    public void init() {
        this.setSize(600, 600);
        label = new JLabel();
        this.add(label, BorderLayout.CENTER);

        // Botões para capturar o frame e reconhecer
        JButton recognizeButton = new JButton("Reconhecer");
        this.add(recognizeButton, BorderLayout.SOUTH);

        recognizeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                recognizeFrame();

            }
        });

    }

    private Pessoa pessoa;
    private EntityManager em;
    public void setEM(EntityManager em) {
        this.em=em;
    }
    public void setWebCam(VideoCapture webCam) {
        this.webCam=webCam;
    }

    private void recognizeFrame() {
        try {
            // 1. Capturar o frame da webcam
            MBFImage frameImage = webCam.getNextFrame();
            BufferedImage bufferedImage = ImageUtilities.createBufferedImageForDisplay(frameImage);

            // Redimensionar o frame para 200x200
            BufferedImage resizedFrame = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = resizedFrame.createGraphics();
            g.drawImage(bufferedImage.getScaledInstance(200, 200, Image.SCALE_SMOOTH), 0, 0, null);
            g.dispose();

            // Salvar o frame capturado temporariamente
            String tempFileName = System.getenv("HOMEPATH") + "/Desktop/temp_frame.jpg";
            File tempFile = new File(tempFileName);
            ImageIO.write(resizedFrame, "jpg", tempFile);

            // 2. Criar o grid com as imagens da pasta imgs
//            ImageServices.createGrid();

            // 3. Comparar o frame capturado com o grid
            //String gridFileName = System.getProperty("user.dir") + "/gridImage.jpg"; // Localização do grid criado
            //System.out.println(gridFileName);
            String gridFileName = System.getenv("HOMEPATH") + "/Desktop/grids/grid1.jpg";
            Posicao p = ImageServices.reconhecimento(tempFileName, gridFileName);  // metodo que compar as duas imagens
            if (p != null) {
                PessoaRepo pessoaRepo = new PessoaRepo(em);
                List<Pessoa> pessoas =  pessoaRepo.ler();
                Pessoa pessoaSelecionada =pessoas.stream().filter(pessoa1 -> pessoa1.getX() == p.getX() && pessoa1.getY() == p.getY()).findFirst().orElse(null);
                if (pessoaSelecionada != null) {
                    JOptionPane.showMessageDialog(null, "Nome:" + " " + pessoaSelecionada.getNome() + " " + "Data de nascimento:" + " " + pessoaSelecionada.getDataDeNascimento() + " " + "Curso de interesse:" + " " + pessoaSelecionada.getCursoDeInteresse());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
