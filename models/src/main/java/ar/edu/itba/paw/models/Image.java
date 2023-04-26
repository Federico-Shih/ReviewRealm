package ar.edu.itba.paw.models;

public class Image {

    private final String id;
    private final String mediaType;
    private final byte[] image;

    public Image(String id, String mediaType, byte[] image) {
        this.id = id;
        this.mediaType = mediaType;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public String getMediaType() {
        return mediaType;
    }

    public byte[] getImage() {
        return image;
    }
}
