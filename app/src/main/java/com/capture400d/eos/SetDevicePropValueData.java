package com.capture400d.eos;

import usb.jphoto.Session;

public class SetDevicePropValueData extends EOSData {

	public SetDevicePropValueData(Session session, EOSConstant property, int propertyValue) {
        super(EOSConstant.OperationCode_SetDevicePropValue.getValue(), session, property.getValue(), propertyValue);
    }
}
