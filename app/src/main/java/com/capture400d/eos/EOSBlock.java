package com.capture400d.eos;

import com.capture400d.ObjectTransfer;

import usb.jphoto.Buffer;

public class EOSBlock extends Buffer {

    EOSBlock(byte[] buf) {
        super(buf);
    }
    
    @Override
    public String toString() {
    	if (this.getData().length < 8) {
    		return " Empty Block";
    	}
        StringBuffer sb = new StringBuffer ();
        int recordLength = getS32(0);
        sb.append(" recordLength=").append(toStringInteger(recordLength)).append("\n");
        int eventCode = getCode();
        sb.append(" eventCode=").append(toStringCode(eventCode)).append("\n");
        if (eventCode == 0) {
            sb.append(" End of block");
        } else {
            EOSConstant event = EOSConstant.find(eventCode);
            if (event != null) {
                switch (event) {
                case Event_DevPropChanged:
                    int devicePropertyCode = getPropertyCode();
                    sb.append(" devicePropertyCode=").append(toStringCode(devicePropertyCode)).append("\n");
                    sb.append(" devicePropertyValue=");
                    EOSConstant deviceProperty = EOSConstant.find(devicePropertyCode);
                    if (deviceProperty != null) {
                    	switch (deviceProperty) {
                    	case DeviceProperty_Iso: {
                            int devicePropertyValue = getEventPropertyValue();
                            EOSISO iso = EOSISO.find(devicePropertyValue);
                            if (iso != null) {
                            	sb.append(iso).append("\n");
                            } else {
                            	sb.append(toStringInteger(devicePropertyValue)).append("\n");
                            }
                    		break;
                    	}
                    	default:
                            int devicePropertyValue = getEventPropertyValue();
                    		sb.append(toStringInteger(devicePropertyValue)).append("\n");
                    	}
                    } else {
                        int devicePropertyValue = getEventPropertyValue();
                        sb.append(toStringInteger(devicePropertyValue)).append("\n");
                    }
                    break;
                case Event_RequestObjectTransfer:
                    int filenameLength = getU8(27);
                    sb.append(" object id=").append(toStringInteger(getObjectId())).append("\n");
                    sb.append(" object size=").append(toStringInteger(getObjectSize())).append("\n");
                    sb.append(" filenameLength=").append(filenameLength).append("(").append(Integer.toHexString(filenameLength)).append(")\n");
                    sb.append(" filename=").append(getObjectFileName()).append("\n");
                    break;
                case Event_Capture:
                    int value = getS32(8);
                    sb.append(" value=").append(toStringInteger(value)).append("\n");;
                    break;
                case DeviceProperty_CaptureDestination:
                case DeviceProperty_UnixTime:
                    sb.append(" devicePropertyValue=");
                    int devicePropertyValue = getPropertyValue();
                    sb.append(toStringInteger(devicePropertyValue)).append("\n");
                }
            }
        }
        return sb.toString();
    }

    private String toStringInteger(int value) {
        StringBuffer sb = new StringBuffer ();
        sb.append(value).append("(").append(Integer.toHexString(value)).append(")");
        return sb.toString();
    }
    
    private String toStringCode(int value) {
        StringBuffer sb = new StringBuffer ();
        EOSConstant eosConstant = EOSConstant.find(value);
        if (eosConstant != null) {
            sb.append(eosConstant.name()).append("(").append(Integer.toHexString(value)).append(")");
            return sb.toString();
        }
        return toStringInteger(value);
    }
    
    
    public int getCode() {
        return getS32(4);
    }
    
    public EOSConstant getCodeName() {
        return EOSConstant.find(getCode());
    }
    
    public int getPropertyCode() {
        return getS32(8);
    }
    
    public EOSConstant getPropertyName() {
        return EOSConstant.find(getPropertyCode());
    }
    
    private String getObjectFileName() {
        int filenameLength = getU8(27);
        StringBuilder str = new StringBuilder(filenameLength);
        for (int i = 0; i < filenameLength; i++) {
            char c = (char) getS8(28+i);
            if (c == 0) {
                break;
            }
            str.append(c);
        }
        return str.toString();
    }
    
    private Integer getObjectSize() {
        return getS32(20);
    }

    private Integer getObjectId() {
        return getS32(8);
    }
    
    public ObjectTransfer getObjectTransfer() {
    	return new ObjectTransfer(getObjectFileName(), getObjectSize(), getObjectId());
    }

    public int getPropertyValue() {
        return getS32(8);
    }
    
    public int getEventPropertyValue() {
        return getS32(12);
    }
}
