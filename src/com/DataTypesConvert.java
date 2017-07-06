

package com;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class DataTypesConvert {

	public final static long getLong(byte[] buf, boolean asc) {
	    if (buf == null) {
	      throw new IllegalArgumentException("byte array is null!");
	    }
	    if (buf.length > 8) {
	      throw new IllegalArgumentException("byte array size > 8 !");
	    }
	    long r = 0;
	    if (asc)
	      for (int i = buf.length - 1; i >= 0; i--) {
	        r <<= 8;
	        r |= (buf[i] & 0x00000000000000ff);
	      }
	    else
	      for (int i = 0; i < buf.length; i++) {
	        r <<= 8;
	        r |= (buf[i] & 0x00000000000000ff);
	      }
	    return r;
	  }
	
	public final static int getInt(byte[] buf, boolean asc) {
	    if (buf == null) {
	      throw new IllegalArgumentException("byte array is null!");
	    }
	    if (buf.length > 4) {
	      throw new IllegalArgumentException("byte array size > 4 !");
	    }
	    int r = 0;
	    if (asc)
	      for (int i = buf.length - 1; i >= 0; i--) {
	        r <<= 8;
	        r |= (buf[i] & 0x000000ff);
	      }
	    else
	      for (int i = 0; i < buf.length; i++) {
	        r <<= 8;
	        r |= (buf[i] & 0x000000ff);
	      }
	    return r;
	  }
	
	 public final static short getShort(byte[] buf, boolean asc) {
		    if (buf == null) {
		      throw new IllegalArgumentException("byte array is null!");
		    }
		    if (buf.length > 2) {
		      throw new IllegalArgumentException("byte array size > 2 !");
		    }
		    short r = 0;
		    if (asc)
		      for (int i = buf.length - 1; i >= 0; i--) {
		        r <<= 8;
		        r |= (buf[i] & 0x00ff);
		      }
		    else
		      for (int i = 0; i < buf.length; i++) {
		        r <<= 8;
		        r |= (buf[i] & 0x00ff);
		      }
		    return r;
		  }
	 
	 public static final String getString(byte[] bArray) {
		  if (bArray == null) {
		      throw new IllegalArgumentException("byte array is null!");
		    }
			String sTemp;
			int j=0;
			for (int i = 0; i < bArray.length; i++) {
				byte by =  bArray[i];
				//int xx = 0xFF & bArray[i];
				sTemp = Integer.toHexString(0xFF & bArray[i]);
				if(by==0){break;}
				if (sTemp.length() > 1){
					j++;
				}
			}
			byte[] buf = new byte[j];
			for (int i = 0; i < j; i++) {
				buf[i] = bArray[i];
			}
			return new String(buf);
		}
	
	
	
	public static final String bytesToHexString(byte[] bArray) {
		StringBuilder sb = new StringBuilder(bArray.length);
		String sTemp;
		for (int i = 0; i < bArray.length; i++) {
			sTemp = Integer.toHexString(0xFF & bArray[i]);
			if (sTemp.length() < 2){
				sb.append(0);
			}
			sb.append(sTemp.toUpperCase());
		}
		return sb.toString();
	}

	public static final Object bytesToObject(byte[] bytes) throws Exception {
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		ObjectInputStream oi = new ObjectInputStream(in);
		Object o = oi.readObject();
		oi.close();
		return o;
	}

	public static int changeByteToInt(byte[] data) {
		return changeByteToInt(data, 0, data.length - 1);
	}

	public static int changeByteToInt(byte[] data, int start, int end) {
		int result = 0;
		for (int i = start; i <= end; i++) {
			result = result << 8;
			result = result | (data[i] & 0xff);
		}
		return result;
	}

	public static long changeByteToLong(byte[] data, int startNum, int endNum,
			int sortType) {
		long TNumber = 0;
		if (sortType == 1)
			for (int i = startNum; i <= endNum; i++) {
				TNumber = TNumber << 8;
				TNumber = TNumber | (data[i] & 0xff);
			}
		else
			for (int i = endNum; i >= startNum; i--) {
				TNumber = TNumber << 8;
				TNumber = TNumber | (data[i] & 0xff);
			}
		return TNumber;
	}

	public static long changeBCDByteToLong(byte[] data, int startNum,
			int endNum, int sortType) {
		long TNumber = 0;
		if (sortType == 1)
			for (int i = startNum; i <= endNum; i++) {
				byte tmpByte = data[i];
				int ten = (tmpByte & 0xF0) >>> 4;
				int dec = tmpByte & 0x0f;
				int tmp = ten * 10 + dec;
				TNumber = TNumber + tmp * (long) Math.pow(100, (endNum - i));
			}
		else
			for (int i = endNum; i >= startNum; i--) {
				byte tmpByte = data[i];
				int ten = (tmpByte & 0xF0) >>> 4;
				int dec = tmpByte & 0x0f;
				int tmp = ten * 10 + dec;
				TNumber = TNumber + tmp * (long) Math.pow(100, (i - startNum));
			}
		return TNumber;
	}

	public static byte[] changeHexStrTobytes(String[] str) {
		byte[] data = new byte[str.length];
		for (int i = 0; i < str.length; i++) {
			String hexStr = "0x" + str[i];
			data[i] = Byte.decode(hexStr).byteValue();
			System.out.println(data[i]);
		}
		return data;
	}

	public static byte[] changeIntToByte(int num, int len) {
		return changeLongToByte(num, len, 1);
	}

	public static byte[] changeLongToByte(long num, int len) {
		return changeLongToByte(num, len, 1);
	}

	public static byte[] changeLongToByte(long num, int len, int sortType) {
		byte[] data = new byte[len];
		if (sortType == 1) {
			for (int i = 0; i < len; i++) {
				data[i] = (byte) ((num >> (8 * (len - 1 - i))) & 0xff);
			}
		} else {
			for (int i = len - 1; i >= 0; i--) {
				data[i] = (byte) ((num >> (8 * i)) & 0xff);
			}
		}
		return data;
	}

	// floatתbyte[]
	public static byte[] floatToByte(float v) {
		ByteBuffer bb = ByteBuffer.allocate(4);
		byte[] ret = new byte[4];
		FloatBuffer fb = bb.asFloatBuffer();
		fb.put(v);
		bb.get(ret);
		return ret;
	}

	// byte[]תfloat
	public static float byteToFloat(byte[] v) {
		ByteBuffer bb = ByteBuffer.wrap(v);
		FloatBuffer fb = bb.asFloatBuffer();
		return fb.get();
	}

	public static boolean checkSum(byte[] b) {
		int sum = 0;
		for (int i = 4; i < b.length - 2; i++) {
			sum = sum + b[i];
		}
		int res = sum / 256;
		sum = sum - res * 256;
		if (b.length < 2) {
			return false;
		}
		if ((byte) sum == b[b.length - 2]) {
			return true;
		} else {
			return false;
		}
	}

	public static byte getCheckSum(byte[] b) {
		if (b == null)
			return 0;
		int sum = 0;
		for (int i = 4; i < b.length - 2; i++) {
			sum = sum + b[i];
		}
		return (byte) sum;
	}

	public static int byte2int(byte b[]) {
		return b[3] & 0xff | (b[2] & 0xff) << 8 | (b[1] & 0xff) << 16
				| (b[0] & 0xff) << 24;
	}

	public static byte[] int2byte(int n) {
		byte buf[] = new byte[4];
		buf[0] = (byte) ((n >> 24) & 0xff);
		buf[1] = (byte) ((n >> 16) & 0xff);
		buf[2] = (byte) ((n >> 8) & 0xff);
		buf[3] = (byte) (n & 0xff);
		return buf;
	}

	public static byte[] short2byte(short n) {
		byte buf[] = new byte[2];
		buf[0] = (byte) (n >> 8);
		buf[1] = (byte) n;
		return buf;
	}

	public static short byte2short(byte buf[]) {
		return (short) ((buf[1] & 0xff) | (buf[0] & 0xff) << 8);
	}

	public static long bytes2long(byte[] b) {
		int mask = 0xff;
		int temp = 0;
		int res = 0;
		for (int i = 0; i < 8; i++) {
			res <<= 8;
			temp = b[i] & mask;
			res |= temp;
		}
		return res;
	}

	public static byte[] long2bytes(long num) {
		byte[] b = new byte[8];
		for (int i = 0; i < 8; i++) {
			b[i] = (byte) (num >>> (56 - i * 8));
		}
		return b;
	}
	
	/**
     * 把IP地址转化为字节数组
     * @param ipAddr
     * @return byte[]
     */
    public static byte[] ipToBytesByInet(String ipAddr) {
        try {
            return InetAddress.getByName(ipAddr).getAddress();
        } catch (Exception e) {
            throw new IllegalArgumentException(ipAddr + " is invalid IP");
        }
    }
	
	/**
     * 根据位运算把 byte[] -> int
     * @param bytes
     * @return int
     */
    public static int bytesToInt(byte[] bytes) {
        int addr = bytes[3] & 0xFF;
        addr |= ((bytes[2] << 8) & 0xFF00);
        addr |= ((bytes[1] << 16) & 0xFF0000);
        addr |= ((bytes[0] << 24) & 0xFF000000);
        return addr;
    }
    /**
     * 把IP地址转化为int
     * @param ipAddr
     * @return int
     */
    public static int ipToInt(String ipAddr) {
        try {
            return bytesToInt(ipToBytesByInet(ipAddr));
        } catch (Exception e) {
            throw new IllegalArgumentException(ipAddr + " is invalid IP");
        }
    }
    private final static int INADDRSZ = 4;
    /**
     * ipInt -> byte[]
     * @param ipInt
     * @return byte[]
     */
    public static byte[] intToBytes(int ipInt) {
        byte[] ipAddr = new byte[INADDRSZ];
        ipAddr[0] = (byte) ((ipInt >>> 24) & 0xFF);
        ipAddr[1] = (byte) ((ipInt >>> 16) & 0xFF);
        ipAddr[2] = (byte) ((ipInt >>> 8) & 0xFF);
        ipAddr[3] = (byte) (ipInt & 0xFF);
        return ipAddr;
    }

    /**
     * 字节数组转化为IP
     * @param bytes
     * @return int
     */
    public static String bytesToIp(byte[] bytes) {
        return new StringBuffer().append(bytes[0] & 0xFF).append('.').append(
                bytes[1] & 0xFF).append('.').append(bytes[2] & 0xFF)
                .append('.').append(bytes[3] & 0xFF).toString();
    }
    /**
     * 把int->ip地址
     * @param ipInt
     * @return String
     */
    public static String intToIp(int ipInt) {
        return new StringBuilder().append(((ipInt >> 24) & 0xff)).append('.')
                .append((ipInt >> 16) & 0xff).append('.').append(
                        (ipInt >> 8) & 0xff).append('.').append((ipInt & 0xff))
                .toString();
    }
}
