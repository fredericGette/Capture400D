package com.capture400d;

public class ObjectTransfer {
    private String filename;
    private int size;
    private int objectId;

    public ObjectTransfer(String filename, int size, int objectId) {
        this.filename = filename;
        this.size = size;
        this.objectId = objectId;
    }

    public String getFilename() {
        return filename;
    }

    public int getSize() {
        return size;
    }

    public int getObjectId() {
        return objectId;
    }
}
