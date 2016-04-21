package com.capture400d.eos.command;

import usb.jphoto.Command;
import usb.jphoto.Session;

public class GetStorageInfo extends EOSCommand {

    public GetStorageInfo(Session session) {
        super(Command.GetStorageInfo, session, 0);
    }
}
