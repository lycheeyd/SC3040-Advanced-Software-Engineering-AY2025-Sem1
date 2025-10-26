package com.Services;


import com.model.Location;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class LocationTest {
    @Test
    void testGettersAndSetters() {
        Location location = new Location("Test", 10.0, 20.0);
        assertThat(location.getName()).isEqualTo("Test");
        assertThat(location.getLatitude()).isEqualTo(10.0);
        assertThat(location.getLongitude()).isEqualTo(20.0);

        location.setName("New Test");
        location.setLatitude(11.0);
        location.setLongitude(21.0);

        assertThat(location.getName()).isEqualTo("New Test");
        assertThat(location.getLatitude()).isEqualTo(11.0);
        assertThat(location.getLongitude()).isEqualTo(21.0);
    }
}