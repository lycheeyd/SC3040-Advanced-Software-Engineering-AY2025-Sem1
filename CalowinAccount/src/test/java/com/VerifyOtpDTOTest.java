package com;


import com.DataTransferObject.VerifyOtpDTO;
import com.Account.Entities.ActionType;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class VerifyOtpDTOTest {
    @Test
    void testSettersAndGetters() {
        VerifyOtpDTO dto = new VerifyOtpDTO();
        dto.setEmail("test@test.com");
        dto.setOtpCode("123456");
        dto.setType(ActionType.SIGN_UP);

        assertThat(dto.getEmail()).isEqualTo("test@test.com");
        assertThat(dto.getOtpCode()).isEqualTo("123456");
        assertThat(dto.getType()).isEqualTo(ActionType.SIGN_UP);
    }
}
