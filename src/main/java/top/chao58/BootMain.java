package top.chao58;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("top.chao58.mapper")
@SpringBootApplication
public class BootMain {

    public static void main(String[] args) {
        SpringApplication.run(BootMain.class, args);
    }


}
