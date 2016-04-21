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

import java.io.IOException;
import java.io.PrintStream;
import java.net.URLConnection;


/**
 * ObjectInfo provides metadata such as image and thumbnail sizes
 * for image objects; it describes associations; and identifies
 * additional object types such as audio clips.
 *
 * <p> Note that in addition to the image formats identified here,
 * devices could also support proprietary formats except for use
 * with thumbnail images.
 *
 * <p> Subclasses using vendor-specific object (or request) codes
 * should override getCodeName() to handle those codes, calling
 * to this superclass for all other codes.
 *
 * @version $Id: ObjectInfo.java,v 1.10 2000/11/21 19:25:16 dbrownell Exp $
 * @author David Brownell
 */
public class ObjectInfo extends Data
{
    int		storageId;			// 8.1
    int		objectFormatCode;		// 6.2
    int		protectionStatus;		// 0 r/w, 1 r/o
    int		objectCompressedSize;

    int		thumbFormat;			// 6.2
    int		thumbCompressedSize;
    int		thumbPixWidth;
    int		thumbPixHeight;

    int		imagePixWidth;
    int		imagePixHeight;
    int		imageBitDepth;
    int		parentObject;

    int		associationType;		// 6.4
    int		associationDesc;		// 6.4
    int		sequenceNumber;			// (ordered associations)
    String	filename;			// (sans path)

    String	captureDate;			// DateTime string
    String	modificationDate;		// DateTime string
    String	keywords;

    int		handle;

    ObjectInfo (int h) { handle = h; }

    /**
     * Construct an ObjectInfo data packet using the object at the other
     * end of the specified connection.
     *
     * @see BaselineInitiator#sendObjectInfo
     *
     * @exception IllegalArgumentException if the object uses an image
     *	format the device doesn't support, or if the object's content
     *	type is not recognized.
     */
    public ObjectInfo (URLConnection conn, DeviceInfo devInfo)
    {
	super (false, new byte [1024]);

	String		type = conn.getContentType ();

	// mandatory fields:
	//	objectCompressedSize
	//	objectFormatCode
	//	associationType (if it's an association; we aren't)

	objectCompressedSize = conn.getContentLength ();

	// image formats
	if (type.startsWith ("image/")) {
	    boolean	error = false;

	    if ("image/jpeg".equals (type)) {
		if (devInfo.supportsImageFormat (JFIF))
		    objectFormatCode = JFIF;
		// cheat:  JFIF files won't have all EXIF markers ...
		else if (devInfo.supportsImageFormat (EXIF_JPEG))
		    objectFormatCode = EXIF_JPEG;
		else
		    error = true;
	    } else if ("image/tiff".equals (type)) {
		if (devInfo.supportsImageFormat (TIFF))
		    objectFormatCode = TIFF;
		// as above, these cheat ...
		else if (devInfo.supportsImageFormat (TIFF_EP))
		    objectFormatCode = TIFF_EP;
		else if (devInfo.supportsImageFormat (TIFF_IT))
		    objectFormatCode = TIFF_IT;
		else
		    error = true;
	    } else {
		if ("image/gif".equals (type))
		    objectFormatCode = GIF;
		else if ("image/png".equals (type))
		    objectFormatCode = PNG;
		else if ("image/vnd.fpx".equals (type))
		    objectFormatCode = FlashPix;
		else if ("image/x-MS-bmp".equals (type))
		    objectFormatCode = BMP;
		else if ("image/x-photo-cd".equals (type))
		    objectFormatCode = PCD;
		else
		    objectFormatCode = UnknownImage;
	    }
	    if (error || !devInfo.supportsImageFormat (objectFormatCode))
		throw new IllegalArgumentException ("device doesn't support " + type);
	}

	// text formats
	else if ("text/html".equals (type))
	    objectFormatCode = HTML;
	else if ("text/plain".equals (type))
	    objectFormatCode = Text;

	// audio formats
	else if ("audio/mp3".equals (type))
	    objectFormatCode = MP3;
	else if ("audio/x-aiff".equals (type))
	    objectFormatCode = AIFF;
	else if ("audio/x-wav".equals (type))
	    objectFormatCode = WAV;

	// video formats
	else if ("video/mpeg".equals (type))
	    objectFormatCode = MPEG;
	// avi, ...
	

	// we don't recognize this object format
	else
	    objectFormatCode = Undefined;
	
	// optional fields:  everything else!

	// fill body; header is filled by PTP transaction engine
	marshal ();
    }

    private void marshal ()
    {
	offset = HDR_LEN;

	put32 (storageId);
	put16 (objectFormatCode);
	put16 (protectionStatus);
	put32 (objectCompressedSize);

	put16 (thumbFormat);
	put32 (thumbCompressedSize);
	put32 (thumbPixWidth);
	put32 (thumbPixHeight);

	put32 (imagePixWidth);
	put32 (imagePixHeight);
	put32 (imageBitDepth);
	put32 (parentObject);

	put16 (associationType);
	put32 (associationDesc);
	put32 (sequenceNumber);
	putString (filename);

	putString (captureDate);
	putString (modificationDate);
	putString (keywords);

	length = offset;
	offset = 0;

	byte temp [] = new byte [length];

	System.arraycopy (data, 0, temp, 0, length);
	data = temp;
    }

    public int getLength ()
    {
	return isIn () ? super.getLength () : data.length;
    }

    void parse ()
    {
	super.parse ();

	storageId = nextS32 ();
	objectFormatCode = nextU16 ();
	protectionStatus = nextU16 ();
	objectCompressedSize = /* unsigned */ nextS32 ();

	thumbFormat = nextU16 ();
	thumbCompressedSize = /* unsigned */ nextS32 ();
	thumbPixWidth = /* unsigned */ nextS32 ();
	thumbPixHeight = /* unsigned */ nextS32 ();

	imagePixWidth = /* unsigned */ nextS32 ();
	imagePixHeight = /* unsigned */ nextS32 ();
	imageBitDepth = /* unsigned */ nextS32 ();
	parentObject = nextS32 ();

	associationType = nextU16 ();
	associationDesc = nextS32 ();
	sequenceNumber = /* unsigned */ nextS32 ();
	filename = nextString ();

	captureDate = nextString ();
	modificationDate = nextString ();
	keywords = nextString ();
    }

    void line (PrintStream out)
    {
	if (filename != null) {
	    // out.print ("Name: ");
	    out.print (filename);
	    out.print ("; ");
	}
	if (objectFormatCode == Association) {
	    String		associationString;

	    associationString = associationString (associationType);
	    if (associationString != null)
		out.print (associationString);
	} else {
	    out.print (objectCompressedSize);
	    out.print (" bytes, ");
	    out.print (getFormatString (objectFormatCode));
	    if (thumbFormat != 0) {
		out.print (" ");
		out.print (imagePixWidth);
		out.print ("x");
		out.print (imagePixHeight);
		if (imageBitDepth != 0) {
		    out.print (", ");
		    out.print (imageBitDepth);
		    out.print (" bits");
		}
	    }
	}
	out.println ();
    }

    void dump (PrintStream out)
    {
	super.dump (out);
	out.println ("ObjectInfo:");

	if (storageId != 0) {
	    out.print ("StorageID: 0x");
	    out.print (Integer.toHexString (storageId));
	    switch (protectionStatus) {
		case 0:	out.println (", unprotected"); break;
		case 1:	out.println (", read-only"); break;
		default:
		    out.print (", reserved protectionStatus 0x");
		    out.println (Integer.toHexString (protectionStatus));
		    break;
	    }
	}

	if (parentObject != 0)
	    out.println ("Parent: 0x" + Integer.toHexString (parentObject));
	if (filename != null)
	    out.println ("Filename " + filename);
	if (sequenceNumber != 0) {
	    out.print ("Sequence = ");
	    out.print (sequenceNumber);
	}

	// images must have thumbnails, except for sendObjectInfo
	if (thumbFormat != 0) {
	    out.print ("Image format: ");
	    out.print (getFormatString (objectFormatCode));
	    out.print (", size ");
	    out.print (objectCompressedSize);
	    out.print (", width ");
	    out.print (imagePixWidth);
	    out.print (", height ");
	    out.print (imagePixHeight);
	    out.print (", depth ");
	    out.println (imageBitDepth);

	    out.print ("Thumbnail format: ");
	    out.print (getFormatString (thumbFormat));
	    out.print (", size ");
	    out.print (thumbCompressedSize);
	    out.print (", width ");
	    out.print (thumbPixWidth);
	    out.print (", height ");
	    out.print (thumbPixHeight);
	    out.print (", depth ");
	    out.println (imageBitDepth);
	} else {
	    out.print ("Object format: ");
	    out.print (getFormatString (objectFormatCode));
	    out.print (", size ");
	    out.println (objectCompressedSize);

	    if (objectFormatCode == Association) {
		String		associationString;

		associationString = associationString (associationType);
		if (associationString != null) {
		    out.print ("Association type: ");
		    out.print (associationString);
		    if (associationDesc != 0) {
			// for albums:  reserved for future use
			// for time sequences:  playback delta (millisec)
			// for 2DPanorama:  row count
			out.print (", desc 0x");
			out.print (Integer.toHexString (associationDesc));
		    }
		    out.println ();
		}
	    }
	}

	if (captureDate != null)
	    out.println ("capture date: " + captureDate);
	if (modificationDate != null)
	    out.println ("modification date: " + modificationDate);
	if (keywords != null)
	    out.println ("keywords: " + keywords);
    }


    /** ObjectFormatCode:  unrecognized non-image format */
    public static final int Undefined = 0x3000;
    /** ObjectFormatCode:  associations include folders and panoramas */
    public static final int Association = 0x3001;
    /** ObjectFormatCode: */
    public static final int Script = 0x3002;
    /** ObjectFormatCode: */
    public static final int Executable = 0x3003;

    /** ObjectFormatCode: */
    public static final int Text = 0x3004;
    /** ObjectFormatCode: */
    public static final int HTML = 0x3005;
    /** ObjectFormatCode: */
    public static final int DPOF = 0x3006;
    /** ObjectFormatCode: */
    public static final int AIFF = 0x3007;

    /** ObjectFormatCode: */
    public static final int WAV = 0x3008;
    /** ObjectFormatCode: */
    public static final int MP3 = 0x3009;
    /** ObjectFormatCode: */
    public static final int AVI = 0x300a;
    /** ObjectFormatCode: */
    public static final int MPEG = 0x300b;

    /** ObjectFormatCode: */
    public static final int ASF = 0x300c;

    /** ImageFormatCode: unknown image format */
    public static final int UnknownImage = 0x3800;
    /**
     * ImageFormatCode: EXIF/JPEG version 2.1, the preferred format
     * for thumbnails and for images.
     */
    public static final int EXIF_JPEG = 0x3801;
    /**
     * ImageFormatCode: Uncompressed TIFF/EP, the alternate format
     * for thumbnails.
     */
    public static final int TIFF_EP = 0x3802;
    /** ImageFormatCode: FlashPix image format */
    public static final int FlashPix = 0x3803;

    /** ImageFormatCode: MS-Windows bitmap image format */
    public static final int BMP = 0x3804;
    /** ImageFormatCode: Canon camera image file format */
    public static final int CIFF = 0x3805;
    // 3806 is reserved
    /** ImageFormatCode: Graphics Interchange Format (deprecated) */
    public static final int GIF = 0x3807;

    /** ImageFormatCode: JPEG File Interchange Format */
    public static final int JFIF = 0x3808;
    /** ImageFormatCode: PhotoCD Image Pac*/
    public static final int PCD = 0x3809;
    /** ImageFormatCode: Quickdraw image format */
    public static final int PICT = 0x380a;
    /** ImageFormatCode: Portable Network Graphics */
    public static final int PNG = 0x380b;

    /** ImageFormatCode: Tag Image File Format */
    public static final int TIFF = 0x380d;
    /** ImageFormatCode: TIFF for Information Technology (graphic arts) */
    public static final int TIFF_IT = 0x380e;
    /** ImageFormatCode: JPEG 2000 baseline */
    public static final int JP2 = 0x380f;

    /** ImageFormatCode: JPEG 2000 extended */
    public static final int JPX = 0x3810;


    public boolean isImage ()
    {
	return (objectFormatCode & 0xf800) == 0x3800;
    }


    public String getCodeName (int code)
    {
	return getFormatString (code);
    }

    /**
     * Maps standard format codes to string names.
     * @param code format code
     * @return interned string identifying that object format.
     */
    public static String getFormatString (int code)
    {
	switch (code) {
	    case Undefined:	return "UnknownFormat";
	    case Association:	return "Association";
	    case Script:	return "Script";
	    case Executable:	return "Executable";
	    case Text:		return "Text";
	    case HTML:		return "HTML";
	    case DPOF:		return "DPOF";
	    case AIFF:		return "AIFF";
	    case WAV:		return "WAV";
	    case MP3:		return "MP3";
	    case AVI:		return "AVI";
	    case MPEG:		return "MPEG";
	    case ASF:		return "ASF";
	    case UnknownImage:	return "UnknownImage";
	    case EXIF_JPEG:	return "EXIF/JPEG";
	    case TIFF_EP:	return "TIFF/EP";
	    case FlashPix:	return "FlashPix";
	    case BMP:		return "BMP";
	    case CIFF:		return "CIFF";
	    case GIF:		return "GIF";
	    case JFIF:		return "JFIF";
	    case PCD:		return "PCD";
	    case PICT:		return "PICT";
	    case PNG:		return "PNG";
	    case TIFF:		return "TIFF";
	    case TIFF_IT:	return "TIFF/IT";
	    case JP2:		return "JP2";
	    case JPX:		return "JPX";
	}
	return Container.getCodeString (code);
    }

    // vendor subclasses can just handle the mappings they know,
    // and defer to superclasses
    String associationString (int associationType)
    {
	switch (associationType) {
	    case 0: return null;
	    case 1: return "GenericFolder";
	    case 2: return "Album";
	    case 3: return "TimeSequence";
	    case 4: return "HorizontalPanorama";
	    case 5: return "VerticalPanorama";
	    case 6: return "2DPanorama";
	    case 7: return "AncillaryData";
	    default:
		StringBuffer	retval;
		if ((associationType & 0x8000) == 0)
		    retval = new StringBuffer ("Reserved-0x");
		else
		    retval = new StringBuffer ("Vendor-0x");
		retval.append (Integer.toHexString (associationType));
		return retval.toString ();
	}
    }
}

