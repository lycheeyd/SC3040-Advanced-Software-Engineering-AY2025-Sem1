package com;

import com.Account.Entities.TripsEntry;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;

class TripsEntryTest {

    @Test
    void testNoArgsConstructor() {
        TripsEntry entry = new TripsEntry();
        assertThat(entry).isNotNull();
    }

    @Test
    void testAllArgsConstructorAndGetters() {
        LocalDateTime time = LocalDateTime.now();
        BigDecimal lat1 = new BigDecimal("1.23");
        BigDecimal lon1 = new BigDecimal("103.45");
        BigDecimal dist = new BigDecimal("5.5");

        TripsEntry entry = new TripsEntry("T1", "Start", "End", lon1, lat1, lon1, lat1, dist,
                150, 50, time, "WALK", "COMPLETED", "USER1");

        assertThat(entry.getTripID()).isEqualTo("T1");
        assertThat(entry.getStartLocation()).isEqualTo("Start");
        assertThat(entry.getEndLocation()).isEqualTo("End");
        assertThat(entry.getStartLongitude()).isEqualTo(lon1);
        assertThat(entry.getStartLatitude()).isEqualTo(lat1);
        assertThat(entry.getEndLongitude()).isEqualTo(lon1);
        assertThat(entry.getEndLatitude()).isEqualTo(lat1);
        assertThat(entry.getDistance()).isEqualTo(dist);
        assertThat(entry.getCaloriesBurnt()).isEqualTo(150);
        assertThat(entry.getCarbonSaved()).isEqualTo(50);
        assertThat(entry.getTripTime()).isEqualTo(time);
        assertThat(entry.getTravelMethod()).isEqualTo("WALK");
        assertThat(entry.getStatus()).isEqualTo("COMPLETED");
        assertThat(entry.getUserID()).isEqualTo("USER1");
    }

    @Test
    void testSetters() {
        TripsEntry entry = new TripsEntry();
        LocalDateTime time = LocalDateTime.now().minusHours(1);
        BigDecimal lat = new BigDecimal("2.0");
        BigDecimal lon = new BigDecimal("100.0");
        BigDecimal dist = new BigDecimal("10.0");

        entry.setTripID("T2");
        entry.setStartLocation("A");
        entry.setEndLocation("B");
        entry.setStartLongitude(lon);
        entry.setStartLatitude(lat);
        entry.setEndLongitude(lon);
        entry.setEndLatitude(lat);
        entry.setDistance(dist);
        entry.setCaloriesBurnt(300);
        entry.setCarbonSaved(100);
        entry.setTripTime(time);
        entry.setTravelMethod("CYCLE");
        entry.setStatus("IN_PROGRESS");
        entry.setUserID("USER2");

        assertThat(entry.getTripID()).isEqualTo("T2");
        assertThat(entry.getStartLocation()).isEqualTo("A");
        assertThat(entry.getEndLocation()).isEqualTo("B");
        assertThat(entry.getStartLongitude()).isEqualTo(lon);
        assertThat(entry.getStartLatitude()).isEqualTo(lat);
        assertThat(entry.getEndLongitude()).isEqualTo(lon);
        assertThat(entry.getEndLatitude()).isEqualTo(lat);
        assertThat(entry.getDistance()).isEqualTo(dist);
        assertThat(entry.getCaloriesBurnt()).isEqualTo(300);
        assertThat(entry.getCarbonSaved()).isEqualTo(100);
        assertThat(entry.getTripTime()).isEqualTo(time);
        assertThat(entry.getTravelMethod()).isEqualTo("CYCLE");
        assertThat(entry.getStatus()).isEqualTo("IN_PROGRESS");
        assertThat(entry.getUserID()).isEqualTo("USER2");
    }
}