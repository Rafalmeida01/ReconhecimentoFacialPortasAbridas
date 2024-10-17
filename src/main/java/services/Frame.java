package services;

import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.video.capture.VideoCapture;
import org.openimaj.video.capture.VideoCaptureException;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

public class Frame {

    private JLabel label;
    private VideoCapture webCam;

    public Frame() {
        // Configurar o JFrame
        JFrame frame = new JFrame("Webcam capture - openIMAJ");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);
        frame.setLayout(new BorderLayout());

        // JLabel para mostrar a imagem da webcam
        label = new JLabel();
        frame.add(label, BorderLayout.CENTER);

        // Botões para capturar o frame e reconhecer
        JButton captureButton = new JButton("Capturar Frame");
        JButton recognizeButton = new JButton("Reconhecer");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(captureButton);
        buttonPanel.add(recognizeButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);

        // Configurar a webcam
        try {
            webCam = new VideoCapture(600, 480);

            // Thread para capturar o vídeo da webcam continuamente
            new Thread(() -> {
                while (true) {
                    try {
                        MBFImage frameImage = webCam.getNextFrame();
                        BufferedImage bufferedImage = ImageUtilities.createBufferedImageForDisplay(frameImage);
                        label.setIcon(new ImageIcon(bufferedImage));
                        Thread.sleep(33);  // Aproximadamente 30fps
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        } catch (VideoCaptureException e) {
            e.printStackTrace();
        }

        // Ação para capturar o frame e salvá-lo na pasta imgs
        captureButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                captureFrame();
            }
        });

        // Ação para capturar o frame, criar o grid e comparar
        recognizeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                recognizeFrame();
            }
        });
    }

    // Metodo para capturar o frame atual e salvar na pasta imgs
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
            String outputFolder = System.getProperty("user.dir") + "/src/main/resources/imgs";
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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Metodo para capturar o frame, criar o grid e comparar
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
            String tempFileName = System.getProperty("user.dir") + "/src/main/resources/imgs/temp_frame.jpg";
            File tempFile = new File(tempFileName);
            ImageIO.write(resizedFrame, "jpg", tempFile);

            // 2. Criar o grid com as imagens da pasta imgs
            ImageServices.createGrid();

            // 3. Comparar o frame capturado com o grid
            String gridFileName = System.getProperty("user.dir") + "/gridImage.jpg"; // Localização do grid criado
            System.out.println(gridFileName);
            ImageServices.reconhecimento(tempFileName, gridFileName);  // metodo que compar as duas imagens

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}