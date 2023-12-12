package com.example.plantngo.storage;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RealtimeDatabaseStorageTest {

    @Mock
    RealtimeDatabaseStorage storage;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetCurrentDate() {
        // Get the expected date
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String expectedDate = currentDate.format(formatter);

        // Mock the getCurrentDate method
        when(storage.getCurrentDate()).thenReturn(expectedDate);

        // Get the actual date from the method
        String actualDate = storage.getCurrentDate();

        // Check if the expected date is equal to the actual date
        assertEquals(expectedDate, actualDate);
    }
}
