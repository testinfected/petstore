package test.com.pyxis.petstore.util;

import com.pyxis.petstore.domain.product.AttachmentStorage;
import com.pyxis.petstore.util.FileSystemPhotoStore;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class FileSystemPhotoStoreTest {

    AttachmentStorage attachmentStorage = new FileSystemPhotoStore("/attachments");

    @Test public void
    photosAreStoredRelativelyToStorageRoot() {
        assertThat("photo location", attachmentStorage.getLocation("photo.png"), equalTo("/attachments/photo.png"));
    }
}
