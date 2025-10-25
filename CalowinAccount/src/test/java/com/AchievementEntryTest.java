package com;

import com.Account.Entities.AchievementEntry;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class AchievementEntryTest {

    @Test
    void testNoArgsConstructor() {
        AchievementEntry entry = new AchievementEntry();
        assertThat(entry).isNotNull();
    }

    @Test
    void testAllArgsConstructorAndGetters() {
        AchievementEntry entry = new AchievementEntry("USER1", 100, 200, "Silver", "Gold");

        assertThat(entry.getUserID()).isEqualTo("USER1");
        assertThat(entry.getTotalCarbonSaved()).isEqualTo(100);
        assertThat(entry.getTotalCalorieBurnt()).isEqualTo(200);
        assertThat(entry.getCarbonMedal()).isEqualTo("Silver");
        assertThat(entry.getCalorieMedal()).isEqualTo("Gold");
    }

    @Test
    void testSetters() {
        AchievementEntry entry = new AchievementEntry();
        entry.setUserID("USER2");
        entry.setTotalCarbonSaved(50);
        entry.setTotalCalorieBurnt(75);
        entry.setCarbonMedal("Bronze");
        entry.setCalorieMedal("Bronze");

        assertThat(entry.getUserID()).isEqualTo("USER2");
        assertThat(entry.getTotalCarbonSaved()).isEqualTo(50);
        assertThat(entry.getTotalCalorieBurnt()).isEqualTo(75);
        assertThat(entry.getCarbonMedal()).isEqualTo("Bronze");
        assertThat(entry.getCalorieMedal()).isEqualTo("Bronze");
    }
}