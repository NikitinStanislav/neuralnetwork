package neuralnetwork.loader.test;

import matrix.Matrix;
import neuralnetwork.loader.BatchData;
import neuralnetwork.loader.Loader;
import neuralnetwork.loader.MetaData;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestTestLoader {

    @Test
    public void test(){

        int batchSize = 33;
        Loader testLoader = new TestLoader(600, batchSize);

        MetaData metaData = testLoader.open();

        int numberItems = metaData.getNumberItems();

        int lastBatchSize = numberItems % batchSize;

        int numberBatches = metaData.getNumberBathes();

        for (int i = 0; i < metaData.getNumberBathes(); i++) {
            BatchData batchData = testLoader.readBatch();

            assertTrue(batchData != null);

            int itemsRead = metaData.getItemsRead();

            int inputSize = metaData.getInputSize();
            int expectedSize = metaData.getExpectedSize();

            Matrix input = new Matrix(inputSize, itemsRead, batchData.getInputBatch());
            Matrix expected = new Matrix(expectedSize, itemsRead, batchData.getExpectedBatch());

            assertTrue(input.sum() != 0);
            assertTrue(expected.sum() == itemsRead);

            if (i == numberBatches-1){
                assertEquals(itemsRead, lastBatchSize);
            }
            else {
                assertEquals(itemsRead, batchSize);
            }
        }
    }
}
