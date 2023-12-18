package ar.edu.itba.paw.webapp.controller.helpers;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.List;

public class ImageValidatorHelper {

    private static final long VALID_SIZE = 128 * 1024 * 1024; // 128 MB
    private static final List<String> VALID_TYPES = Arrays.asList(MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_GIF_VALUE);

    private ImageValidatorHelper() {
        /* Utility */
    }

    public static boolean isValid(FormDataBodyPart value) {
        return value == null || value.getMediaType() != null && VALID_TYPES.contains(value.getMediaType().toString())  && value.getContentDisposition().getSize() <= VALID_SIZE;
    }
}
