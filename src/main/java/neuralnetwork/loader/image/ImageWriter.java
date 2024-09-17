package neuralnetwork.loader.image;

import neuralnetwork.NeuralNetwork;
import neuralnetwork.loader.BatchData;
import neuralnetwork.loader.Loader;
import neuralnetwork.loader.MetaData;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class ImageWriter {
    public static void main(String[] args) {
        if (args.length == 0){
            System.out.println("Usage: [app] <MNIST DATA DIRECTIY>");
            return;
        }

        String directory = args[0];
        if (!new File(directory).isDirectory()){
            System.out.println("'"+directory+"' is not a directory");
        }

        new ImageWriter().run(directory);
    }

    private int convertOneHotToInt(double[] labelData, int offset, int oneHotSize){

        double maxValue = 0;
        int maxIndex = 0;

        for (int i = 0; i < oneHotSize; i++) {
            if (labelData[offset+i] > maxValue){
                maxValue = labelData[offset+i];
                maxIndex = i;
            }
        }

       /* for (int i = 0; i < oneHotSize; i++) {
            //System.out.print(labelData[offset + i] + " ");
            if((Math.abs(labelData[offset+i] - 1)) < 0.001){
                return i;
            }
        }*/

        //throw new RuntimeException("Invalid one hot vector");
        return maxIndex;
    }

    private void run(String directory) {
        final String trainImages = String.format("%s%s%s", directory, File.separator, "train-images.idx3-ubyte");
        final String trainLabels = String.format("%s%s%s", directory, File.separator, "train-labels.idx1-ubyte");
        final String testImages = String.format("%s%s%s", directory, File.separator, "t10k-images.idx3-ubyte");
        final String testLabels = String.format("%s%s%s", directory, File.separator, "t10k-labels.idx1-ubyte");

        int batchSize = 900;

        ImageLoader trainLoader = new ImageLoader(trainImages, trainLabels, batchSize);
        ImageLoader testLoader = new ImageLoader(testImages, testLabels, batchSize);

        ImageLoader loader = testLoader;

        ImageMetaData metaData = loader.open();

        NeuralNetwork neuralNetwork = NeuralNetwork.load("mnistNeural0.net");

        int imageWidth = metaData.getWidth();
        int imageHeight = metaData.getHeight();

        int labelSize = metaData.getExpectedSize(); //10

        for (int i = 0; i < metaData.getNumberBathes(); i++) {
            BatchData batchData = testLoader.readBatch();

            int numberOfImages = metaData.getItemsRead();

            int horizontalImages = (int)Math.sqrt(numberOfImages);
            while(numberOfImages % horizontalImages != 0){
                ++horizontalImages;   //until it fit
            }
            int verticalImages = numberOfImages/horizontalImages;

            int canvasWidth = horizontalImages * imageWidth;
            int canvasHeight = verticalImages * imageHeight;

            String montagePath = String.format("montage%d.jpg", i);
            System.out.println("Writing "+ montagePath);

            BufferedImage montage = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_RGB);

            double[] pixelData = batchData.getInputBatch();
            double[] labelData = batchData.getExpectedBatch();

            int imageSize = imageHeight * imageWidth;
            boolean[] correct = new boolean[numberOfImages];

            for (int j = 0; j <numberOfImages; j++) {
                double[] singleImage = Arrays.copyOfRange(pixelData, j * imageSize, (j + 1) * imageSize);
                double[] singleLabel = Arrays.copyOfRange(labelData, j * labelSize, (j + 1) * labelSize);

                double[] predictedLabel = neuralNetwork.predict(singleImage);
                int predicted = convertOneHotToInt(predictedLabel, 0, labelSize);
                int actual = convertOneHotToInt(singleLabel, 0, labelSize);

                correct[j] = predicted == actual;
            }

            for (int pixelIndex = 0; pixelIndex < pixelData.length; pixelIndex++) {
                int imageNumber = pixelIndex / imageSize;
                int pixelNumber = pixelIndex % imageSize;

                int montageRow = imageNumber / horizontalImages;
                int montageCol = imageNumber % horizontalImages;

                int pixelRow = pixelNumber / imageWidth;
                int pixelCol = pixelNumber % imageWidth;

                int x = montageCol * imageWidth + pixelCol;
                int y = montageRow * imageHeight + pixelRow;

                double pixelValue = pixelData[pixelIndex];
                int color = (int)(0x100 * pixelValue);
                int pixelColor = 0;// (color << 16) + (color << 8) + color;

                if (correct[imageNumber]){
                    pixelColor = (color << 16) + (color << 8) + color;
                } else {
                    pixelColor = (color << 16);// + (color << 8) + color;
                }

                montage.setRGB(x, y, pixelColor);
            }

            try {
                ImageIO.write(montage, "jpg", new File(montagePath));
            } catch (IOException e) {
                throw new LoaderException("Image is not written" ,e);
            }

            StringBuilder sb = new StringBuilder();

            for (int labelIndex = 0; labelIndex < numberOfImages; labelIndex++) {

                if(labelIndex % horizontalImages == 0){
                    sb.append("\n");
                }

                int label = convertOneHotToInt(labelData, labelIndex * labelSize, labelSize);
                sb.append(String.format("%d ", label));
            }
            String labelPath = String.format("label%d.txt", i);
            System.out.println("Writing "+labelPath);
            try {
                FileWriter fw = new FileWriter(labelPath);
                fw.write(sb.toString());
                fw.close();
            } catch (IOException e) {
                throw new LoaderException("Fail to write a label", e);
            }
        }
        loader.close();
    }
}
