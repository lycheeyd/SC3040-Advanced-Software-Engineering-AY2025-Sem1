package com;


import com.Account.Entities.ActionType;
import com.Account.Entities.OTPEntry;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;

class OTPEntryTest {

    @Test
    void testNoArgsConstructor() {
        OTPEntry entry = new OTPEntry();
        assertThat(entry).isNotNull();
    }

    @Test
    void testAllArgsConstructorAndGetters() {
        LocalDateTime time = LocalDateTime.now();
        OTPEntry entry = new OTPEntry("test@test.com", "123456", time, ActionType.SIGN_UP);

        assertThat(entry.getEmail()).isEqualTo("test@test.com");
        assertThat(entry.getOtpCode()).isEqualTo("123456");
        assertThat(entry.getExpiresAt()).isEqualTo(time);
        assertThat(entry.getOtpType()).isEqualTo(ActionType.SIGN_UP);
    }

    @Test
    void testSetters() {
        OTPEntry entry = new OTPEntry();
        LocalDateTime time = LocalDateTime.now().plusDays(1);

        entry.setEmail("new@test.com");
        entry.setOtpCode("654321");
        entry.setExpiresAt(time);
        entry.setOtpType(ActionType.FORGOT_PASSWORD);

        assertThat(entry.getEmail()).isEqualTo("new@test.com");
        assertThat(entry.getOtpCode()).isEqualTo("654321");
        assertThat(entry.getExpiresAt()).isEqualTo(time);
        assertThat(entry.getOtpType()).isEqualTo(ActionType.FORGOT_PASSWORD);
    }
}