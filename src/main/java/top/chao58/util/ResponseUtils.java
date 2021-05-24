package top.chao58.util;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class ResponseUtils {

    public static void print(HttpServletResponse response, R r) {
        response.setContentType("text/plain;charset=utf-8");
        try (PrintWriter writer = response.getWriter()) {
            writer.print(r);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
