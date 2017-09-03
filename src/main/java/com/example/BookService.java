package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Component
public class BookService {

    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Transactional
    public void book(String... bookNames) {
        Arrays.asList(bookNames).forEach(bookName -> jdbcTemplate.update("insert into book (book_name) values (?)", bookName));
        System.out.println("Books stored successfully!!!");
    }

    public Book fetchByName(String bookName) {
        return jdbcTemplate.queryForObject("select id, book_name from book where book_name = ?", new Object[]{bookName},
                (rs, rowNum) -> new Book(rs.getLong("id"), rs.getString("book_name")));
    }

    public List<Book> fetchAll() {
        return jdbcTemplate.query("select id, book_name from book",
                (rs, rowNum) -> new Book(rs.getLong("id"), rs.getString("book_name")));
    }
}
