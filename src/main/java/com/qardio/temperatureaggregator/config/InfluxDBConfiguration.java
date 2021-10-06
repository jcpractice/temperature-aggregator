package com.qardio.temperatureaggregator.config;

import lombok.Data;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/*
    This class is written forcefully as the Spring boot provided jar "influxdb-java" is not working.
 */
@Configuration
@PropertySource("classpath:application.properties")
@Data
@Setter
public class InfluxDBConfiguration {
    @Value("${influx.db.tokenstr}")
    private String tokenStr;
    @Value("${influx.db.url}")
    private String databaseUrl;
    @Value("${influx.db.bucket}")
    private String bucketName;
    @Value("${influx.db.org.name}")
    private String orgName;
    @Value("${influx.db.username}")
    private String userName;
    @Value("${influx.db.password}")
    private String password;

}
