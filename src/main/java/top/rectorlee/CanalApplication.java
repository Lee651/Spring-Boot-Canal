package top.rectorlee;

import com.xpand.starter.canal.annotation.EnableCanalClient;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableCanalClient
@MapperScan(basePackages = "top.rectorlee.mapper")
public class CanalApplication {
    public static void main(String[] args) {
        SpringApplication.run(CanalApplication.class, args);
    }
}
