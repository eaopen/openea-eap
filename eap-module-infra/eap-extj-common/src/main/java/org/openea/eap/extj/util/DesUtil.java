package org.openea.eap.extj.util;

import cn.hutool.core.codec.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.MessageDigest;
import java.security.SecureRandom;

public class DesUtil {
    private static final String MD5 = "MD5";
    private static final String SHA1 = "SHA1";
    private static final String HMAC_MD_5 = "HmacMD5";
    private static final String HMAC_SHA_1 = "HmacSHA1";
    private static final String DES = "DES";
    private static final String AES = "AES";
    private static String charset = "utf-8";
    private static int keysizeDES = 0;
    private static int keysizeAES = 128;
    private static String key = "jnpf";
    private static String SHORT_LINK_KEY = "jnpflink";

    public DesUtil() {
    }

    private static String messageDigest(String res, String algorithm) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte[] resBytes = charset == null ? res.getBytes() : res.getBytes(charset);
            return base64(md.digest(resBytes));
        } catch (Exception var4) {
            var4.printStackTrace();
            return null;
        }
    }

    private static String keyGeneratorMac(String res, String algorithm, String key) {
        try {
            SecretKey sk = null;
            if (key == null) {
                KeyGenerator kg = KeyGenerator.getInstance(algorithm);
                sk = kg.generateKey();
            } else {
                byte[] keyBytes = charset == null ? key.getBytes() : key.getBytes(charset);
                sk = new SecretKeySpec(keyBytes, algorithm);
            }

            Mac mac = Mac.getInstance(algorithm);
            mac.init((Key)sk);
            byte[] result = mac.doFinal(res.getBytes());
            return base64(result);
        } catch (Exception var6) {
            var6.printStackTrace();
            return null;
        }
    }

    private static String keyGeneratorEs(String res, String algorithm, String key, int keysize, boolean isEncode) {
        try {
            SecretKeySpec sks = null;
            KeyGenerator kg = KeyGenerator.getInstance(algorithm);
            if (key == null) {
                kg.init(keysize);
            } else {
                SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
                random.setSeed(key.getBytes());
                if (keysize == 0) {
                    kg.init(random);
                } else {
                    kg.init(keysize, random);
                }
            }

            SecretKey sk = kg.generateKey();
            sks = new SecretKeySpec(sk.getEncoded(), algorithm);
            Cipher cipher = Cipher.getInstance(algorithm);
            if (isEncode) {
                cipher.init(1, sks);
                byte[] resBytes = charset == null ? res.getBytes() : res.getBytes(charset);
                return parseByte2HexStr(cipher.doFinal(resBytes));
            } else {
                cipher.init(2, sks);
                return new String(cipher.doFinal(parseHexStr2Byte(res)));
            }
        } catch (Exception var10) {
            var10.printStackTrace();
            return null;
        }
    }

    private static String base64(byte[] res) {
        return Base64.encode(res);
    }

    public static String parseByte2HexStr(byte[] buf) {
        StringBuffer sb = new StringBuffer();

        for(int i = 0; i < buf.length; ++i) {
            String hex = Integer.toHexString(buf[i] & 255);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }

            sb.append(hex.toUpperCase());
        }

        return sb.toString();
    }

    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1) {
            return null;
        } else {
            byte[] result = new byte[hexStr.length() / 2];

            for(int i = 0; i < hexStr.length() / 2; ++i) {
                int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
                int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
                result[i] = (byte)(high * 16 + low);
            }

            return result;
        }
    }

    public static String md5(String res) {
        return messageDigest(res, "MD5");
    }

    public static String md5(String res, String key) {
        return keyGeneratorMac(res, "HmacMD5", key);
    }

    public static String sha1(String res) {
        return messageDigest(res, "SHA1");
    }

    public static String sha1(String res, String key) {
        return keyGeneratorMac(res, "HmacSHA1", key);
    }

    public static String desEncode(String res) {
        return keyGeneratorEs(res, "DES", key, keysizeDES, true);
    }

    public static String desDecode(String res) {
        return keyGeneratorEs(res, "DES", key, keysizeDES, false);
    }

    public static String aesEncode(String res) {
        return keyGeneratorEs(res, "AES", key, keysizeAES, true);
    }

    public static String aesDecode(String res) {
        return keyGeneratorEs(res, "AES", key, keysizeAES, false);
    }

    public static String base64Encode(String res) {
        return Base64.encode(res.getBytes());
    }

    public static String base64Decode(String res) {
        return new String(Base64.decode(res));
    }

    public static String aesOrDecode(String res, boolean isEncode, boolean isAes) {
        SecureRandom random = new SecureRandom();

        try {
            String codeType = "DES";
            String keyStr = SHORT_LINK_KEY;
            if (isAes) {
                codeType = "AES";
                keyStr = SHORT_LINK_KEY + SHORT_LINK_KEY;
            }

            SecretKeySpec secretkey = new SecretKeySpec(keyStr.getBytes(), codeType);
            Cipher cipher = Cipher.getInstance(codeType);
            byte[] result;
            if (isEncode) {
                cipher.init(1, secretkey, random);
                result = cipher.doFinal(res.getBytes());
                return parseByte2HexStr(result);
            } else {
                cipher.init(2, secretkey, random);
                result = parseHexStr2Byte(res);
                return new String(cipher.doFinal(result));
            }
        } catch (Exception var9) {
            return "";
        }
    }
}
