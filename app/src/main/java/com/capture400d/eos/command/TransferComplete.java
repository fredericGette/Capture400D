package com.capture400d.eos.command;

import usb.jphoto.Session;

import com.capture400d.eos.EOSConstant;

public class TransferComplete extends EOSCommand {

    public TransferComplete(Session session, int objectId) {
        super(EOSConstant.OperationCode_TransferComplete.getValue(), session, objectId);
    }
}
