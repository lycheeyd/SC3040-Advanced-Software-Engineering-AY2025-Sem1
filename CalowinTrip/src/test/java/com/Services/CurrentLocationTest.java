package com.Services;


import com.model.CurrentLocation;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class CurrentLocationTest {
    @Test
    void testGettersAndSetters() {
        CurrentLocation location = new CurrentLocation("Current", 30.0, 40.0);
        assertThat(location.getName()).isEqualTo("Current");
        assertThat(location.getLatitude()).isEqualTo(30.0);
        assertThat(location.getLongitude()).isEqualTo(40.0);

        location.setName("New Current");
        location.setLatitude(31.0);
        location.setLongitude(41.0);

        assertThat(location.getName()).isEqualTo("New Current");
        assertThat(location.getLatitude()).isEqualTo(31.0);
        assertThat(location.getLongitude()).isEqualTo(41.0);
    }
}