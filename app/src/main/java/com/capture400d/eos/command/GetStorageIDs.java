package com.capture400d.eos.command;

import usb.jphoto.Command;
import usb.jphoto.Session;

public class GetStorageIDs extends EOSCommand {

    public GetStorageIDs(Session session) {
        super(Command.GetStorageIDs, session);
    }
}
