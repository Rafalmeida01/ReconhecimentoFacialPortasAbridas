package services;

import org.openimaj.feature.FloatFV;
import org.openimaj.feature.FloatFVComparison;
import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.processing.face.detection.HaarCascadeDetector;
import org.openimaj.image.processing.face.detection.keypoints.FKEFaceDetector;
import org.openimaj.image.processing.face.detection.keypoints.KEDetectedFace;
import org.openimaj.image.processing.face.feature.FacePatchFeature;
import org.openimaj.image.processing.face.feature.comparison.FaceFVComparator;
import org.openimaj.image.processing.face.similarity.FaceSimilarityEngine;
import org.openimaj.math.geometry.shape.Rectangle;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import javax.imageio.ImageIO;

public class ImageServices {

    public static void reconhecimento(String url1, String url2) throws IOException {

        final File image1url = new File(url1);
        final File image2url = new File(url2);

        final FImage image1 = ImageUtilities.readF(image1url);
        final FImage image2 = ImageUtilities.readF(image2url);

        // then we set up a face detector; will use a haar cascade detector to
        // find faces, followed by a keypoint-enhanced detector to find facial
        // keypoints for our feature. There are many different combinations of
        // features and detectors to choose from.
        final HaarCascadeDetector detector = HaarCascadeDetector.BuiltInCascade.frontalface_alt2.load();
        final FKEFaceDetector kedetector = new FKEFaceDetector(detector);

        // now we construct a feature extractor - this one will extract pixel
        // patches around prominant facial keypoints (like the corners of the
        // mouth, etc) and build them into a vector.
        final FacePatchFeature.Extractor extractor = new FacePatchFeature.Extractor();

        // in order to compare the features we need a comparator. In this case,
        // we'll use the Euclidean distance between the vectors:
        final FaceFVComparator<FacePatchFeature, FloatFV> comparator =
                new FaceFVComparator<FacePatchFeature, FloatFV>(FloatFVComparison.EUCLIDEAN);

        // Now we can construct the FaceSimilarityEngine. It is capable of
        // running the face detector on a pair of images, extracting the
        // features and then comparing every pair of detected faces in the two
        // images:
        final FaceSimilarityEngine<KEDetectedFace, FacePatchFeature, FImage> engine =
                new FaceSimilarityEngine<KEDetectedFace, FacePatchFeature, FImage>(kedetector, extractor, comparator);

        // we need to tell the engine to use our images:
        engine.setQuery(image1, "image1");
        engine.setTest(image2, "image2");

        // and then to do its work of detecting, extracting and comparing
        engine.performTest();

        // finally, for this example, we're going to display the "best" matching
        // faces in the two images. The following loop goes through the map of
        // each face in the first image to all the faces in the second:
        for (final Map.Entry<String, Map<String, Double>> e : engine.getSimilarityDictionary().entrySet()) {
            // this computes the matching face in the second image with the
            // smallest distance:
            double bestScore = Double.MAX_VALUE;
            String best = null;
            for (final Map.Entry<String, Double> matches : e.getValue().entrySet()) {
                if (matches.getValue() < bestScore) {
                    bestScore = matches.getValue();
                    best = matches.getKey();
                }
            }

            // and this composites the original two images together, and draws
            // the matching pair of faces:
            final FImage img = new FImage(image1.width + image2.width, Math.max(image1.height, image2.height));
            img.drawImage(image1, 0, 0);
            img.drawImage(image2, image1.width, 0);

            img.drawShape(engine.getBoundingBoxes().get(e.getKey()), 1F);

            final Rectangle r = engine.getBoundingBoxes().get(best);
            r.translate(image1.width, 0);
            img.drawShape(r, 1F);

            // and finally displays the result
            DisplayUtilities.display(img);
        }
    }

    public static void createGrid() {
        String path = System.getenv("HOMEPATH")+"\\temp\\Reconhecimento-Facial-Java-Fiec\\src\\main\\resources\\imgs";
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

                // Ignorar o arquivo temp_frame.jpg
                if (file.getName().equals("temp_frame.jpg")) {
                    continue;
                }

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
            File gridImg = new File(System.getProperty("user.dir") + "/gridImage.jpg");
            ImageIO.write(grid, "jpg", gridImg);
            System.out.println("Grid criado com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao criar o grid.");
            e.printStackTrace();
        }
    }

}