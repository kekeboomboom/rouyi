package com.cogent.system.common;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/11/7
 * {@code @description:}
 */
public class SDIUtil {

    /**
     * sdiName such as SDI-1 SDI-2
     * @param sdiName
     * @param sn
     * @return
     */
    public static String getSDIDestName(String sdiName,String sn) {
        return sdiName + "_" + sn;
    }
}
