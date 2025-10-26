package com;

import com.DataTransferObject.LoginDTO;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class LoginDTOTest {
    @Test
    void testSettersAndGetters() {
        LoginDTO dto = new LoginDTO();
        dto.setEmail("test@test.com");
        dto.setPassword("pass");

        assertThat(dto.getEmail()).isEqualTo("test@test.com");
        assertThat(dto.getPassword()).isEqualTo("pass");
    }
}