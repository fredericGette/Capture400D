package com.capture400d.eos.command;

import usb.jphoto.Session;

import com.capture400d.eos.EOSConstant;

public class SetPCConnectMode extends EOSCommand {

    public SetPCConnectMode(Session session, int mode) {
        super(EOSConstant.OperationCode_SetPCConnectMode.getValue(), session, mode);
    }
}
