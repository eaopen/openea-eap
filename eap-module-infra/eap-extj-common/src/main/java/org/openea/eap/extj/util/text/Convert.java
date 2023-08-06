package org.openea.eap.extj.util.text;

import org.openea.eap.extj.util.StringUtil;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.text.NumberFormat;
import java.util.Set;

public class Convert {
    public Convert() {
    }

    public static String toStr(Object value, String defaultValue) {
        if (null == value) {
            return defaultValue;
        } else {
            return value instanceof String ? (String)value : value.toString();
        }
    }

    public static String toStr(Object value) {
        return toStr(value, (String)null);
    }

    public static Character toChar(Object value, Character defaultValue) {
        if (null == value) {
            return defaultValue;
        } else if (value instanceof Character) {
            return (Character)value;
        } else {
            String valueStr = toStr(value, (String)null);
            return StringUtil.isEmpty(valueStr) ? defaultValue : valueStr.charAt(0);
        }
    }

    public static Character toChar(Object value) {
        return toChar(value, (Character)null);
    }

    public static Byte toByte(Object value, Byte defaultValue) {
        if (value == null) {
            return defaultValue;
        } else if (value instanceof Byte) {
            return (Byte)value;
        } else if (value instanceof Number) {
            return ((Number)value).byteValue();
        } else {
            String valueStr = toStr(value, (String)null);
            if (StringUtil.isEmpty(valueStr)) {
                return defaultValue;
            } else {
                try {
                    return Byte.parseByte(valueStr);
                } catch (Exception var4) {
                    return defaultValue;
                }
            }
        }
    }

    public static Byte toByte(Object value) {
        return toByte(value, (Byte)null);
    }

    public static Short toShort(Object value, Short defaultValue) {
        if (value == null) {
            return defaultValue;
        } else if (value instanceof Short) {
            return (Short)value;
        } else if (value instanceof Number) {
            return ((Number)value).shortValue();
        } else {
            String valueStr = toStr(value, (String)null);
            if (StringUtil.isEmpty(valueStr)) {
                return defaultValue;
            } else {
                try {
                    return Short.parseShort(valueStr.trim());
                } catch (Exception var4) {
                    return defaultValue;
                }
            }
        }
    }

    public static Short toShort(Object value) {
        return toShort(value, (Short)null);
    }

    public static Number toNumber(Object value, Number defaultValue) {
        if (value == null) {
            return defaultValue;
        } else if (value instanceof Number) {
            return (Number)value;
        } else {
            String valueStr = toStr(value, (String)null);
            if (StringUtil.isEmpty(valueStr)) {
                return defaultValue;
            } else {
                try {
                    return NumberFormat.getInstance().parse(valueStr);
                } catch (Exception var4) {
                    return defaultValue;
                }
            }
        }
    }

    public static Number toNumber(Object value) {
        return toNumber(value, (Number)null);
    }

    public static Integer toInt(Object value, Integer defaultValue) {
        if (value == null) {
            return defaultValue;
        } else if (value instanceof Integer) {
            return (Integer)value;
        } else if (value instanceof Number) {
            return ((Number)value).intValue();
        } else {
            String valueStr = toStr(value, (String)null);
            if (StringUtil.isEmpty(valueStr)) {
                return defaultValue;
            } else {
                try {
                    return Integer.parseInt(valueStr.trim());
                } catch (Exception var4) {
                    return defaultValue;
                }
            }
        }
    }

    public static Integer toInt(Object value) {
        return toInt(value, (Integer)null);
    }

    public static Integer[] toIntArray(String str) {
        return toIntArray(",", str);
    }

    public static Long[] toLongArray(String str) {
        return toLongArray(",", str);
    }

    public static Integer[] toIntArray(String split, String str) {
        if (StringUtil.isEmpty(str)) {
            return new Integer[0];
        } else {
            String[] arr = str.split(split);
            Integer[] ints = new Integer[arr.length];

            for(int i = 0; i < arr.length; ++i) {
                Integer v = toInt(arr[i], 0);
                ints[i] = v;
            }

            return ints;
        }
    }

    public static Long[] toLongArray(String split, String str) {
        if (StringUtil.isEmpty(str)) {
            return new Long[0];
        } else {
            String[] arr = str.split(split);
            Long[] longs = new Long[arr.length];

            for(int i = 0; i < arr.length; ++i) {
                Long v = toLong(arr[i], (Long)null);
                longs[i] = v;
            }

            return longs;
        }
    }

    public static String[] toStrArray(String str) {
        return toStrArray(",", str);
    }

    public static String[] toStrArray(String split, String str) {
        return str.split(split);
    }

    public static Long toLong(Object value, Long defaultValue) {
        if (value == null) {
            return defaultValue;
        } else if (value instanceof Long) {
            return (Long)value;
        } else if (value instanceof Number) {
            return ((Number)value).longValue();
        } else {
            String valueStr = toStr(value, (String)null);
            if (StringUtil.isEmpty(valueStr)) {
                return defaultValue;
            } else {
                try {
                    return (new BigDecimal(valueStr.trim())).longValue();
                } catch (Exception var4) {
                    return defaultValue;
                }
            }
        }
    }

    public static Long toLong(Object value) {
        return toLong(value, (Long)null);
    }

    public static Double toDouble(Object value, Double defaultValue) {
        if (value == null) {
            return defaultValue;
        } else if (value instanceof Double) {
            return (Double)value;
        } else if (value instanceof Number) {
            return ((Number)value).doubleValue();
        } else {
            String valueStr = toStr(value, (String)null);
            if (StringUtil.isEmpty(valueStr)) {
                return defaultValue;
            } else {
                try {
                    return (new BigDecimal(valueStr.trim())).doubleValue();
                } catch (Exception var4) {
                    return defaultValue;
                }
            }
        }
    }

    public static Double toDouble(Object value) {
        return toDouble(value, (Double)null);
    }

    public static Float toFloat(Object value, Float defaultValue) {
        if (value == null) {
            return defaultValue;
        } else if (value instanceof Float) {
            return (Float)value;
        } else if (value instanceof Number) {
            return ((Number)value).floatValue();
        } else {
            String valueStr = toStr(value, (String)null);
            if (StringUtil.isEmpty(valueStr)) {
                return defaultValue;
            } else {
                try {
                    return Float.parseFloat(valueStr.trim());
                } catch (Exception var4) {
                    return defaultValue;
                }
            }
        }
    }

    public static Float toFloat(Object value) {
        return toFloat(value, (Float)null);
    }

    public static Boolean toBool(Object value, Boolean defaultValue) {
        if (value == null) {
            return defaultValue;
        } else if (value instanceof Boolean) {
            return (Boolean)value;
        } else if (StringUtil.isEmpty(String.valueOf(value))) {
            return defaultValue;
        } else {
            switch (String.valueOf(value).trim().toLowerCase()) {
                case "true":
                    return true;
                case "false":
                    return false;
                case "yes":
                    return true;
                case "ok":
                    return true;
                case "no":
                    return false;
                case "1":
                    return true;
                case "0":
                    return false;
                default:
                    return defaultValue;
            }
        }
    }

    public static Boolean toBool(Object value) {
        return toBool(value, (Boolean)null);
    }

    public static <E extends Enum<E>> E toEnum(Class<E> clazz, Object value, E defaultValue) {
        if (value == null) {
            return defaultValue;
        } else if (clazz.isAssignableFrom(value.getClass())) {
            E myE = (E)value;
            return myE;
        } else {
            String valueStr = toStr(value, (String)null);
            if (StringUtil.isEmpty(valueStr)) {
                return defaultValue;
            } else {
                try {
                    return Enum.valueOf(clazz, valueStr);
                } catch (Exception var5) {
                    return defaultValue;
                }
            }
        }
    }

    public static <E extends Enum<E>> E toEnum(Class<E> clazz, Object value) {
        return toEnum(clazz, value, (E)null);
    }

    public static BigInteger toBigInteger(Object value, BigInteger defaultValue) {
        if (value == null) {
            return defaultValue;
        } else if (value instanceof BigInteger) {
            return (BigInteger)value;
        } else if (value instanceof Long) {
            return BigInteger.valueOf((Long)value);
        } else {
            String valueStr = toStr(value, (String)null);
            if (StringUtil.isEmpty(valueStr)) {
                return defaultValue;
            } else {
                try {
                    return new BigInteger(valueStr);
                } catch (Exception var4) {
                    return defaultValue;
                }
            }
        }
    }

    public static BigInteger toBigInteger(Object value) {
        return toBigInteger(value, (BigInteger)null);
    }

    public static BigDecimal toBigDecimal(Object value, BigDecimal defaultValue) {
        if (value == null) {
            return defaultValue;
        } else if (value instanceof BigDecimal) {
            return (BigDecimal)value;
        } else if (value instanceof Long) {
            return new BigDecimal((Long)value);
        } else if (value instanceof Double) {
            return new BigDecimal((Double)value);
        } else if (value instanceof Integer) {
            return new BigDecimal((Integer)value);
        } else {
            String valueStr = toStr(value, (String)null);
            if (StringUtil.isEmpty(valueStr)) {
                return defaultValue;
            } else {
                try {
                    return new BigDecimal(valueStr);
                } catch (Exception var4) {
                    return defaultValue;
                }
            }
        }
    }

    public static BigDecimal toBigDecimal(Object value) {
        return toBigDecimal(value, (BigDecimal)null);
    }

    public static String utf8Str(Object obj) {
        return str(obj, CharsetKit.CHARSET_UTF_8);
    }

    public static String str(Object obj, String charsetName) {
        return str(obj, Charset.forName(charsetName));
    }

    public static String str(Object obj, Charset charset) {
        if (null == obj) {
            return null;
        } else if (obj instanceof String) {
            return (String)obj;
        } else if (!(obj instanceof byte[]) && !(obj instanceof Byte[])) {
            return obj instanceof ByteBuffer ? str((ByteBuffer)obj, charset) : obj.toString();
        } else {
            return str((Object)((Byte[])((Byte[])obj)), (Charset)charset);
        }
    }

    public static String str(byte[] bytes, String charset) {
        return str(bytes, StringUtil.isEmpty(charset) ? Charset.defaultCharset() : Charset.forName(charset));
    }

    public static String str(byte[] data, Charset charset) {
        if (data == null) {
            return null;
        } else {
            return null == charset ? new String(data) : new String(data, charset);
        }
    }

    public static String str(ByteBuffer data, String charset) {
        return data == null ? null : str(data, Charset.forName(charset));
    }

    public static String str(ByteBuffer data, Charset charset) {
        if (null == charset) {
            charset = Charset.defaultCharset();
        }

        return charset.decode(data).toString();
    }

    public static String toSbc(String input) {
        return toSbc(input, (Set)null);
    }

    public static String toSbc(String input, Set<Character> notConvertSet) {
        char[] c = input.toCharArray();

        for(int i = 0; i < c.length; ++i) {
            if (null == notConvertSet || !notConvertSet.contains(c[i])) {
                if (c[i] == ' ') {
                    c[i] = 12288;
                } else if (c[i] < 127) {
                    c[i] += 'ﻠ';
                }
            }
        }

        return new String(c);
    }

    public static String toDbc(String input) {
        return toDbc(input, (Set)null);
    }

    public static String toDbc(String text, Set<Character> notConvertSet) {
        char[] c = text.toCharArray();

        for(int i = 0; i < c.length; ++i) {
            if (null == notConvertSet || !notConvertSet.contains(c[i])) {
                if (c[i] == 12288) {
                    c[i] = ' ';
                } else if (c[i] > '\uff00' && c[i] < '｟') {
                    c[i] -= 'ﻠ';
                }
            }
        }

        String returnString = new String(c);
        return returnString;
    }

    public static String digitUppercase(double n) {
        String[] fraction = new String[]{"角", "分"};
        String[] digit = new String[]{"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"};
        String[][] unit = new String[][]{{"元", "万", "亿"}, {"", "拾", "佰", "仟"}};
        String head = n < 0.0 ? "负" : "";
        n = Math.abs(n);
        String s = "";

        int integerPart;
        for(integerPart = 0; integerPart < fraction.length; ++integerPart) {
            s = s + (digit[(int)(Math.floor(n * 10.0 * Math.pow(10.0, (double)integerPart)) % 10.0)] + fraction[integerPart]).replaceAll("(零.)+", "");
        }

        if (s.length() < 1) {
            s = "整";
        }

        integerPart = (int)Math.floor(n);

        for(int i = 0; i < unit[0].length && integerPart > 0; ++i) {
            String p = "";

            for(int j = 0; j < unit[1].length && n > 0.0; ++j) {
                p = digit[integerPart % 10] + unit[1][j] + p;
                integerPart /= 10;
            }

            s = p.replaceAll("(零.)*零$", "").replaceAll("^$", "零") + unit[0][i] + s;
        }

        return head + s.replaceAll("(零.)*零元", "元").replaceFirst("(零.)+", "").replaceAll("(零.)+", "零").replaceAll("^整$", "零元整");
    }
}