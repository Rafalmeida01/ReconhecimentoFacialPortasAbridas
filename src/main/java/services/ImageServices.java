package services;

import example.Classes.Posicao;
import org.openimaj.feature.FloatFV;
import org.openimaj.feature.FloatFVComparison;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.processing.face.detection.HaarCascadeDetector;
import org.openimaj.image.processing.face.detection.keypoints.FKEFaceDetector;
import org.openimaj.image.processing.face.detection.keypoints.KEDetectedFace;
import org.openimaj.image.processing.face.feature.FacePatchFeature;
import org.openimaj.image.processing.face.feature.comparison.FaceFVComparator;
import org.openimaj.image.processing.face.similarity.FaceSimilarityEngine;
import org.openimaj.math.geometry.shape.Rectangle;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class ImageServices {

    public static Posicao reconhecimento(String url1, String url2) throws IOException {

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
            double bestScore = Double.MIN_VALUE;
            String best = null;
            for (final Map.Entry<String, Double> matches : e.getValue().entrySet()) {
                System.out.println(matches.getKey() + " " + matches.getValue());
                if (matches.getValue() < 47) {
                    bestScore = matches.getValue();
                    best = matches.getKey();
                }
            }

// and this composites the original two images together, and draws
// the matching pair of faces:
            if(best != null) {
                final FImage img = new FImage(image1.width + image2.width, Math.max(image1.height, image2.height));
                img.drawImage(image1, 0, 0);
                img.drawImage(image2, image1.width, 0);

                img.drawShape(engine.getBoundingBoxes().get(e.getKey()), 1F);

                final Rectangle r = engine.getBoundingBoxes().get(best);
                r.translate(image1.width, 0);
                img.drawShape(r, 1F);

                //float x1 = r.getTopLeft().getX();
                //float x2 = r.getBottomRight().getX();
                //float x = (x1 + x2)/2;
                System.out.println(r.getTopLeft().getX());
                float x = (r.getTopLeft().getX()-200) / 200;
                float y = r.getTopLeft().getY() / 200;

                System.out.println((int)x + " " + (int)y);

// and finally displays the result
                //.display(img);
                return new Posicao((int) x, (int) y);
            }
        }
        return null;
    }

    public static Posicao createGrid() {
        String path = System.getenv("HOMEPATH")+"\\Desktop\\imgs";
        int w = 200;
        int h = 200;
        int gridSize = 10;

        BufferedImage grid = new BufferedImage(w * gridSize, h * gridSize, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = grid.createGraphics();

        int imageCounter = 0;

        File folder = new File(path);
        File[] files = folder.listFiles();
        int posicao = files.length - 1;

        if (files != null) {
            for (File file : files) {

                // Ignorar o arquivo temp_frame.jpg
                //if (file.getName().equals("temp_frame.jpg")) {
                //    continue;
                //}

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
            String gridFileName =System.getenv("HOMEPATH") + "/Desktop/grids/grid1.jpg";
            ImageIO.write(grid, "jpg", new File(gridFileName));
            System.out.println("Grid criado com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao criar o grid.");
            e.printStackTrace();
        }

        Posicao p = new Posicao();
        p.setX(posicao%10);
        p.setY(posicao/10);
        return p;
    }

}