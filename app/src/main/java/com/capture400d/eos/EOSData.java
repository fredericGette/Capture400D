package com.capture400d.eos;

import com.capture400d.ObjectTransfer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import usb.jphoto.Data;
import usb.jphoto.Session;

public class EOSData extends Data {

    private List<EOSBlock> blocks;
    
	public EOSData(byte[] buf) {
		super(buf);
		this.blocks = new ArrayList<EOSBlock>();
		EOSConstant code = EOSConstant.find(getCode());
		if (code != null) {
    		switch(code) {
    		case OperationCode_GetEvent:
    		    parseBlocks();
    		    break;
    		case OperationCode_SetDevicePropValue:
    		    parseBlocks();
    		    break;
    		}
		}
	}
	
    public EOSData(int code, Session s, int param1, int param2) {
    	super (false, new byte [HDR_LEN + (4 * 3)]);
    	this.blocks = new ArrayList<EOSBlock>();
		putHeader(getData().length, 2, code, s.getXID());
		put32 (4*3);
		put32 (param1);
		put32 (param2);
		if (code == EOSConstant.OperationCode_SetDevicePropValue.getValue()) {
			parseBlocks();
		}
	}

	private void parseBlocks() {
        int index = 12;
        while (index+4 <= getData().length) {
            int recordLength = getS32(index);
            if (recordLength == 0) {
            	break;
            }
            EOSBlock block = new EOSBlock(Arrays.copyOfRange(getData(), index, index+recordLength));
            blocks.add(block);
            index += recordLength;
        }
    }

    
	@Override
	public String getCodeName(int code) {
	    EOSConstant eosConstant = EOSConstant.find(code);
	    if (eosConstant != null) {
	        return eosConstant.name();
	    }
		return super.getCodeName(code);
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer ();
        sb.append(super.toString());
//        sb.append("\n");
//        for (EOSBlock block : this.blocks) {
//        	sb.append(block.toString()).append("\n");
//        }
		return sb.toString();
	}

	public List<ObjectTransfer> getRequestObjectTransfers() {
		List<ObjectTransfer> objects = null;
		for (EOSBlock block : blocks) {
			if (EOSConstant.Event_RequestObjectTransfer.equals(block.getCodeName())) {
				if (objects == null) {
					objects = new ArrayList<ObjectTransfer>();
				}
				objects.add(block.getObjectTransfer());
			}
		}
		return objects;
	}

	public Integer getCaptureDestination() {
        for (EOSBlock block : blocks) {
            if (EOSConstant.DeviceProperty_CaptureDestination.equals(block.getCodeName())) {
                return block.getPropertyValue();
            }
            if (EOSConstant.Event_DevPropChanged.equals(block.getCodeName())) {
                if (EOSConstant.DeviceProperty_CaptureDestination.equals(block.getPropertyName())) {
                    return block.getEventPropertyValue();
                }
            }
        }
        return null;
	}
	
	public Integer getTransferOption() {
        for (EOSBlock block : blocks) {
            if (EOSConstant.Event_DevPropChanged.equals(block.getCodeName())) {
                if (EOSConstant.DeviceProperty_TransferOption.equals(block.getPropertyName())) {
                    return block.getEventPropertyValue();
                }
            }
        }
        return null;
	}
	
	public boolean isHalfPushReleaseButton() {
        for (EOSBlock block : blocks) {
            if (EOSConstant.Event_HalfPushReleaseButton.equals(block.getCodeName())) {
                return true;
            }
        }
        return false;
	}
	
	public Integer getCapture() {
        for (EOSBlock block : blocks) {
            if (EOSConstant.Event_Capture.equals(block.getCodeName())) {
                return block.getPropertyValue();
            }
        }
        return null;
	}
}
