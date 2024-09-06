package neuralnetwork.loader;

public abstract class AbstractMeteData implements MetaData{

    private int numberItems; //total number of items available for running through the network
    private int inputSize; //how many items in single column (single instance)
    private int expectedSize; //same as expected data
    private int numberBathes;
    private int totalItemsRead;
    private int itemsRead;

    @Override
    public int getNumberItems() {
        return numberItems;
    }

    @Override
    public void setNumberItems(int numberItems) {
        this.numberItems = numberItems;
    }

    @Override
    public int getInputSize() {
        return inputSize;
    }

    @Override
    public void setInputSize(int inputSize) {
        this.inputSize =inputSize;
    }

    @Override
    public int getExpectedSize() {
        return expectedSize;
    }

    @Override
    public void setExpectedSize(int expectedSize) {
        this.expectedSize = expectedSize;
    }

    @Override
    public int getNumberBathes() {
        return numberBathes;
    }

    @Override
    public void setNumberBathes(int numberBathes) {
        this.numberBathes = numberBathes;
    }

    @Override
    public int getTotalItemsRead() {
        return totalItemsRead;
    }

    @Override
    public void setTotalItemsRead(int totalItemsRead) {
        this.totalItemsRead = totalItemsRead;
    }

    @Override
    public int getItemsRead() {
        return itemsRead;
    }

    @Override
    public void setItemsRead(int itemsRead) {
        this.itemsRead = itemsRead;
    }
}
