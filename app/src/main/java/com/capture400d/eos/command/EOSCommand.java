package com.capture400d.eos.command;

import com.capture400d.eos.EOSConstant;

import usb.jphoto.Command;
import usb.jphoto.Session;

public class EOSCommand extends Command {

	public EOSCommand(byte[] buf) {
		super(buf);
	}
	
	public EOSCommand(int code, Session s, int param1) {
        super(code, s, param1);
    }

    public EOSCommand(int code, Session s, int param1, int param2, int param3) {
		super(code, s, param1, param2, param3);
	}
    
    public EOSCommand(int code, Session s, long param1, int param2) {
		super(code, s, param1, param2);
	}

	public EOSCommand(int code, Session s, int param1, int param2) {
		super(code, s, param1, param2);
	}

	public EOSCommand(int code, Session s, int param1, int param2, int param3, int param4) {
		super(code, s, param1, param2, param3, param4);
	}
	
	public EOSCommand(int code, Session s) {
		super(code, s);
	}

	@Override
	public String getCodeName(int code) {
	    EOSConstant eosConstant = EOSConstant.find(code);
        if (eosConstant != null) {
            return eosConstant.name();
        }
		return super.getCodeName(code);
	}

	
}
