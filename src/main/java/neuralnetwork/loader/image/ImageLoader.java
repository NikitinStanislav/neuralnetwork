package neuralnetwork.loader.image;

import neuralnetwork.loader.BatchData;
import neuralnetwork.loader.Loader;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class ImageLoader implements Loader {
    private String imageFileName;
    private String labelFileName;
    private int batchSize;
    private int imagePixelSize = 28;
    private int expectedSize = 10; //numbers 0...9

    private DataInputStream dsImages;
    private DataInputStream dsLabels;

    private ImageMetaData metaData;

    public ImageLoader(String imageFileName, String labelFileName, int batchSize) {
        this.imageFileName = imageFileName;
        this.labelFileName = labelFileName;
        this.batchSize = batchSize;
    }

    @Override
    public ImageMetaData open() {
        try {
            dsImages = new DataInputStream(new FileInputStream(imageFileName));
        } catch (Exception e) {
            throw new LoaderException("Cannot open " + imageFileName, e);
        }

        try {
            dsLabels = new DataInputStream(new FileInputStream(labelFileName));
        } catch (Exception e) {
            throw new LoaderException("Cannot open " + labelFileName, e);
        }
        metaData = readMetaData();
        return metaData;
    }

    @Override
    public void close() {

        metaData = null;
        try {
            dsImages.close();
        } catch (Exception e){
            throw new LoaderException("Cannot close " + imageFileName, e);
        }

        try {
            dsLabels.close();
        } catch (Exception e){
            throw new LoaderException("Cannot close " + labelFileName, e);
        }

    }

    private ImageMetaData readMetaData(){
        metaData = new ImageMetaData();

        int numberItems = 0;

        try {
            int magicLabelNumber = dsLabels.readInt();
            if (magicLabelNumber != 2049) {
                throw new LoaderException("Magic label number of " + labelFileName + " is not verified");
            }
            numberItems = dsLabels.readInt();
            System.out.println("Number labels: " + numberItems);

            metaData.setNumberItems(numberItems);
        } catch (IOException e) {
            throw new LoaderException("Unable to read " + labelFileName, e);
        }

        try {
            int magicImageNumber = dsImages.readInt();
            if (magicImageNumber != 2051) {
                throw new LoaderException("Magic image number of" + imageFileName + " is not verified");
            }
            numberItems = dsImages.readInt();
            System.out.println("Number images: " + numberItems);
            int height = dsImages.readInt();
            int width = dsImages.readInt();
            if (height != width || height != imagePixelSize) {
                throw new LoaderException("Number of pixels is incorrect");
            }

            metaData.setHeight(height);
            metaData.setWidth(width);
            metaData.setInputSize(width * height);
        } catch (IOException e) {
            throw new LoaderException("Unable to read " + imageFileName, e);
        }
        metaData.setExpectedSize(expectedSize);
        metaData.setNumberBathes((int)Math.ceil((double)numberItems/batchSize)); //if we got incomplete last batch so it won't be missed

        return metaData;
    }

    @Override
    public ImageMetaData getMetaData() {
        return metaData;
    }

    @Override
    public BatchData readBatch() {
        return null;
    }
}
