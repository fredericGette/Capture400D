package com.capture400d.eos.command;

import com.capture400d.eos.EOSConstant;

import usb.jphoto.Session;

public class GetEvent extends EOSCommand {

    public GetEvent(Session session) {
        super(EOSConstant.OperationCode_GetEvent.getValue(), session);
    }
}
