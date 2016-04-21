package usb.core;

import java.io.InputStream;
import java.io.OutputStream;

public class Endpoint {

	public String getType() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isInput() {
		// TODO Auto-generated method stub
		return false;
	}

	public int getMaxPacketSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getEndpoint() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void clearHalt() {
		// TODO Auto-generated method stub
		
	}

	public byte[] recvInterrupt() throws USBException {
		// TODO Auto-generated method stub
		return null;
	}

	public OutputStream getOutputStream() {
		// TODO Auto-generated method stub
		return null;
	}

	public InputStream getInputStream() {
		// TODO Auto-generated method stub
		return null;
	}

}
