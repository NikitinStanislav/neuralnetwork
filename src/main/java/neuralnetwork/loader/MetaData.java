package neuralnetwork.loader;

public interface MetaData {
   /** int numberItems; //total number of items available for running through the network
    int inputSize; //how many items in single column (single instance)
    int expectedSize; //same as expected data
    int numberBathes;
    int totalItemsRead;
    int itemsRead; //in current batch (or current read)*/

    public int getNumberItems();

    public void setNumberItems(int numberItems);

    public int getInputSize();

    public void setInputSize(int inputSize);

    public int getExpectedSize();

    public void setExpectedSize(int expectedSize);

    public int getNumberBathes();

    public void setNumberBathes(int numberBathes);

    public int getTotalItemsRead();

    public void setTotalItemsRead(int totalItemsRead);

    public int getItemsRead();

    public void setItemsRead(int itemsRead);
}
