package neuralnetwork.loader.image;

import neuralnetwork.loader.BatchData;
import neuralnetwork.loader.Loader;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ImageLoader implements Loader {
    private String imageFileName;
    private String labelFileName;
    private int batchSize;
    private int imagePixelSize = 28;
    private int expectedSize = 10; //numbers 0...9

    private DataInputStream dsImages;
    private DataInputStream dsLabels;

    private ImageMetaData metaData;
    private Lock readLock = new ReentrantLock();

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
            //System.out.println("Number labels: " + numberItems);

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
            //System.out.println("Number images: " + numberItems);
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
        metaData.setNumberBathes((int)Math.ceil((double)numberItems/batchSize)); //if we got incomplete last batch, so it won't be missed

        return metaData;
    }

    @Override
    public ImageMetaData getMetaData() {
        return metaData;
    }

    @Override
    public BatchData readBatch() {
        readLock.lock();
        try {
            ImageBatchData batchData = new ImageBatchData();

            int inputItemsRead = readInputBatch(batchData);
            int expectedItemsRead = readExpectedBatch(batchData);

            if (inputItemsRead != expectedItemsRead){
                throw new LoaderException("Mismatch between images read and labels read");
            }
            metaData.setItemsRead(inputItemsRead);

            return batchData;
        } finally {
            readLock.unlock();
        }
    }

    private int readExpectedBatch(ImageBatchData batchData) {
        try {
            int totalItemsRead = metaData.getTotalItemsRead();
            int numberItems = metaData.getNumberItems();

            int numberToRead = Math.min(numberItems - totalItemsRead, batchSize);
            byte[] labelData = new byte[numberToRead];
            int expectedSize = metaData.getExpectedSize();

            int numberRead = dsLabels.read(labelData, 0, numberToRead);
            if (numberRead != numberToRead){
                throw new LoaderException("Couldn't read sufficient bytes from label data");
            }
            double[] data = new double[numberToRead * expectedSize];
            for (int i = 0; i < numberToRead; i++) {
                byte label = labelData[i]; //literally a number 0..9

                data[i * expectedSize + label] = 1;
            }
            batchData.setExpectedBatch(data);
            return numberToRead;
        } catch (IOException e) {
            throw new LoaderException("Error occurred reading label data", e);
        }
    }

    private int readInputBatch(ImageBatchData batchData) {
        try {
            int totalItemsRead = metaData.getTotalItemsRead();
            int numberItems = metaData.getNumberItems();

            int numberToRead = Math.min(numberItems - totalItemsRead, batchSize);
            int inputSize = metaData.getInputSize();
            int numberBytesToRead = numberToRead * inputSize;

            byte[] imageData = new byte[numberBytesToRead];

            int numberRead = dsImages.read(imageData, 0, numberBytesToRead);
            if (numberRead != numberBytesToRead){
                throw new LoaderException("Couldn't read sufficient bytes from image data");
            }

            double[] data = new double[numberBytesToRead];
            for (int i = 0; i < numberBytesToRead; i++) {
                data[i] = (imageData[i] & 0xFF)/256.0;
            }
            batchData.setInputBatch(data);

            return numberToRead;
        } catch (IOException e) {
            throw new LoaderException("Error occurred reading image data", e);
        }
    }
}
