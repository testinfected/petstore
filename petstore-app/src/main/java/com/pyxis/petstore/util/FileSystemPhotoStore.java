package com.pyxis.petstore.util;

import com.pyxis.petstore.domain.product.AttachmentStorage;
import com.pyxis.petstore.domain.product.Product;
import org.springframework.stereotype.Service;

@Service
public class FileSystemPhotoStore implements AttachmentStorage {
    private final String rootPath;

    public FileSystemPhotoStore(String rootPath) {
        this.rootPath = rootPath;
    }

    public String getLocation(String name) {
        return rootPath + "/" + name;
    }
}
