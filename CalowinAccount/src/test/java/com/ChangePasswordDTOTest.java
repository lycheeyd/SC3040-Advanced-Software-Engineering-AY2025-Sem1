package com;

import com.DataTransferObject.ChangePasswordDTO;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ChangePasswordDTOTest {
    @Test
    void testSettersAndGetters() {
        ChangePasswordDTO dto = new ChangePasswordDTO();
        dto.setUserID("USER1");
        dto.setOldPassword("old");
        dto.setNewPassword("new");
        dto.setConfirm_newPassword("new");

        assertThat(dto.getUserID()).isEqualTo("USER1");
        assertThat(dto.getOldPassword()).isEqualTo("old");
        assertThat(dto.getNewPassword()).isEqualTo("new");
        assertThat(dto.getConfirm_newPassword()).isEqualTo("new");
    }
}