package com.capture400d.eos.command;

import usb.jphoto.Command;
import usb.jphoto.Session;

public class GetDeviceInfo extends EOSCommand {

    public GetDeviceInfo(Session session) {
        super(Command.GetDeviceInfo, session);
    }
}
