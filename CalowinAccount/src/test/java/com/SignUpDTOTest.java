package com;


import com.DataTransferObject.SignupDTO;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class SignupDTOTest {
    @Test
    void testSettersAndGetters() {
        SignupDTO dto = new SignupDTO();
        dto.setEmail("test@test.com");
        dto.setName("Test User");
        dto.setPassword("pass");
        dto.setConfirm_password("pass");
        dto.setWeight(70.5f);

        assertThat(dto.getEmail()).isEqualTo("test@test.com");
        assertThat(dto.getName()).isEqualTo("Test User");
        assertThat(dto.getPassword()).isEqualTo("pass");
        assertThat(dto.getConfirm_password()).isEqualTo("pass");
        assertThat(dto.getWeight()).isEqualTo(70.5f);
    }
}