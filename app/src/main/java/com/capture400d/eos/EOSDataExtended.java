package com.capture400d.eos;

import java.util.ArrayList;
import java.util.List;

import usb.jphoto.Container;

public class EOSDataExtended extends Container {

    private List<EOSBlock> blocks;
    private EOSConstant currentOperation;
    
    public EOSDataExtended(byte[] buf, EOSConstant currentOperation) {
        super(buf);
        this.blocks = new ArrayList<EOSBlock>();
        this.currentOperation = currentOperation;
    }
 
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer ();
        sb.append(this.currentOperation).append("...\n");
        for (EOSBlock block : this.blocks) {
            sb.append(block.toString()).append("\n");
        }
        return sb.toString();
    }

    @Override
    public int getCode() {
        return this.currentOperation.getValue();
    }
    
    
}
