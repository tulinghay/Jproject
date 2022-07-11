package com.example.notebook.utils;

import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

public class MD5Utils {
    /**
     *         <!--md5 依赖-->
     *         <dependency>
     *             <groupId>commons-codec</groupId>
     *             <artifactId>commons-codec</artifactId>
     *             <version>1.3</version>
     *         </dependency>
     *
     *         <dependency>
     *             <groupId>org.apache.commons</groupId>
     *             <artifactId>commons-lang3</artifactId>
     *             <version>3.1</version>
     *         </dependency>
     * @return
     */
    // 定义salt用来参与加密
    public static final String salt = "1a2b3c4d";

    /**
     * 直接对传入字符串进行加密
     * @param src
     * @return
     */
    public static String md5(String src) {

//        PriorityQueue<Integer> p = new PriorityQueue<>(2, (a, b) ->(a - b));
        return DigestUtils.md5DigestAsHex(src.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 使用salt将字符串进行加密
     * @param inputPass
     * @return
     */
    public static String inputPassToFromPass(String inputPass) {
        String str = salt.charAt(0) + salt.charAt(2) + inputPass + salt.charAt(5) + salt.charAt(4);
        return md5(str);
    }

    /**
     * 使用传入的salt进行加密
     * @param fromPass
     * @param salt
     * @return
     */
    public static String fromPassToDBPass(String fromPass, String salt) {
        String str = salt.charAt(0) + salt.charAt(2) + fromPass + salt.charAt(5) + salt.charAt(4);
        return md5(str);
    }

    /**
     * 先使用final salt进行加密，后使用传入的salt进行二次加密
     * @param inputPass
     * @param salt
     * @return
     */
    public static String inputPassDBPass(String inputPass, String salt) {
        String fromPass = inputPassToFromPass(inputPass);
        String dbPass = fromPassToDBPass(fromPass, salt);
        return dbPass;
    }

}
