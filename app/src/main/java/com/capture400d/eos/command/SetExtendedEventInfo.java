package com.capture400d.eos.command;

import usb.jphoto.Session;

import com.capture400d.eos.EOSConstant;

public class SetExtendedEventInfo extends EOSCommand {

    public SetExtendedEventInfo(Session session) {
        super(EOSConstant.OperationCode_SetExtendedEventInfo.getValue(), session, 1);
    }
}
