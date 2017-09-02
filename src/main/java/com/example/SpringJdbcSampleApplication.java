package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
public class SpringJdbcSampleApplication implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public static void main(String[] args) {
        SpringApplication.run(SpringJdbcSampleApplication.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {
        jdbcTemplate.execute("DROP TABLE CUSTOMER IF EXISTS");
        jdbcTemplate.execute("CREATE TABLE CUSTOMER (ID SERIAL, FIRST_NAME VARCHAR2(255), LAST_NAME VARCHAR2(255))");

        List<Object[]> stringNames = Arrays.asList("Jayant Kumar", "Latha Jangay", "Bhavani J").stream().map(name -> name.split(" ")).collect(Collectors.toList());

//        System.out.println(stringNames);

        jdbcTemplate.batchUpdate("INSERT INTO CUSTOMER (FIRST_NAME, LAST_NAME) VALUES (?, ?)", stringNames);


        jdbcTemplate.query("SELECT ID, FIRST_NAME, LAST_NAME FROM CUSTOMER WHERE first_name = ?", new Object[]{"Bhavani"},
                (rs, rowNum) -> new Customer(rs.getLong("id"), rs.getString("first_name"), rs.getString("last_name"))
        ).stream().forEach(customer -> System.out.println(customer));
    }
}
