package com.example.plantngo.main;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;

import com.example.plantngo.R;

import org.junit.Before;
import org.junit.Test;

import androidx.test.core.app.ActivityScenario;

public class MainActivityTest {

    private ActivityScenario<MainActivity> scenario;

    @Before
    public void setUp() {
        scenario = ActivityScenario.launch(MainActivity.class);
    }

    @Test
    public void testBottomNavigation() {
        // Test home icon
        Espresso.onView(ViewMatchers.withId(R.id.home))
                .perform(ViewActions.click());
        Espresso.onView(ViewMatchers.withId(R.id.fragment_home))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        // Test camera icon
        Espresso.onView(ViewMatchers.withId(R.id.camera))
                .perform(ViewActions.click());
        Espresso.onView(ViewMatchers.withId(R.id.fragment_camera))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        // Test profile icon
        Espresso.onView(ViewMatchers.withId(R.id.profile))
                .perform(ViewActions.click());
        Espresso.onView(ViewMatchers.withId(R.id.fragment_profile))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }
}
