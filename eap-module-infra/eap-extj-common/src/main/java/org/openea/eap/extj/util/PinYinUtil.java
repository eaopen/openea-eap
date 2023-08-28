package org.openea.eap.extj.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class PinYinUtil {
    public PinYinUtil() {
    }

    public static String getPingYin(String inputString) {
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_V);
        char[] input = inputString.trim().toCharArray();
        String output = "";

        try {
            for(int i = 0; i < input.length; ++i) {
                if (Character.toString(input[i]).matches("[\\u4E00-\\u9FA5]+")) {
                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(input[i], format);
                    output = output + temp[0];
                } else {
                    output = output + Character.toString(input[i]);
                }
            }
        } catch (BadHanyuPinyinOutputFormatCombination var6) {
            var6.printStackTrace();
        }

        return output;
    }

    public static String getFirstSpell(String chinese) {
        StringBuffer pybf = new StringBuffer();
        if (chinese == null) {
            return null;
        } else {
            char[] arr = chinese.toCharArray();
            HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
            defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
            defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);

            for(int i = 0; i < arr.length; ++i) {
                if (arr[i] > 128) {
                    try {
                        String[] temp = PinyinHelper.toHanyuPinyinStringArray(arr[i], defaultFormat);
                        if (temp != null) {
                            if (temp[0].length() <= 0) {
                                return null;
                            }

                            pybf.append(temp[0].charAt(0));
                        }
                    } catch (BadHanyuPinyinOutputFormatCombination var6) {
                        var6.printStackTrace();
                    }
                } else {
                    pybf.append(arr[i]);
                }
            }

            return pybf.toString().replaceAll("\\W", "").trim();
        }
    }

    public static String getFullSpell(String chinese) {
        StringBuffer pybf = new StringBuffer();
        char[] arr = chinese.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);

        for(int i = 0; i < arr.length; ++i) {
            if (arr[i] > 128) {
                try {
                    String[] ca = PinyinHelper.toHanyuPinyinStringArray(arr[i], defaultFormat);
                    String temp = ca == null ? "" : ca[0];
                    if (temp != null) {
                        pybf.append(temp);
                    }
                } catch (BadHanyuPinyinOutputFormatCombination var7) {
                    var7.printStackTrace();
                }
            } else {
                pybf.append(arr[i]);
            }
        }

        return pybf.toString();
    }
}

