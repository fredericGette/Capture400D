package com.capture400d.eos.command;

import usb.jphoto.Command;
import usb.jphoto.Session;

public class OpenSession extends EOSCommand {

    public OpenSession(Session session) {
        super(Command.OpenSession, session, session.getNextSessionID ());
    }
}
