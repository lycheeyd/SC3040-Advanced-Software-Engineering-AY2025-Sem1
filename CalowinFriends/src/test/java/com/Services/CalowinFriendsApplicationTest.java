package com.Services;


import com.CalowinFriendsApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CalowinFriendsApplicationTest {

    @Test
    void contextLoads() {
        // This test ensures the Spring application context can start successfully
    }

    @Test
    void mainApplicationStarts() {
        // This test calls the main method for code coverage
        CalowinFriendsApplication.main(new String[]{});
    }
}