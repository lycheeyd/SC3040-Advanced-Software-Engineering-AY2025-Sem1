package com;

import com.DataTransferObject.LoginResponseDTO;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class LoginResponseDTOTest {
    @Test
    void testConstructorAndGetters() {
        LoginResponseDTO dto = new LoginResponseDTO("USER1", "test@test.com", "Test User", 70f, "bio",
                100, 200, "Silver", "Gold");

        assertThat(dto.getUserID()).isEqualTo("USER1");
        assertThat(dto.getEmail()).isEqualTo("test@test.com");
        assertThat(dto.getName()).isEqualTo("Test User");
        assertThat(dto.getWeight()).isEqualTo(70f);
        assertThat(dto.getBio()).isEqualTo("bio");
        assertThat(dto.getTotalCarbonSaved()).isEqualTo(100);
        assertThat(dto.getTotalCalorieBurnt()).isEqualTo(200);
        assertThat(dto.getCarbonMedal()).isEqualTo("Silver");
        assertThat(dto.getCalorieMedal()).isEqualTo("Gold");
    }

    @Test
    void testSetters() {
        LoginResponseDTO dto = new LoginResponseDTO(null, null, null, 0, null, 0, 0, null, null);

        dto.setUserID("USER2");
        dto.setEmail("new@test.com");
        dto.setName("New Name");
        dto.setWeight(80f);
        dto.setBio("new bio");
        dto.setTotalCarbonSaved(150);
        dto.setTotalCalorieBurnt(250);
        dto.setCarbonMedal("Gold");
        dto.setCalorieMedal("Platinum");

        assertThat(dto.getUserID()).isEqualTo("USER2");
        assertThat(dto.getEmail()).isEqualTo("new@test.com");
        assertThat(dto.getName()).isEqualTo("New Name");
        assertThat(dto.getWeight()).isEqualTo(80f);
        assertThat(dto.getBio()).isEqualTo("new bio");
        assertThat(dto.getTotalCarbonSaved()).isEqualTo(150);
        assertThat(dto.getTotalCalorieBurnt()).isEqualTo(250);
        assertThat(dto.getCarbonMedal()).isEqualTo("Gold");
        assertThat(dto.getCalorieMedal()).isEqualTo("Platinum");
    }
}