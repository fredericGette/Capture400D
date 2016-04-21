// Copyright 2000 by David Brownell <dbrownell@users.sourceforge.net>
//
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
// 
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
//

package usb.jphoto;

import java.io.PrintStream;


/**
 * Command messages start PTP transactions, and are sent from
 * initiator to responder.  They include an operation code,
 * either conform to chapter 10 of the PTP specification or
 * are vendor-specific commands.
 *
 * <p> Create these objects in helper routines which package
 * intelligence about a given Operation.  That is, it'll know
 * the command code, how many command and response parameters
 * may be used, particularly significant response code, and
 * whether the transaction has a data phase (and its direction). 
 *
 * <p> Note that subclasses adding vendor-specific operation codes
 * should override getCodeName() to handle those codes, calling
 * to this superclass for all other codes.  Those would typically
 * be used in {@link BaselineInitiator#transact} invocations.
 *
 * @version $Id: Command.java,v 1.2 2000/09/24 15:48:05 dbrownell Exp $
 * @author David Brownell
 */
public class Command extends ParamVector
{
	public Command (byte buf []) {
		super(buf);
	}
	
    private Command (int nparams, int code, Session s)
    {
	super (new byte [HDR_LEN + (4 * nparams)]);
	putHeader (data.length, 1 /*OperationCode*/, code, s.getNextXID ());
    }

    /**
     * This creates a zero-parameter command.
     * @param code as defined in section 10, table 18
     * @param s session this command is associated with
     */
    public Command (int code, Session s)
    {
	this (0, code, s);
    }

    /**
     * This creates a one-parameter command.
     * @param code as defined in section 10, table 18
     * @param s session this command is associated with
     * @param param1 first operation parameter
     */
    public Command (int code, Session s, int param1)
    {
	this (1, code, s);
	put32 (param1);
    }

    /**
     * This creates a two-parameter command.
     * @param code as defined in section 10, table 18
     * @param s session this command is associated with
     * @param param1 first operation parameter
     * @param param2 second operation parameter
     */
    public Command (int code, Session s, int param1, int param2)
    {
	this (2, code, s);
	put32 (param1);
	put32 (param2);
    }

    /**
     * This creates a three-parameter command.
     * @param code as defined in section 10, table 18
     * @param s session this command is associated with
     * @param param1 first operation parameter
     * @param param2 second operation parameter
     * @param param3 third operation parameter
     */
    public Command (int code, Session s, int param1, int param2, int param3)
    {
	this (3, code, s);
	put32 (param1);
	put32 (param2);
	put32 (param3);
    }
    
    public Command (int code, Session s, long param1, int param2)
    {
	this (3, code, s);
	put64 (param1);
	put32 (param2);
    }
    
    public Command (int code, Session s, int param1, int param2, int param3, int param4)
    {
	this (4, code, s);
	put32 (param1);
	put32 (param2);
	put32 (param3);
	put32 (param4);
    }

    // allegedly some commands could have up to five params


    /** OperationCode: */
    public static final int GetDeviceInfo = 0x1001;
    /** OperationCode: */
    public static final int OpenSession = 0x1002;
    /** OperationCode: */
    public static final int CloseSession = 0x1003;

    /** OperationCode: */
    public static final int GetStorageIDs = 0x1004;
    /** OperationCode: */
    public static final int GetStorageInfo = 0x1005;
    /** OperationCode: */
    public static final int GetNumObjects = 0x1006;
    /** OperationCode: */
    public static final int GetObjectHandles = 0x1007;

    /** OperationCode: */
    public static final int GetObjectInfo = 0x1008;
    /** OperationCode: */
    public static final int GetObject = 0x1009;
    /** OperationCode: */
    public static final int GetThumb = 0x100a;
    /** OperationCode: */
    public static final int DeleteObject = 0x100b;

    /** OperationCode: */
    public static final int SendObjectInfo = 0x100c;
    /** OperationCode: */
    public static final int SendObject = 0x100d;
    /** OperationCode: */
    public static final int InitiateCapture = 0x100e;
    /** OperationCode: */
    public static final int FormatStore = 0x100f;

    /** OperationCode: */
    public static final int ResetDevice = 0x1010;
    /** OperationCode: */
    public static final int SelfTest = 0x1011;
    /** OperationCode: */
    public static final int SetObjectProtection = 0x1012;
    /** OperationCode: */
    public static final int PowerDown = 0x1013;

    /** OperationCode: */
    public static final int GetDevicePropDesc = 0x1014;
    /** OperationCode: */
    public static final int GetDevicePropValue = 0x1015;
    /** OperationCode: */
    public static final int SetDevicePropValue = 0x1016;
    /** OperationCode: */
    public static final int ResetDevicePropValue = 0x1017;

    /** OperationCode: */
    public static final int TerminateOpenCapture = 0x1018;
    /** OperationCode: */
    public static final int MoveObject = 0x1019;
    /** OperationCode: */
    public static final int CopyObject = 0x101a;
    /** OperationCode: */
    public static final int GetPartialObject = 0x101b;

    /** OperationCode: */
    public static final int InitiateOpenCapture = 0x101c;


    public String getCodeName (int code)
    {
	return getOpcodeString (code);
    }

    /**
     * Maps standard operation codes to string names.
     * @param code operation code
     * @return interned string identifying that operation.
     */
    public static String getOpcodeString (int code)
    {
	switch (code) {
	    case GetDeviceInfo:		return "GetDeviceInfo";
	    case OpenSession:		return "OpenSession";
	    case CloseSession:		return "CloseSession";
	       
	    case GetStorageIDs:		return "GetStorageIDs";
	    case GetStorageInfo:	return "GetStorageInfo";
	    case GetNumObjects:		return "GetNumObjects";
	    case GetObjectHandles:	return "GetObjectIDs";
	       
	    case GetObjectInfo:		return "GetObjectInfo";
	    case GetObject:		return "GetObject";
	    case GetThumb:		return "GetThumb";
	    case DeleteObject:		return "DeleteObject";
	       
	    case SendObjectInfo:	return "SendObjectInfo";
	    case SendObject:		return "SendObject";
	    case InitiateCapture:	return "InitiateCapture";
	    case FormatStore:		return "FormatStore";
	       
	    case ResetDevice:		return "ResetDevice";
	    case SelfTest:		return "SelfTest";
	    case SetObjectProtection:	return "SetObjectProtection";
	    case PowerDown:		return "PowerDown";
	       
	    case GetDevicePropDesc:	return "GetDevicePropDesc";
	    case GetDevicePropValue:	return "GetDevicePropValue";
	    case SetDevicePropValue:	return "SetDevicePropValue";
	    case ResetDevicePropValue:	return "ResetDevicePropValue";
	       
	    case TerminateOpenCapture:	return "TerminateOpenCapture";
	    case MoveObject:		return "MoveObject";
	    case CopyObject:		return "CopyObject";
	    case GetPartialObject:	return "GetPartialObject";
	       
	    case InitiateOpenCapture:	return "InitiateOpenCapture";
	}
	return Container.getCodeString (code);
    }
}
