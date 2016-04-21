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
 * StorageInfo provides information such as the type and capacity of
 * storage media, whether it's removable, and more.
 *
 * @version $Id: StorageInfo.java,v 1.3 2000/09/26 16:16:30 dbrownell Exp $
 * @author David Brownell
 */
public class StorageInfo extends Data
{
    int		storageType;
    int		filesystemType;
    int		accessCapability;
    long	maxCapacity;

    long	freeSpaceInBytes;
    int		freeSpaceInImages;
    String	storageDescription;
    String	volumeLabel;

    StorageInfo () { }

    // StorageInfo (byte buf []) { super (false, buf); }
    // StorageInfo (byte buf [], int len) { super (false, buf, len); }

    void parse ()
    {
	super.parse ();

	storageType = nextU16 ();
	filesystemType = nextU16 ();
	accessCapability = nextU16 ();
	maxCapacity = /* unsigned */ nextS64 ();
	freeSpaceInBytes = /* unsigned */ nextS64 ();
	freeSpaceInImages = /* unsigned */ nextS32 ();
	storageDescription = nextString ();
	volumeLabel = nextString ();
    }

    void line (PrintStream out)
    {
	String	temp;

	switch (storageType) {
	    case 0: temp = "undefined"; break;
	    case 1: temp = "Fixed ROM"; break;
	    case 2: temp = "Removable ROM"; break;
	    case 3: temp = "Fixed RAM"; break;
	    case 4: temp = "Removable RAM"; break;
	    default:
		temp = "Reserved-0x" + Integer.toHexString (storageType);
	}
	out.println ("Storage Type: " + temp);
    }

    void dump (PrintStream out)
    {
	String	temp;

	super.dump (out);
	out.println ("StorageInfo:");
	line (out);

	switch (filesystemType) {
	    case 0: temp = "undefined"; break;
	    case 1: temp = "flat"; break;
	    case 2: temp = "hierarchical"; break;
	    case 3: temp = "dcf"; break;
	    default:
		if ((filesystemType & 0x8000) != 0)
		    temp = "Reserved-0x";
		else
		    temp = "Vendor-0x";
		temp += Integer.toHexString (filesystemType);
	}
	out.println ("Filesystem Type: " + temp);

	// access: rw, ro, or ro "with object deletion"

	if (maxCapacity != ~0)
	    out.println ("Capacity in bytes: " + maxCapacity);
	if (freeSpaceInBytes != ~0)
	    out.println ("Free bytes: " + freeSpaceInBytes);
	if (freeSpaceInImages != ~0)
	    out.println ("Free space in Images: " + freeSpaceInImages);

	if (storageDescription != null)
	    out.println ("Description: " + storageDescription);
	if (volumeLabel != null)
	    out.println ("Volume Label: " + volumeLabel);
    }
}
