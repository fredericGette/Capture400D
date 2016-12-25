package com.capture400d.eos;

//see https://github.com/felis/PTP_2.0/blob/master/canoneos.h
//see http://magiclantern.wikia.com/wiki/PTP
public enum EOSConstant {

	OperationCode_GetStorageIDs(0x9101),
	OperationCode_GetStorageInfo(0x9102),
	OperationCode_GetObject(0x9107),
	OperationCode_GetDeviceInfoEx(0x9108),
	OperationCode_GetObjectIDs(0x9109),
	OperationCode_Capture(0x910f),
	OperationCode_SetDevicePropValue(0x9110),
    OperationCode_SetPCConnectMode(0x9114),
    OperationCode_SetExtendedEventInfo(0x9115),
    OperationCode_GetEvent(0x9116),
    OperationCode_TransferComplete(0x9117),
    OperationCode_CancelTransfer(0x9118),
    OperationCode_ResetTransfer(0x9119),
    OperationCode_GetDevicePropValue(0x9127),
    OperationCode_GetLiveViewPicture(0x9153),
    OperationCode_MoveFocus(0x9155),
    OperationCode_PCHDDCapacity(0x911A),
    OperationCode_SetUILock(0x911B),
    OperationCode_ResetUILock(0x911C),
    
    ReponseCode_OK(0x2001),
    ReponseCode_Busy(0x2019),
    
    Event_ObjectCreated(0xC181),
    Event_RequestObjectTransfer(0xC186),
    Event_DevPropChanged(0xC189),
    Event_DevPropValuesAccepted(0xC18A),
    Event_Capture(0xC18B),
    Event_HalfPushReleaseButton(0xC18E),
    
    
    DeviceProperty_Aperture(0xD101), 
    DeviceProperty_ShutterSpeed(0xD102),
    DeviceProperty_Iso(0xD103), 
    DeviceProperty_ExposureCompensation(0xD104), 
    DeviceProperty_ShootingMode(0xD105), 
    DeviceProperty_DriveMode(0xD106), 
    DeviceProperty_ExpMeterringMode(0xD107), 
    DeviceProperty_AFMode(0xD108), 
    DeviceProperty_WhiteBalance(0xD109), 
    DeviceProperty_PictureStyle(0xD110), 
    DeviceProperty_TransferOption(0xD111), 
    DeviceProperty_UnixTime(0xD113), 
    DeviceProperty_ImageQuality(0xD120), 
    DeviceProperty_LiveView(0xD1B0), 
    DeviceProperty_AvailableShots(0xD11B), 
    DeviceProperty_CaptureDestination(0xD11C), 
    DeviceProperty_BracketMode(0xD11D); 
    
    private int value;

    private EOSConstant(int value) {
        this.value = value;
    }
    
    public static EOSConstant find(int value) {
        for (EOSConstant constant : EOSConstant.values()) {
            if (constant.value == value) {
                return constant;
            }
        }
        return null;
    }
    
    public int getValue() {
        return value;
    }
}
