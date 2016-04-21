package com.capture400d;

public class ByteUtils {
    
    public static byte toByte(int v) {
        return (byte)v;
    }
    
    public static byte[] toByte(int[] ai) {
        byte[] ab = new byte[ai.length];
        for (int i = 0; i<ai.length; i++) {
            ab[i] = (byte)ai[i];
        }
        return ab;
    }
    
    public static byte[] toByte(Object[] ai) {
        byte[] ab = new byte[ai.length];
        for (int i = 0; i<ai.length; i++) {
            ab[i] = ((Integer)ai[i]).byteValue();
        }
        return ab;
    }
    
    public static String toString(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<Math.min(data.length, 80); i++) {
            byte b = data[i];
            sb.append(toString(b));
            if (i<data.length-1) {
                sb.append(":");
            }
        }
        return sb.toString();
    }
    
    public static String toString(byte b) {
        StringBuilder sb = new StringBuilder();
        String str = Integer.toHexString(b);
        if (str.length() < 2) {
            sb.append("0").append(str);
        } else if (str.length() > 2) {
            sb.append(str.substring(str.length()-2));
        } else {
            sb.append(str);
        }
        return sb.toString();
    }
    
    public static int copy(byte[] source, byte[] destination, int source_from, int destination_index) {
    	System.arraycopy(source, source_from, destination, destination_index, source.length-source_from);
        return source.length-source_from;
    }
}
