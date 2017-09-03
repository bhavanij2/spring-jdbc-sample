package com.example;

import io.vavr.control.Try;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;

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
        jdbcTemplate.execute("CREATE TABLE CUSTOMER (ID IDENTITY, FIRST_NAME VARCHAR2(255), LAST_NAME VARCHAR2(255))");

        List<Object[]> stringNames = Arrays.asList("Jayant Kumar", "Bona J", "Latha Jangay").stream().map(name -> name.split(" ")).collect(Collectors.toList());

//        System.out.println(stringNames);

        jdbcTemplate.batchUpdate("INSERT INTO CUSTOMER (FIRST_NAME, LAST_NAME) VALUES (?, ?)", stringNames);


        jdbcTemplate.query("SELECT ID, FIRST_NAME, LAST_NAME FROM CUSTOMER WHERE first_name = ?", new Object[]{"Bona"},
                (rs, rowNum) -> new Customer(rs.getLong("id"), rs.getString("first_name"), rs.getString("last_name"))
        ).stream().forEach(customer -> System.out.println(customer));
    }


    @Bean
    public CommandLineRunner transactionDemo(BookService bookService) {
        return (args) -> {
            jdbcTemplate.execute("DROP TABLE BOOK IF EXISTS");
            jdbcTemplate.execute("CREATE TABLE BOOK (ID IDENTITY, BOOK_NAME VARCHAR2(5) NOT NULL)");

            String[] bookNames = {"App", "Bat", "Cat"};

            Try.run(() -> {
                bookService.book(bookNames);
            }).onFailure(throwable -> {
                String throwableString = String.format("%s :: %s :: %s", throwable.getCause(), throwable.getMessage(), throwable.getClass());
                System.out.println(throwableString);
            });


            bookService.fetchAll().forEach(book -> System.out.println(book));

            System.out.println("*********");

            Try.run(() -> {
                String[] books2 = {"Dog", "Elephant"};
                bookService.book(books2);
            }).onFailure(throwable -> {
                String throwableString = String.format("%s :: %s :: %s", throwable.getCause(), throwable.getMessage(), throwable.getClass());
                System.out.println(throwableString);
            });

            bookService.fetchAll().forEach(book -> System.out.println(book));
//            Assert.isTrue(bookService.fetchAll().size() == 3, "3 books not found!");

        };
    }
}
