package com.example;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Customer {

    private Long id;

    private String firstName, lastName;

}
