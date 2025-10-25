package com;


import com.DataTransferObject.SendOtpDTO;
import com.Account.Entities.ActionType;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class SendOtpDTOTest {
    @Test
    void testSettersAndGetters() {
        SendOtpDTO dto = new SendOtpDTO();
        dto.setEmail("test@test.com");
        dto.setType(ActionType.SIGN_UP);

        assertThat(dto.getEmail()).isEqualTo("test@test.com");
        assertThat(dto.getType()).isEqualTo(ActionType.SIGN_UP);
    }
}