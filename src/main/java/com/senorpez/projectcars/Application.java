package com.senorpez.projectcars;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
	DB2.main(args);
//        DatabaseFactory.main(args);
        //SpringApplication.run(Application.class, args);
    }
}
