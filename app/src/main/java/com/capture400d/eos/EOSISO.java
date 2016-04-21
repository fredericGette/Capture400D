package com.capture400d.eos;

public enum EOSISO {

	iso100(0x48),
	iso200(0x50), 
	iso400(0x58), 
	iso800(0x60), 
	iso1600(0x68);
	
	private int value;
	
	private EOSISO(int value) {
		this.value = value;
	}
	
    public static EOSISO find(int value) {
        for (EOSISO iso : EOSISO.values()) {
            if (iso.value == value) {
                return iso;
            }
        }
        return null;
    }
}
