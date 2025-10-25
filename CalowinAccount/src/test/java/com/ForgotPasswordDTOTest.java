package com;

import com.DataTransferObject.ForgotPasswordDTO;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ForgotPasswordDTOTest {
    @Test
    void testSettersAndGetters() {
        ForgotPasswordDTO dto = new ForgotPasswordDTO();
        dto.setEmail("test@test.com");
        dto.setOtpCode("123456");

        assertThat(dto.getEmail()).isEqualTo("test@test.com");
        assertThat(dto.getOtpCode()).isEqualTo("123456");
    }
}