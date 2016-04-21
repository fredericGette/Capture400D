package com.capture400d.eos.command;

import usb.jphoto.Session;

import com.capture400d.eos.EOSConstant;

public class GetObject extends EOSCommand {

    public GetObject(Session session, int objectId, int startIndex, int size) {
        super(EOSConstant.OperationCode_GetObject.getValue(), session, objectId, startIndex, size);
    }
}
