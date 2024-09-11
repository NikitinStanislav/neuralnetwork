package neuralnetwork.loader.image;

public class LoaderException extends RuntimeException {

    public LoaderException(String message, Throwable cause) {
        super(message, cause);
    }

    public LoaderException(String message) {
        super(message);
    }
}
