package project;


import org.openimaj.feature.FloatFV;
import org.openimaj.feature.FloatFVComparison;
import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.processing.face.detection.HaarCascadeDetector;
import org.openimaj.image.processing.face.detection.keypoints.FKEFaceDetector;
import org.openimaj.image.processing.face.detection.keypoints.KEDetectedFace;
import org.openimaj.image.processing.face.feature.FacePatchFeature;
import org.openimaj.image.processing.face.feature.comparison.FaceFVComparator;
import org.openimaj.image.processing.face.similarity.FaceSimilarityEngine;
import org.openimaj.math.geometry.shape.Rectangle;
import org.openimaj.video.capture.VideoCapture;

import org.openimaj.video.capture.VideoCaptureException;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.

    public class Main {
        public static void main(String[] args) throws IOException, InterruptedException {
          JFrame frame = new JFrame("Webcam capture - openIMAJ");
          frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
          frame.setSize(500, 500);
          JLabel label = new JLabel();
          frame.add(label);
          frame.setVisible(true);

          try {
              VideoCapture webCam = new VideoCapture(600, 480);
              while (true) {
                  MBFImage frameImage = webCam.getNextFrame();
                  BufferedImage bufferedImage = ImageUtilities.createBufferedImageForDisplay(frameImage);

                  label.setIcon(new ImageIcon(bufferedImage));

                  Thread.sleep(33);
              }
          } catch (VideoCaptureException | InterruptedException e) {
              e.printStackTrace();
          }


            // Load the native

    final URL image1url = new URL("https://jpimg.com.br/uploads/2023/06/yurialberto.png");

    final URL image2url = new URL("https://centraldotimao.com.br/wp-content/uploads/2022/09/FdyAf-3WYAAwoZb.jpg");



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
}
