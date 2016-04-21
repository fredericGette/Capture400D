package com.capture400d.eos.command;

import usb.jphoto.Session;

import com.capture400d.eos.EOSConstant;

public class SetDevicePropValue extends EOSCommand {

    public SetDevicePropValue(Session session) {
        super(EOSConstant.OperationCode_SetDevicePropValue.getValue(), session);
    }
}
