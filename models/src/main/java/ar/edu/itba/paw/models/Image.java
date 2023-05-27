package ar.edu.itba.paw.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "images")
public class Image {
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "mediatype")
    private String mediaType;

    @Column(name = "data")
    private byte[] image;

    public Image(String id, String mediaType, byte[] image) {
        this.id = id;
        this.mediaType = mediaType;
        this.image = image;
    }

    public Image(String mediaType, byte[] image) {
        this.mediaType = mediaType;
        this.image = image;
    }

    public Image() {

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
