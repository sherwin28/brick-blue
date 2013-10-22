package net.isger.brick.blue;

/**
 * 字节向量
 * 
 * @author issing
 * 
 */
public class ByteVector {

    private static final int DEF_CAPACITY = 64;

    private byte[] data;

    private int length;

    public ByteVector() {
        this(DEF_CAPACITY);
    }

    public ByteVector(int size) {
        if (size < DEF_CAPACITY) {
            size = DEF_CAPACITY;
        }
        data = new byte[size];
    }

    /**
     * 输出一个字节
     * 
     * @param b
     * @return
     */
    public ByteVector putByte(final int b) {
        int length = this.length;
        if (length + 1 > data.length) {
            enlarge(1);
        }
        data[length++] = (byte) b;
        this.length = length;
        return this;
    }

    /**
     * 输出一个字
     * 
     * @param s
     * @return
     */
    public ByteVector putShort(final int s) {
        int length = this.length;
        if (length + 2 > data.length) {
            enlarge(2);
        }
        byte[] data = this.data;
        data[length++] = (byte) (s >>> 8);
        data[length++] = (byte) s;
        this.length = length;
        return this;
    }

    /**
     * 输出双字
     * 
     * @param i
     * @return
     */
    public ByteVector putInt(final int i) {
        int length = this.length;
        if (length + 4 > data.length) {
            enlarge(4);
        }
        byte[] data = this.data;
        data[length++] = (byte) (i >>> 24);
        data[length++] = (byte) (i >>> 16);
        data[length++] = (byte) (i >>> 8);
        data[length++] = (byte) i;
        this.length = length;
        return this;
    }

    /**
     * 输出四个字
     * 
     * @param l
     * @return
     */
    public ByteVector putLong(final long l) {
        int length = this.length;
        if (length + 8 > data.length) {
            enlarge(8);
        }
        byte[] data = this.data;
        int i = (int) (l >>> 32);
        data[length++] = (byte) (i >>> 24);
        data[length++] = (byte) (i >>> 16);
        data[length++] = (byte) (i >>> 8);
        data[length++] = (byte) i;
        i = (int) l;
        data[length++] = (byte) (i >>> 24);
        data[length++] = (byte) (i >>> 16);
        data[length++] = (byte) (i >>> 8);
        data[length++] = (byte) i;
        this.length = length;
        return this;
    }

    /**
     * 输出UTF8字节
     * 
     * @param s
     * @return
     */
    public ByteVector putUTF8(final String s) {
        int charLength = s.length();
        int len = length;
        if (len + 2 + charLength > data.length) {
            enlarge(2 + charLength);
        }
        byte[] data = this.data;
        data[len++] = (byte) (charLength >>> 8);
        data[len++] = (byte) charLength;
        for (int i = 0; i < charLength; ++i) {
            char c = s.charAt(i);
            if (c >= '\001' && c <= '\177') {
                data[len++] = (byte) c;
            } else {
                int byteLength = i;
                for (int j = i; j < charLength; ++j) {
                    c = s.charAt(j);
                    if (c >= '\001' && c <= '\177') {
                        byteLength++;
                    } else if (c > '\u07FF') {
                        byteLength += 3;
                    } else {
                        byteLength += 2;
                    }
                }
                data[length] = (byte) (byteLength >>> 8);
                data[length + 1] = (byte) byteLength;
                if (length + 2 + byteLength > data.length) {
                    length = len;
                    enlarge(2 + byteLength);
                    data = this.data;
                }
                for (int j = i; j < charLength; ++j) {
                    c = s.charAt(j);
                    if (c >= '\001' && c <= '\177') {
                        data[len++] = (byte) c;
                    } else if (c > '\u07FF') {
                        data[len++] = (byte) (0xE0 | c >> 12 & 0xF);
                        data[len++] = (byte) (0x80 | c >> 6 & 0x3F);
                        data[len++] = (byte) (0x80 | c & 0x3F);
                    } else {
                        data[len++] = (byte) (0xC0 | c >> 6 & 0x1F);
                        data[len++] = (byte) (0x80 | c & 0x3F);
                    }
                }
                break;
            }
        }
        length = len;
        return this;
    }

    /**
     * 输出字节向量
     * 
     * @param vector
     * @return
     */
    public ByteVector put(final ByteVector vector) {
        this.put(vector.data, 0, vector.length);
        return this;
    }

    /**
     * 输出字节数组
     * 
     * @param b
     * @param off
     * @param len
     * @return
     */
    public ByteVector put(final byte[] b, final int off, final int len) {
        if (length + len > data.length) {
            enlarge(len);
        }
        if (b != null) {
            System.arraycopy(b, off, data, length, len);
        }
        length += len;
        return this;
    }

    /**
     * 从两个参数中各输出一个字节
     * 
     * @param b1
     * @param b2
     * @return
     */
    ByteVector put11(final int b1, final int b2) {
        int length = this.length;
        if (length + 2 > data.length) {
            enlarge(2);
        }
        byte[] data = this.data;
        data[length++] = (byte) b1;
        data[length++] = (byte) b2;
        this.length = length;
        return this;
    }

    /**
     * 从第一个参数中输出一个字节，从第二个参数中输出一个字
     * 
     * @param b
     * @param s
     * @return
     */
    ByteVector put12(final int b, final int s) {
        int length = this.length;
        if (length + 3 > data.length) {
            enlarge(3);
        }
        byte[] data = this.data;
        data[length++] = (byte) b;
        data[length++] = (byte) (s >>> 8);
        data[length++] = (byte) s;
        this.length = length;
        return this;
    }

    /**
     * 从第一个参数中输出一个字节，从第二和三个参数中各输出一个字
     * 
     * @param b
     * @param s1
     * @param s2
     * @return
     */
    ByteVector put122(final int b, final int s1, final int s2) {
        int length = this.length;
        if (length + 5 > data.length) {
            enlarge(5);
        }
        byte[] data = this.data;
        data[length++] = (byte) b;
        data[length++] = (byte) (s1 >>> 8);
        data[length++] = (byte) s1;
        data[length++] = (byte) (s2 >>> 8);
        data[length++] = (byte) s2;
        this.length = length;
        return this;
    }

    /**
     * 转换为字节数组输出
     * 
     * @return
     */
    public byte[] toArray() {
        byte[] array = new byte[this.length];
        System.arraycopy(this.data, 0, array, 0, this.length);
        return array;
    }

    /**
     * 获取保存字节总数
     * 
     * @return
     */
    public int getLength() {
        return this.length;
    }

    /**
     * 放大向量容量空间
     * 
     * @param size
     */
    private void enlarge(final int size) {
        int length1 = 2 * data.length;
        int length2 = length + size;
        byte[] newData = new byte[length1 > length2 ? length1 : length2];
        System.arraycopy(data, 0, newData, 0, length);
        data = newData;
    }

}
