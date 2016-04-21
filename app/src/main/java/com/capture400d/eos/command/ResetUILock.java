package com.capture400d.eos.command;

import usb.jphoto.Session;

import com.capture400d.eos.EOSConstant;

public class ResetUILock extends EOSCommand {

    public ResetUILock(Session session) {
        super(EOSConstant.OperationCode_ResetUILock.getValue(), session);
    }
}
