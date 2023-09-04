package com.ruoyi.zlyyh.utils;

import cn.hutool.core.util.StrUtil;
import cn.tass.exceptions.TAException;
import cn.tass.hsmApi.hsmGeneralFinance.hsmGeneralFinance;
import cn.tass.kits.Forms;

import java.nio.charset.StandardCharsets;

/**
 * 配置文件加密
 *
 * @author 25487
 */
public class JasyptUtils {

    public static void main(String[] args) {
        //要加密的数据（数据库的用户名或密码）
        String secret = encrypt("904287b07f6c4c25bee5a28a1dfacaed");
        String symmetricKey = encrypt("9e161f6d9dba5ec1c1c129a213021ac19e161f6d9dba5ec1");
        String ylRsaPrivateKey = encrypt("MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAOHP3s9KCCmZRNJDTSLeGStXAYHMHrar6SIEirs5LPUXlgJ6x1RMfwr2B+sN7u2LjfOnJFbWpZ5wNSu7dOD47vTYvt4b8s5T7W6bNJ3grNRvsd5NJmNwCi3JhWXinAb0iaTTvTdXsRgqsFQkhN5IiT/fuJyQXfMq28N9iuj61Ue/AgMBAAECgYA5X0Ih8pJOV2W38meHw1wruchlPQJUsSrjfLnuxaEZiGuRp7Y7QdBJbacnXnvlGL7xN/cwpQqhTasEuHUCr9yuGLHqukLEkSQr9irfIkzowxF+TGBCNRqWKfA9I9SoqE0hfVhrHlLqDQAfSRYtv7ct9f59NZqxqOjU3SD7IEfSsQJBAPI28fvpx00XDkLnDSYRhIgKVfp3E29YOLqsYGQy2MYU8bDL7AkxNt8CGee3n77r3opq5TtNyNgCjJjAgZaDUBkCQQDuqfCE/d5FOx+g7zkbdw7m7YrI78DUivqZoHE4tMjRfWO4Wlc13oEJmTi6Q/fkwXAymLWnr9sPHdGXkTEN9nGXAkBRro/aaVMpvy2zCCIaBWxRSFMIXSGzueefpEP5ZmBOJIABJDPcQbmEaTv9NaJsizSIL1ZQsoGZvvpVBQ5uiKf5AkEA7N5VDGk59NQdLAjBUPT1IVSY7ZymcUwOM/L87B7hQKmGEy32aEyRHQmpN5TFr5Ac3tqZT/hFHdemcRt06DqzuQJAKvz0oWZbvfZYmfrFoQOgR+hxl0RxymeGDeLPq4iBBdUKkCm9ekGPFxGXyor0+uGyzmFM8A3GZ1PusAp417PHDQ==");
        System.out.println(secret);
        System.out.println(symmetricKey);
        System.out.println(ylRsaPrivateKey);
    }
//    public static void main(String[] args) {
//        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
//        //加密所需的salt(盐)
//        textEncryptor.setPassword("yzgnet.com");
//        System.out.println(textEncryptor.decrypt("vvJIqPhzx5XDsM+HlHpTYw=="));
//        System.out.println(textEncryptor.decrypt("w45nZ59Pkz8IfnWckT4sk9/IwEnHqSov"));
//        System.out.println(textEncryptor.decrypt("9lVmD6hCssTSDVFyVVMXAryf8usimNdD"));
////        //要加密的数据（数据库的用户名或密码）
////        String username = textEncryptor.encrypt("zlyyh");
////        String pawwword = textEncryptor.encrypt("Yzgnet.com");
////        String rd = textEncryptor.encrypt("zlyyh#@!13m");
////        String st = textEncryptor.encrypt("664a7c84048f4559a0a159b0733dee61");
////        String ab = textEncryptor.encrypt("e3df37c73bc7e9f4583d3ecb8a7ad6c2e3df37c73bc7e9f4");
////        String pi = textEncryptor.encrypt("MIICeQIBADANBgkqhkiG9w0BAQEFAASCAmMwggJfAgEAAoGBAKeJfobd84HmjBkYFGjvAVdYeLg1zS+XOTuCuPqGuuGTsYaorog2zRnBWSUQ0yuMteJnSIC3vxivR46Cd2IyV9bD7H1/SuRiFtMgdABAyNykLMGRV61V3lzPqnDxBkqgK0R5QYsvs0OS0gbdZUNPSxuFOfnptdq2GCxJOD9DCxMZAgMBAAECgYEApo7YzTfnGKOdcG0yDUhfavi3u1sxjDipW3KQd/Bt5kkw2pDkQuNIcGx6NZFOfyM6x8Sqnd0PDHlliFZIXcVy8Jszf4eCLIEbykptDtujjrBV01PqtKYVB3WgJP9o/baLftZsRCFTPb6psqczwli5kD83pCHNkklA7l5rZG/WcQECQQD6rNZFuckAta6rTkuFSeTlxPT3IBu90gsE9xxkiTI5uD+0B8+q7mo8bZrRY0hRV7slpkGqQbXCu/2f+q7Ie6iJAkEAqxiOJyXrbIl/RnKsTDZ/RsC9qCxcTb3DBwO55CfsLu74WMTKd3unC3EjtSn7IazGDIPjRjaG6rgSJ1n8B9FSEQJBAPAWdA9KFqMQX+AA2EIr+Qi8cGb0oL1YnGdACjicUreHqbPTO2oaeTOxQnPDpHMMFNnFd+UKlHyTsyHzZk3sagkCQQCVRqBIAaqMkM4tzcEL4YRcW69dOg7yePzect7N9BL5w9+Du3aWlpjgv76SwmTsNYy5wJwbV1mREjYshTMCMxuxAkEAiHYaeYsitAgFhiLWO63uED3HjL8OPNrL8myJwbbAD0N1tR+EqQt9HYhSbjzFbqmW7EqnEFM44yOvEoIqUYUUfg==");
////        String jt = textEncryptor.encrypt("abcde!@#ms123..mKK12IMtuvwxyz");
////        System.out.println("username:" + username);
////        System.out.println("password:" + pawwword);
////        System.out.println("password:" + rd);
////        System.out.println("password:" + st);
////        System.out.println("password:" + ab);
////        System.out.println("password:" + pi);
////        System.out.println("password:" + jt);
//    }

//    private static final String ip = "172.16.92.23";
//    private static final String port = "4034";
//    private static final String keyType = "00A";
//    private static final Integer key = 11;
//    private static hsmGeneralFinance instance;
//
//    static {
//        try {
//            instance = hsmGeneralFinance.getInstance("{[LOGGER];logsw=error;logPath=./log/;[HOST 1];hsmModel=EHSM;host=" + ip + ";linkNum=-10;timeout=3;port=" + port + ";}");
//        } catch (TAException e) {
//            e.printStackTrace();
//        }
//    }
    private static final String ip = "114.112.103.110";
    private static final String port = "36793";
    private static final String keyType = "00A";
    private static final Integer key = 11;
    private static hsmGeneralFinance instance;

    static {
        try {
            instance = hsmGeneralFinance.getInstance("{[LOGGER];logsw=error;logPath=./log/;[HOST 1];hsmModel=EHSM;host=" + ip + ";linkNum=-10;timeout=3;port=" + port + ";}");
        } catch (TAException e) {
            e.printStackTrace();
        }
    }

    /**
     * 加密
     *
     * @param value 待加密字符串
     */
    public static String encrypt(String value) {
        if (StrUtil.isBlank(value) || "null".equals(value)) {
            return value;
        }
        byte[] bytes;
        try {
            bytes = instance.generalDataEnc(0, keyType, key, "", 0, "",
                4, value.getBytes(StandardCharsets.UTF_8), "");
        } catch (TAException e) {
            throw new IllegalArgumentException(e);
        }
        return Forms.byteToHexString(bytes);
    }

//    public static void main(String[] args) {
//        // 银联测试环境
//        //要加密的数据（数据库的用户名或密码）
//        String redisPwd = encrypt("zlyyh#@!13m");
//        // 7907D2DF9322CD024F10CB32136FF8D0
//
//        String jwtKey = encrypt("abcde!@#ms123..mKK12IMtuvwxyz");
//        // 91D1A22E4980B2A223758E36857E7BE6B92AFE1B389E9BE3C2360B99F3BF35F9
//
//        String secret = encrypt("7209ef4d04694d9a9ff48db7b21651ab");
//        // FB4AC0A3D35009351D6E6A8704F9A58A5870E181BCFBF2FA0F69E5B122D5858892279E4D7950FA6DC7E39549D1331296
//
//        String symmetricKey = encrypt("2fcb048abc239831bac47fe3b32a2c012fcb048abc239831");
//        // A7E4FE3BF4C3479B61CCE05341AFBABF649FDE14091E6C1914D2C6C43439D370A7E4FE3BF4C3479B61CCE05341AFBABF92279E4D7950FA6DC7E39549D1331296
//
//        String ylRsaPrivateKey = encrypt("MIICeQIBADANBgkqhkiG9w0BAQEFAASCAmMwggJfAgEAAoGBAKeJfobd84HmjBkYFGjvAVdYeLg1zS+XOTuCuPqGuuGTsYaorog2zRnBWSUQ0yuMteJnSIC3vxivR46Cd2IyV9bD7H1/SuRiFtMgdABAyNykLMGRV61V3lzPqnDxBkqgK0R5QYsvs0OS0gbdZUNPSxuFOfnptdq2GCxJOD9DCxMZAgMBAAECgYEApo7YzTfnGKOdcG0yDUhfavi3u1sxjDipW3KQd/Bt5kkw2pDkQuNIcGx6NZFOfyM6x8Sqnd0PDHlliFZIXcVy8Jszf4eCLIEbykptDtujjrBV01PqtKYVB3WgJP9o/baLftZsRCFTPb6psqczwli5kD83pCHNkklA7l5rZG/WcQECQQD6rNZFuckAta6rTkuFSeTlxPT3IBu90gsE9xxkiTI5uD+0B8+q7mo8bZrRY0hRV7slpkGqQbXCu/2f+q7Ie6iJAkEAqxiOJyXrbIl/RnKsTDZ/RsC9qCxcTb3DBwO55CfsLu74WMTKd3unC3EjtSn7IazGDIPjRjaG6rgSJ1n8B9FSEQJBAPAWdA9KFqMQX+AA2EIr+Qi8cGb0oL1YnGdACjicUreHqbPTO2oaeTOxQnPDpHMMFNnFd+UKlHyTsyHzZk3sagkCQQCVRqBIAaqMkM4tzcEL4YRcW69dOg7yePzect7N9BL5w9+Du3aWlpjgv76SwmTsNYy5wJwbV1mREjYshTMCMxuxAkEAiHYaeYsitAgFhiLWO63uED3HjL8OPNrL8myJwbbAD0N1tR+EqQt9HYhSbjzFbqmW7EqnEFM44yOvEoIqUYUUfg==");
//        // 6B4B8C2A5FC94AA2C95A913D21EDFC89EFFD413566DD6F4B077BAF49737D2DE5B9167E181FC69E3590DCBF95C20B2B65B75756DDB266561C7C152DCC30F246F311B0B7CC1D7D93C7DCB07D3F66E117678B7C981695BA3024E82578360554E3F3D4C3167EE7F703DA1F8C91550302D6B745EAD79EA811D347C3D9ECAA3D47E93418C667254B729C1BC2C7E4F6AFBB3E2A5998A33991BDF37DE06EADEA4F326D3D4F2BD9A3B4ADE4E1CAE47F5FD54B316102FE74EA79740AC220284029F00765E9E3141D9E7AFD08F81B152F4EA2510B2907B82280CDD0F1960CC4C82781A61BA4AFCAD3A2AB0E1F77CA874BC234571A14D95AC40BE817253AE96885F47CFD386490764AB85FF5A9AF791C6C56D0EA97EC996E7D20450123ED47755698B3AFED2EB5DA9E6734128F23A3E0C581D1B9B5C05A076CD9B9F65A761A8CE80BA1702340F292AE489AAEC3599E26ED5E62417CF3FD759067C203E8404FB1D9DA9E8F9AB1763903EA1AD8A684B568F7C22112F0C8E539B7030A9F89A7C2367A4A58CC28AC03E5378DD52E0768721C3D4A8292777E346054B886C39F30583E4F225E7D9BA586B8236A6BFDC8D46B990DF8A8E7CA91824D38B0F23D4BC04065355DCA2955C0BBD0C3748AEB33ED9508153357103070A4EB56B41F9EC9F3C9F62B2034629E0263A14E048CE570038ADB04B2ABDD7B3743BFEC09F814683B5933577018F461910C63B9AF7EB8CCC10B17C8077AE2F760B1100F199BC652A2AE48F4B581ED261459D565602F9D4679B64A01EF601CB7F43D30A8EE44F24F2E8D761DFDDFB431BC545E203317F4C27D0691962222E06344BBBAB04E2BF08296E36ADD955B0CE9BE41F7AB90D8CCC823CFAC17EC2B94BC165F80F111B3C62411A6119A0202525C2ADD9DFAF4FF4C7B8092542D8B0138A78F04ABA9E89BE6D31C63514B4720059B37A6E825E64C7778F40C35B78FA3BC13B4AADC498DF3AE07E3E89DC3B0EC03749C7448A1E7146FED1B1DA5C1D34F2E3979E7F2E90986E418654347F21D9111B3522A8FFC66A4EFEE3FEF81705A1D214EEED0153CA44BA1C9F43019FB26A22D549FCBB71B480DE7255D44A7C716EAE3C98DD8D046B9D90F09C99EA6C65142A0DC91D4906C1B08E3058B17BAC5F8220F5030A60B4DECBDBDF9A7C1738602E51DAB851F34BA6E26B97A61385BEA9C2ADFDC6FC6CA6932C9C5BC97670C5E4FD00F4B12
//
//        String foodRsaPrivateKey = encrypt("MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJRRWWInHE/3Qsw0+35WT8eSR6XqeQVzdEz2Dsf6VdJS8c2eEaX+cCh7vjIFty7ENxkd7aKc4+HWSQO2dfy83p/cxsNXpJ7bfDB4SZ5S6Rr6irHCSZQymRuvGaBcjpnVX+qB3adp0UpGtG3z+AkTXnnnxKcFrYu3XF8GJb5Khbf5AgMBAAECgYACmRozufOpAu/Mm/D72Y80M7/FjEHqcodLAdRodF4kfQd3TpmIith0HRbL0YXP7+f3LKsI+i6Tuik1Q3D1qGlN5K2Xs+wPAh1br7nGVFPCJ39gO7081bY63fCDRt5rYcXyvNLXrr4hYp8HgKIO4dZCoObDpYipUz84f9uKuoXdwQJBAOG6UVIGgwtZrz1hQa9jgSF9OBTBtW5fOTJ1BLXFh88L/dy2OsdzOShR0AzfuGR8jRe01h2s3bs1S4ja5OICldUCQQCoNWRN6/MwYzfYdp8RqZ55RNakSGlCNGda4saaz7RSvfTyZ4jTBbtSBykgud9snuwbkex9yaGEgTkoi2M0pPeVAkEAsBpEwUKVT/CbF77dmPB/WNoxO3hYjJA7tlK25v0BVBWd62g7+Ui6aetR7glH+RV2me0aMrKfliMhF9b2RCESNQJAEExMEbjA8XlLme+0bfOvZTSkT3qsqDuHoCjE8Y8ae8HoD+y0Ny4g/kuvUnpwCYhEfE9hSLbWrY4PybvnutwZGQJAJ8mlk7C1i872Gx4zu+S89f/kS2WZzMHPJ7KHFHntzA2UxgXz6NxhX4UIALZSjh4nFvidhoZCPxpJbOC8APzX3w==");
//        // 9DA34D5DDD36DE197A47FFF7F68E31BBEFFD413566DD6F4B077BAF49737D2DE596127531F400ADED0267A87E4401080BEA2A7C09120E7D725494B5510959BBDE31C19EBB061951FF147F905A207C8542F6C3C22EE59C719E92FD4881E0D2D2721AA50C59537205E1F17172EB05BAE01800AD72098DC6925402566F0F62E6D2F8FC5E972412568806451AECB20D78811AB54C89DC02F8BB11B00C787D0C0FFEC312751691F5FBDF0DF4E0E0914052988FCF0327DDD7D3A20B7428813894EA68595BF45AA067ECEDDC1968EF62E8AE5D70547E544B7908802504F7A0F6B1AEDB6436B8B3F3C4ADFD0532AA6BB5462630D0CB69508FBD1EB7CCD1003936ECE098E86AC90E54C29D0A2D829F42C340C4AB7CD5E9C8CA2B2886545459E029B1E8A11B04406D874E00F3E878D3A64393ED2084380E626BCA9A4C907625EE1E001903C13A0B759EB7C697F18153A88E99167DBFBA94203E80E8E7F52AB823C760DB2F7A230AB2012077D1BB7BE4DC490F55D0AEA6002F7D99D1953A5CC708DF807627DF6725AE183872B5FCBCF923FDDD493CA9DE58E13B0F6FEB8A2A904574041523DB4EB7A1F0D6CC9385875B0C7E5FDC4B339BF185B0AE4EAAC92F4EC417B19690A06763291079597AFCE0A9FE6F8A8CE714D8029E09C2FD7BC802F934023C742DC68076D23F40769C63FB57D7651B72017F9DD6E16458FD00B7374BFDF5DEDDC7F3134C4B693CB0F9640837FB40BBB2D19377C6B5A9E17ABBD9E8004C9834E46BF5A5CC739723D977171DFCE42F27627C1EA9D797E18CD615D519A1992E8FAD9124C7CABE64E39448561E885FCAF9E9529F0A7DE8E6B4C946613ABCE9B991FDB9C1AFB18B0E86532DFF12CC7E405DC9C6FA74C9C25B5963E6F88E0B2656EC222C8EB85EF90D718D8BC8BF7C1DFCDC96483DAC6DF9D8E6F7746C0FAC7174339950E497D6DEBFAAA7322173A49EFE65B63BBEF3190E20BDEF740D55493A72C56160562F96A741B7EDF6658A0A93C4BDA4DC092E60B1B7A73B62F1B3F75A0ED46A0DD80F54C915C67D40F20A75999ECBE1D78C0100AB6A6DCFBFEA3A0CBF8E44F6E8FB208EB89F45A82DA2A2AC2E7DEEB74AA4416E61BCFB9195BC5788788255BDB0BC24B04DEBE8BF1AAB6FFD5E6E76585A58AC8CE4E37BFDA025D23EBCEA792B08B0B5B39A53BCDB3B449284651547FACEFE92279E4D7950FA6DC7E39549D1331296
//
//        String cloudSecret = encrypt("08dceb4040ab48a2b6d7ab70f1f98fe3");
//        // 6AAAEFB6081134DFD1447E4AC8F05F699B053BAF9B0D1D93BAA31223EFDE869192279E4D7950FA6DC7E39549D1331296
//
//        String cloudSymmetricKey = encrypt("+LuYJiZxtpDE92z3XUKHLg==");
//        // 866525D58F16662A620377D77459B4C11A56A97604AD6F38217A61C255ADD734
//
//        String ysfmerchantToken = encrypt("FF31307CF3A29E2D");
//        // E5B1AB73B491BEEE23229A9392E30BEF92279E4D7950FA6DC7E39549D1331296
//
//        String ysfmerchantCertPwd = encrypt("yzg1234");
//        // EB46D1D5D937ADDED23773FFCEF4F98E
//
//        String databaseUser = encrypt("zlyyh");
//        // 579123AD6AD21E9C88A81A2DDDBB815B
//
//        String databasePwd = encrypt("Yzgnet.com");
//        // 54FE07E288CB03AE7FE93B4E447C2861
//
//        String monitorUser = encrypt("ruoyi");
//        // AB7AE507634A04C5FFDA4ABA26A986D8
//
//        String monitorPwd = encrypt("123456");
//        // 9766161BF8259B33EBE328EA079C53C7
//
//        String sentinelUser = encrypt("sentinel");
//        // 3956AC10BE9B0ED52A588C3413243161
//
//        String sentinelPwd = encrypt("sentinel");
//        // 3956AC10BE9B0ED52A588C3413243161
//
//        String xxlJobToken = encrypt("YzgJobToken123!@#");
//        // 6F4527FE53944C91181F80EA6AF48D8061B833EFE50E8C0E838D65C52D33C773
//
//        String nacosIdentityKey = encrypt("yzg");
//        // 5B444ADD41E19BA628BD81FA8DA9C80E
//
//        String nacosIdentityValue = encrypt("YzgJo123bToken123!@#");
//        // 1E116E5F043FDFFAE1EFEDF5DADDD4505B6BF41349FE8246A42A505E699165A6
//
//        String nacosTokenSecretKey = encrypt("YzgJobToken1fsadfw71j23mfsdafas23!@#");
//        // 751AC2E3DEF8A54A5A58238909B44096D76853059140F1A788C477CF9BE12EA15B6BF41349FE8246A42A505E699165A6
//
//        System.out.println("redisPwd:" + redisPwd);
//        System.out.println("jwtKey:" + jwtKey);
//        System.out.println("secret:" + secret);
//        System.out.println("symmetricKey:" + symmetricKey);
//        System.out.println("ylRsaPrivateKey:" + ylRsaPrivateKey);
//        System.out.println("foodRsaPrivateKey:" + foodRsaPrivateKey);
//        System.out.println("cloudSecret:" + cloudSecret);
//        System.out.println("cloudSymmetricKey:" + cloudSymmetricKey);
//        System.out.println("ysfmerchantToken:" + ysfmerchantToken);
//        System.out.println("ysfmerchantCertPwd:" + ysfmerchantCertPwd);
//        System.out.println("databaseUser:" + databaseUser);
//        System.out.println("databasePwd:" + databasePwd);
//        System.out.println("monitorUser:" + monitorUser);
//        System.out.println("monitorPwd:" + monitorPwd);
//        System.out.println("sentinelUser:" + sentinelUser);
//        System.out.println("sentinelPwd:" + sentinelPwd);
//        System.out.println("xxlJobToken:" + xxlJobToken);
//        System.out.println("nacosIdentityKey:" + nacosIdentityKey);
//        System.out.println("nacosIdentityValue:" + nacosIdentityValue);
//        System.out.println("nacosTokenSecretKey:" + nacosTokenSecretKey);
//
//
//        // 云之谷测试环境
////        //要加密的数据（数据库的用户名或密码）
////        String redisPwd = encrypt("Yzg!@#13.8Mdc");
////        String jwtKey = encrypt("abcde!@#ms123..mKK12IMtuvwxyz");
////        String secret = encrypt("664a7c84048f4559a0a159b0733dee61");
////        String symmetricKey = encrypt("e3df37c73bc7e9f4583d3ecb8a7ad6c2e3df37c73bc7e9f4");
////        String ylRsaPrivateKey = encrypt("MIICeQIBADANBgkqhkiG9w0BAQEFAASCAmMwggJfAgEAAoGBAKeJfobd84HmjBkYFGjvAVdYeLg1zS+XOTuCuPqGuuGTsYaorog2zRnBWSUQ0yuMteJnSIC3vxivR46Cd2IyV9bD7H1/SuRiFtMgdABAyNykLMGRV61V3lzPqnDxBkqgK0R5QYsvs0OS0gbdZUNPSxuFOfnptdq2GCxJOD9DCxMZAgMBAAECgYEApo7YzTfnGKOdcG0yDUhfavi3u1sxjDipW3KQd/Bt5kkw2pDkQuNIcGx6NZFOfyM6x8Sqnd0PDHlliFZIXcVy8Jszf4eCLIEbykptDtujjrBV01PqtKYVB3WgJP9o/baLftZsRCFTPb6psqczwli5kD83pCHNkklA7l5rZG/WcQECQQD6rNZFuckAta6rTkuFSeTlxPT3IBu90gsE9xxkiTI5uD+0B8+q7mo8bZrRY0hRV7slpkGqQbXCu/2f+q7Ie6iJAkEAqxiOJyXrbIl/RnKsTDZ/RsC9qCxcTb3DBwO55CfsLu74WMTKd3unC3EjtSn7IazGDIPjRjaG6rgSJ1n8B9FSEQJBAPAWdA9KFqMQX+AA2EIr+Qi8cGb0oL1YnGdACjicUreHqbPTO2oaeTOxQnPDpHMMFNnFd+UKlHyTsyHzZk3sagkCQQCVRqBIAaqMkM4tzcEL4YRcW69dOg7yePzect7N9BL5w9+Du3aWlpjgv76SwmTsNYy5wJwbV1mREjYshTMCMxuxAkEAiHYaeYsitAgFhiLWO63uED3HjL8OPNrL8myJwbbAD0N1tR+EqQt9HYhSbjzFbqmW7EqnEFM44yOvEoIqUYUUfg==");
////        String foodRsaPrivateKey = encrypt("MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJRRWWInHE/3Qsw0+35WT8eSR6XqeQVzdEz2Dsf6VdJS8c2eEaX+cCh7vjIFty7ENxkd7aKc4+HWSQO2dfy83p/cxsNXpJ7bfDB4SZ5S6Rr6irHCSZQymRuvGaBcjpnVX+qB3adp0UpGtG3z+AkTXnnnxKcFrYu3XF8GJb5Khbf5AgMBAAECgYACmRozufOpAu/Mm/D72Y80M7/FjEHqcodLAdRodF4kfQd3TpmIith0HRbL0YXP7+f3LKsI+i6Tuik1Q3D1qGlN5K2Xs+wPAh1br7nGVFPCJ39gO7081bY63fCDRt5rYcXyvNLXrr4hYp8HgKIO4dZCoObDpYipUz84f9uKuoXdwQJBAOG6UVIGgwtZrz1hQa9jgSF9OBTBtW5fOTJ1BLXFh88L/dy2OsdzOShR0AzfuGR8jRe01h2s3bs1S4ja5OICldUCQQCoNWRN6/MwYzfYdp8RqZ55RNakSGlCNGda4saaz7RSvfTyZ4jTBbtSBykgud9snuwbkex9yaGEgTkoi2M0pPeVAkEAsBpEwUKVT/CbF77dmPB/WNoxO3hYjJA7tlK25v0BVBWd62g7+Ui6aetR7glH+RV2me0aMrKfliMhF9b2RCESNQJAEExMEbjA8XlLme+0bfOvZTSkT3qsqDuHoCjE8Y8ae8HoD+y0Ny4g/kuvUnpwCYhEfE9hSLbWrY4PybvnutwZGQJAJ8mlk7C1i872Gx4zu+S89f/kS2WZzMHPJ7KHFHntzA2UxgXz6NxhX4UIALZSjh4nFvidhoZCPxpJbOC8APzX3w==");
////        String cloudSecret = encrypt("08dceb4040ab48a2b6d7ab70f1f98fe3");
////        String cloudSymmetricKey = encrypt("+LuYJiZxtpDE92z3XUKHLg==");
////        String ysfmerchantToken = encrypt("FF31307CF3A29E2D");
////        String ysfmerchantCertPwd = encrypt("yzg1234");
////        String databaseUser = encrypt("yzg");
////        String databasePwd = encrypt("Yzgnet.com610!@#");
////        String monitorUser = encrypt("ruoyi");
////        String monitorPwd = encrypt("123456");
////        String sentinelUser = encrypt("sentinel");
////        String sentinelPwd = encrypt("sentinel");
////        String xxlJobToken = encrypt("YzgJobToken123!@#");
////        String nacosIdentityKey = encrypt("yzg");
////        String nacosIdentityValue = encrypt("YzgJo123bToken123!@#");
////        String nacosTokenSecretKey = encrypt("YzgJobToken1fsadfw71j23mfsdafas23!@#");
////        System.out.println("redisPwd:" + redisPwd);
////        System.out.println("jwtKey:" + jwtKey);
////        System.out.println("secret:" + secret);
////        System.out.println("symmetricKey:" + symmetricKey);
////        System.out.println("ylRsaPrivateKey:" + ylRsaPrivateKey);
////        System.out.println("foodRsaPrivateKey:" + foodRsaPrivateKey);
////        System.out.println("cloudSecret:" + cloudSecret);
////        System.out.println("cloudSymmetricKey:" + cloudSymmetricKey);
////        System.out.println("ysfmerchantToken:" + ysfmerchantToken);
////        System.out.println("ysfmerchantCertPwd:" + ysfmerchantCertPwd);
////        System.out.println("databaseUser:" + databaseUser);
////        System.out.println("databasePwd:" + databasePwd);
////        System.out.println("monitorUser:" + monitorUser);
////        System.out.println("monitorPwd:" + monitorPwd);
////        System.out.println("sentinelUser:" + sentinelUser);
////        System.out.println("sentinelPwd:" + sentinelPwd);
////        System.out.println("xxlJobToken:" + xxlJobToken);
////        System.out.println("nacosIdentityKey:" + nacosIdentityKey);
////        System.out.println("nacosIdentityValue:" + nacosIdentityValue);
////        System.out.println("nacosTokenSecretKey:" + nacosTokenSecretKey);
//    }
}
