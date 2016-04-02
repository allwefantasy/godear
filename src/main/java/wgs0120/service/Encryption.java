package wgs0120.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.csdn.common.settings.Settings;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 1/12/14 WilliamZhu(allwefantasy@gmail.com)
 */
@Singleton
public class Encryption {

    private Settings settings;

    @Inject
    public Encryption(Settings settings) {

        this.settings = settings;
    }

    private byte[] md5ToByte(String str) {
        MessageDigest messageDigest = null;

        try {
            messageDigest = MessageDigest.getInstance("MD5");

            messageDigest.reset();

            messageDigest.update(str.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            System.out.println("NoSuchAlgorithmException caught!");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        byte[] byteArray = messageDigest.digest();
        return byteArray;
    }

    public String encrypt(String str) {
        str += settings.get("password.secret", "123456789123456789123456789123456789123456789");
        byte[] byteArray = md5ToByte(str);

        StringBuffer md5StrBuff = new StringBuffer();

        for (int i = 0; i < byteArray.length; i++) {
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
                md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
            else
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
        }

        return md5StrBuff.toString();
    }

}
