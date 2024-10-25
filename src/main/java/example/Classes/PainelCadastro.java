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

public class PainelCadastro extends JPanel {
    public void init() {
        this.setSize(600, 600);
        this.setLayout(null);
        nomeLabel = new JLabel("Nome");
        dataNascLabel = new JLabel("Data de nascimento");
        cursoLabel = new JLabel("Curso desejado");
        nomeText = new JTextField();
        dataNascText = new JTextField();
        cursoText = new JTextField();
        button = new JButton("Tirar foto");
        cadastra = new JButton("Cadastrar");

        nomeLabel.setBounds(50, 50, 100, 30);
        nomeText.setBounds(50, 100, 100, 30);
        dataNascLabel.setBounds(50, 150, 100, 30);
        dataNascText.setBounds(50, 200, 100, 30);
        cursoLabel.setBounds(50, 250, 100, 30);
        cursoText.setBounds(50, 300, 100, 30);
        cadastra.setBounds(50, 400, 100, 60);
        button.setBounds(50, 400, 100, 60);
        label = new JLabel();
        label.setBounds(0,0,600,600);
        this.add(label);


        nomeLabel.setVisible(true);
        dataNascLabel.setVisible(true);
        cursoLabel.setVisible(true);
        nomeText.setVisible(true);
        dataNascText.setVisible(true);
        cursoText.setVisible(true);
        label.setVisible(false);
        button.setVisible(false);



        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                captureFrame();
                PessoaRepo pessoaRepo = new PessoaRepo(em);
                pessoaRepo.criar(pessoa);
                nomeLabel.setVisible(true);
                dataNascLabel.setVisible(true);
                cursoLabel.setVisible(true);
                nomeText.setVisible(true);
                dataNascText.setVisible(true);
                cursoText.setVisible(true);
                label.setVisible(false);
                button.setVisible(false);
            }
        });


        cadastra.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pessoa = new Pessoa();
                pessoa.setNome(nomeText.getText());
                pessoa.setDataDeNascimento(dataNascText.getText());
                pessoa.setCursoDeInteresse(cursoText.getText());
                nomeLabel.setVisible(false);
                dataNascLabel.setVisible(false);
                cursoLabel.setVisible(false);
                nomeText.setVisible(false);
                dataNascText.setVisible(false);
                cursoText.setVisible(false);
                label.setVisible(true);
                button.setVisible(true);
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
        });

        this.add(nomeLabel);
        this.add(nomeText);
        this.add(dataNascLabel);
        this.add(dataNascText);
        this.add(cursoLabel);
        this.add(cursoText);
        this.add(button);
        this.add(cadastra);
        this.add(label);
    }
    private JLabel nomeLabel;
    private JTextField nomeText;
    private JLabel dataNascLabel;
    private JTextField dataNascText;
    private JLabel cursoLabel;
    private JTextField cursoText;
    private VideoCapture webCam;
    private JLabel label;
    private JButton button;
    private JButton cadastra;

    private Pessoa pessoa;
    private EntityManager em;
    public void setEM(EntityManager em) {
        this.em=em;
    }
    public void setWebCam(VideoCapture webCam) {
        this.webCam=webCam;
    }

    private void captureFrame() {
        try {
            // Capturar o próximo frame da webcam
            MBFImage frameImage = webCam.getNextFrame();
            BufferedImage bufferedImage = ImageUtilities.createBufferedImageForDisplay(frameImage);

            // Redimensionar a imagem para 200x200
            BufferedImage resizedImage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = resizedImage.createGraphics();
            g.drawImage(bufferedImage.getScaledInstance(200, 200, Image.SCALE_SMOOTH), 0, 0, null);
            g.dispose();

            // Caminho da pasta "imgs" no projeto
            String outputFolder = "\\\\DESKTOP-VO2TSQR\\Users\\gabri\\imgs";
            File folder = new File(outputFolder);
            if (!folder.exists()) {
                folder.mkdirs();  // Criar a pasta se ela não existir
            }

            // Nome do arquivo
            String fileName = "captured_" + System.currentTimeMillis() + ".jpg";
            File outputFile = new File(outputFolder + File.separator + fileName);

            // Salvar a imagem
            ImageIO.write(resizedImage, "jpg", outputFile);
            System.out.println("Imagem capturada e salva em: " + outputFile.getAbsolutePath());

            Posicao p = ImageServices.createGrid();
            System.out.println(p);

            pessoa.setX(p.getX()-1);
            pessoa.setY(p.getY());



        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
