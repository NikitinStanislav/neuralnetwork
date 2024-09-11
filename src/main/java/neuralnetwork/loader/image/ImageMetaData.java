package neuralnetwork.loader.image;

import neuralnetwork.loader.AbstractMeteData;

public class ImageMetaData extends AbstractMeteData {
    private int width;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    private int height;
}
