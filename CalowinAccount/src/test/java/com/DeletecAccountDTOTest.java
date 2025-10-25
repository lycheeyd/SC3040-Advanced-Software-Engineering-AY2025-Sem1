package com;

import com.DataTransferObject.DeleteAccountDTO;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class DeleteAccountDTOTest {
    @Test
    void testSettersAndGetters() {
        DeleteAccountDTO dto = new DeleteAccountDTO();
        dto.setUserID("USER1");
        dto.setEmail("test@test.com");
        dto.setOtpCode("123456");

        assertThat(dto.getUserID()).isEqualTo("USER1");
        assertThat(dto.getEmail()).isEqualTo("test@test.com");
        assertThat(dto.getOtpCode()).isEqualTo("123456");
    }
}