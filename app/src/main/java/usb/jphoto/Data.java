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

import com.capture400d.ByteUtils;


/**
 * The optional middle phase of a PTP transaction involves sending
 * data to or from the responder.
 *
 * <p> Note that subclasses used with vendor-specific operation  codes
 * should override getCodeName() to recognize those codes, calling to
 * this superclass for all other codes.
 *
 * @version $Id: Data.java,v 1.3 2000/10/04 05:49:52 dbrownell Exp $
 * @author David Brownell
 */
public class Data extends Container
{
    private boolean	in;

    public Data(byte buf []) {
    	super(buf);
    }
    
    Data () { this (true, null, 0); }

    public Data (boolean isIn, byte buf [])
	{ super (buf); in = isIn; }

    Data (boolean isIn, byte buf [], int len)
	{ super (buf, len); in = isIn; }

    boolean isIn ()
	{ return in; }

    public String getCodeName (int code)
    {
	return Command.getOpcodeString (code);
    }

    public String toString ()
    {
	StringBuffer	temp = new StringBuffer ();
	int		code = getCode ();

	temp.append ("{ ");
	temp.append (getBlockTypeName (getBlockType ()));
	if (in)
	    temp.append (" IN");
	else
	    temp.append (" OUT");
	temp.append ("; len ");
	temp.append (Integer.toString (getLength ()));
	temp.append ("; ");
	temp.append (getCodeName (code));
	temp.append ("; xid ");
	temp.append (Integer.toString (getXID ()));
	temp.append ("}");
	temp.append (ByteUtils.toString(data));
	return temp.toString ();
    }
}

