package com.example.plantngo.authentication;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;

import com.example.plantngo.R;

import org.junit.Before;
import org.junit.Test;

public class ProfileFragmentTest {

    private FragmentScenario<ProfileFragment> scenario;

    @Before
    public void setUp() {
        scenario = FragmentScenario.launchInContainer(ProfileFragment.class);
    }

    @Test
    public void testSettingsButton() {
        Espresso.onView(ViewMatchers.withId(R.id.settings_button))
                .perform(ViewActions.click());

        Espresso.onView(ViewMatchers.withId(R.id.fragment_settings))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }
}
