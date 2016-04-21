package com.capture400d.eos.command;

import usb.jphoto.Session;

import com.capture400d.eos.EOSConstant;

public class SetUILock extends EOSCommand {

    public SetUILock(Session session) {
        super(EOSConstant.OperationCode_SetUILock.getValue(), session);
    }
}
