package ynu.edu.orderservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import feign.Retryer;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@MapperScan("ynu.edu.orderservice.mapper")
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
    
    /**
     * 配置Feign客户端重试器
     * period: 重试的间隔时间(ms)
     * maxPeriod: 最大重试时间(ms)
     * maxAttempts: 最大重试次数
     */
    @Bean
    public Retryer feignRetryer() {
        return new Retryer.Default(100, 1000, 3);
    }
} 