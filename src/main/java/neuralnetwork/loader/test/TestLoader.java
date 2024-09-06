package neuralnetwork.loader.test;

import neuralnetwork.Util;
import neuralnetwork.loader.BatchData;
import neuralnetwork.loader.Loader;
import neuralnetwork.loader.MetaData;

public class TestLoader implements Loader {

    private int numberItems; //total number of items available for running through the network
    private int inputSize = 500; //how many items in single column (single instance)
    private int expectedSize = 3; //same as expected data
    private int batchSize;
    private int numberBathes;

    private int totalItemsRead;
    private int itemsRead;

    private MetaData metaData;

    public TestLoader(int numberItems, int batchSize) {
        this.numberItems = numberItems;
        this.batchSize = batchSize;

        metaData = new TestMetaData();
        metaData.setNumberItems(numberItems);

        numberBathes = numberItems / batchSize;

        if(numberItems % batchSize != 0){
            numberBathes += 1;
        }

        metaData.setNumberBathes(numberBathes);
        metaData.setInputSize(inputSize);
        metaData.setExpectedSize(expectedSize);
    }

    @Override
    public MetaData open() {
        return metaData;
    }

    @Override
    public void close() {

    }

    @Override
    public MetaData getMetaData() {
        return metaData;
    }

    @Override
    public BatchData readBatch() {
        if(totalItemsRead == numberItems) {
            return null;
        }
        itemsRead = batchSize;

        totalItemsRead += itemsRead;

        int excessItems = totalItemsRead - numberItems;

        if (excessItems > 0) {
            totalItemsRead -= excessItems;
            itemsRead -= excessItems;
        }

        var io = Util.generateTrainingArrays(inputSize, expectedSize, itemsRead);

        var batchData =  new TestBatchData();
        batchData.setInputBatch(io.getInput());
        batchData.setExpectedBatch(io.getOutput());

        metaData.setTotalItemsRead(totalItemsRead);
        metaData.setItemsRead(itemsRead);

        return batchData;
    }
}
