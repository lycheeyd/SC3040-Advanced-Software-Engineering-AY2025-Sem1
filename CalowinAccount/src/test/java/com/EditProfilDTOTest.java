package com;

import com.DataTransferObject.EditProfileDTO;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class EditProfileDTOTest {
    @Test
    void testSettersAndGetters() {
        EditProfileDTO dto = new EditProfileDTO();
        dto.setUserID("USER1");
        dto.setName("New Name");
        dto.setWeight(75.5f);
        dto.setBio("New bio");

        assertThat(dto.getUserID()).isEqualTo("USER1");
        assertThat(dto.getName()).isEqualTo("New Name");
        assertThat(dto.getWeight()).isEqualTo(75.5f);
        assertThat(dto.getBio()).isEqualTo("New bio");
    }
}