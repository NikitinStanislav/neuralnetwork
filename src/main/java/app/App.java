package app;

import neuralnetwork.loader.BatchData;
import neuralnetwork.loader.Loader;
import neuralnetwork.loader.MetaData;
import neuralnetwork.loader.image.ImageLoader;

import java.io.File;

public class App {
    public static void main(String[] args) {

        if (args.length == 0){
            System.out.println("Usage: [app] <MNIST DATA DIRECTIY>");
            return;
        }

        String directory = args[0];
        if (!new File(directory).isDirectory()){
            System.out.println("'"+directory+"' is not a directory");
        }

//        try {
//            System.out.println(new File(args[0]).getCanonicalPath());
//        } catch (Exception e){}

        final String trainImages = String.format("%s%s%s", directory, File.separator, "train-images.idx3-ubyte");
        final String trainLabels = String.format("%s%s%s", directory, File.separator, "train-labels.idx1-ubyte");
        final String testImages = String.format("%s%s%s", directory, File.separator, "t10k-images.idx3-ubyte");
        final String testLabels = String.format("%s%s%s", directory, File.separator, "t10k-labels.idx1-ubyte");

        Loader trainLoader = new ImageLoader(trainImages, trainLabels, 32);
        Loader testLoader = new ImageLoader(testImages, testLabels, 32);

        trainLoader.open();
        MetaData metaData = testLoader.open();

        for (int i = 0; i < metaData.getNumberBathes(); i++) {
            BatchData batchData = testLoader.readBatch();
        }

        trainLoader.close();
        testLoader.close();
    }
}
