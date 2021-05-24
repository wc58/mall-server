package top.chao58.test;


import org.junit.Test;
import top.chao58.security.TokenUtils;

public class TokenUtilsTest {

    @Test
    public void generateToken() {
        TokenUtils tokenUtils = new TokenUtils();
        String token = tokenUtils.generateToken("chao");
        System.out.println(token);
    }


    @Test
    public void getUsernameFromToken() {
        String token = "eyJhbGciOiJIUzUxMiJ9.eyJfQ1JFQVRFRCI6MTYyMDQ4MDQ0NDYzNywiX1VTRVIiOiJjaGFvIiwiZXhwIjoxNjIwNDgwNTA1fQ.DsiEmUWA49ktf6VputtJ6y7XWhMVxGkfkb0RlQiflApFXVF8RfR08za36yitcrEcARNsqdOYOGdz-ZxpLQ9Z2w";
        TokenUtils tokenUtils = new TokenUtils();
        String username = tokenUtils.getUsernameFromToken(token);
        System.out.println(tokenUtils.validateToken(token, "chao"));
        System.out.println(username);
    }


}
