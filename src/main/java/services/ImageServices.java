package services;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class ImageServices {

    public static void createGrid() {
        String path = System.getenv("HOMEPATH") + "\\Desktop\\output";
        int w = 200;
        int h = 200;
        int gridSize = 10;

        BufferedImage grid = new BufferedImage(w * gridSize, h * gridSize, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = grid.createGraphics();

        int imageCounter = 0;

        File folder = new File(path);
        File[] files = folder.listFiles();

        if (files != null) {
            for (File file : files) {
                try {
                    BufferedImage img = ImageIO.read(file);

                    // Calcular a posição da imagem no grid
                    int x = (imageCounter % gridSize) * w; // Posição X
                    int y = (imageCounter / gridSize) * h; // Posição Y

                    g.drawImage(img, x, y, null);

                    imageCounter++;

                    // Interromper após 100 imagens
                    if (imageCounter == 100) {
                        break;
                    }

                } catch (Exception e) {
                    System.out.println("Erro ao carregar a imagem: " + file.getName());
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("Erro: Pasta não encontrada ou vazia.");
        }

        g.dispose();

        try {
            // Salvar a imagem grid criada
            File gridImg = new File(System.getenv("HOMEPATH") + "\\Desktop\\gridImage.jpg");
            ImageIO.write(grid, "jpg", gridImg);
            System.out.println("Grid criado com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao criar o grid.");
            e.printStackTrace();
        }
    }

    public static void resizeInputs() {
        String inputFolder = System.getenv("HOMEPATH") + "\\Desktop\\imgs";
        String outputFolder = System.getenv("HOMEPATH") + "\\Desktop\\output";

        int w = 200;
        int h = 200;

        File folder = new File(inputFolder);
        File[] files = folder.listFiles();

        if (files != null) {
            for (File file : files) {
                try {
                    BufferedImage img = ImageIO.read(file);

                    BufferedImage resizedImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
                    Graphics2D g = resizedImage.createGraphics();
                    g.drawImage(img.getScaledInstance(w, h, Image.SCALE_SMOOTH), 0, 0, null);
                    g.dispose();

                    // Criar o nome do arquivo de saída
                    String outputFileName = outputFolder + File.separator + file.getName();

                    // Salvar a imagem redimensionada
                    ImageIO.write(resizedImage, "jpg", new File(outputFileName));

                    System.out.println("Imagem redimensionada: " + file.getName());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("Erro: Pasta não encontrada ou vazia.");
        }
    }

}