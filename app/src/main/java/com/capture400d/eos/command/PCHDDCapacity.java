package com.capture400d.eos.command;

import usb.jphoto.Session;

import com.capture400d.eos.EOSConstant;

public class PCHDDCapacity extends EOSCommand {

    public PCHDDCapacity(Session session, long capacity, int mode) {
        super(EOSConstant.OperationCode_PCHDDCapacity.getValue(), session, capacity, mode);
    }
}
