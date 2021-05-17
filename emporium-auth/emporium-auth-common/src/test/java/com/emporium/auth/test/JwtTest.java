package com.emporium.auth.test;

import com.emporium.auth.pojo.UserInfo;
import com.emporium.auth.utils.JwtUtils;
import com.emporium.auth.utils.RsaUtils;
import org.junit.Before;
import org.junit.Test;

import java.security.PrivateKey;
import java.security.PublicKey;

public class JwtTest {

    private static final String pubKeyPath = "E:\\temp\\rsa\\rsa.pub";

    private static final String priKeyPath = "E:\\temp\\rsa\\rsa.pri";

    private PublicKey publicKey;

    private PrivateKey privateKey;

    @Test
    public void testRsa() throws Exception {
        //盐
        RsaUtils.generateKey(pubKeyPath, priKeyPath, "234");
    }

    @Before
    public void testGetRsa() throws Exception {
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
    }

    @Test
    public void testGenerateToken() throws Exception {
        // 生成token
        String token = JwtUtils.generateToken(new UserInfo(20L, "jack"), privateKey, 5);
        System.out.println("token = " + token);
    }

    @Test
    public void testParseToken() throws Exception {
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6MjAsInVzZXJuYW1lIjoiamFjayIsImV4cCI6MTU2NjgxOTI0N30.HIQOx497AuYCwLHrhlyLQXFwX0a9lsBkvc1ZYoOxHkk4mXNE_x2Uj0k6ZsSfO6bcMNHnVBvxcKVYrxB_aHgoTfH1B1oMO7WMB8KV64HypEmkKg1EF8-YtQaYPbS1wPlCCLwTLPWVkQyNC6ljoPX3xkJ-gJkJRB3ePuMTRezCJjY";
        // 解析token---获取payload
        UserInfo user = JwtUtils.getInfoFromToken(token, publicKey);
        System.out.println("id: " + user.getId());
        System.out.println("userName: " + user.getUsername());
    }
}