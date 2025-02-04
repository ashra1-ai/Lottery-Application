package com.example.projectv2;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.projectv2.View.YourEventsFragment;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

/**
 * Test class for {@link YourEventsFragment}.
 * This class uses AndroidJUnit4 for testing and verifies the correct behavior
 * and initialization of the YourEventsFragment, including its layout manager,
 * event controller, adapter, and event list.
 */
@RunWith(AndroidJUnit4.class)
public class YourEventsFragmentTest {

    private FragmentScenario<YourEventsFragment> scenario;

    /**
     * Sets up the test environment before each test.
     * Initializes Mockito annotations and launches the {@link YourEventsFragment}.
     */
    @Before
    public void setup() {
        // Initialize Mockito annotations
        MockitoAnnotations.initMocks(this);

        // Launch the fragment
        scenario = FragmentScenario.launchInContainer(YourEventsFragment.class);
    }

    /**
     * Verifies that the fragment and its view are successfully created.
     */
    @Test
    public void testFragmentCreation() {
        scenario.onFragment(fragment -> {
            assertNotNull("Fragment should not be null", fragment);
            assertNotNull("Fragment view should not be null", fragment.getView());
        });
    }

    /**
     * Verifies that the RecyclerView in the fragment has a LinearLayoutManager.
     */
    @Test
    public void testRecyclerViewLayoutManager() {
        scenario.onFragment(fragment -> {
            RecyclerView recyclerView = fragment.getView().findViewById(R.id.recyclerViewYourEvents);

            assertNotNull("RecyclerView should have a layout manager", recyclerView.getLayoutManager());

            assertTrue("Layout manager should be LinearLayoutManager",
                    recyclerView.getLayoutManager() instanceof LinearLayoutManager);
        });
    }

    /**
     * Verifies that the event controller is properly initialized in the fragment.
     */
    @Test
    public void testEventControllerInitialization() {
        scenario.onFragment(fragment -> {
            // Use reflection to access private eventController
            try {
                java.lang.reflect.Field eventControllerField =
                        YourEventsFragment.class.getDeclaredField("eventController");
                eventControllerField.setAccessible(true);
                Object eventController = eventControllerField.get(fragment);
                assertNotNull("EventController should be initialized", eventController);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                fail("Could not access eventController: " + e.getMessage());
            }
        });
    }

    /**
     * Verifies that the adapter is properly initialized in the fragment.
     */
    @Test
    public void testAdapterInitialization() {
        scenario.onFragment(fragment -> {
            // Use reflection to access private adapter
            try {
                java.lang.reflect.Field adapterField =
                        YourEventsFragment.class.getDeclaredField("adapter");
                adapterField.setAccessible(true);
                Object adapter = adapterField.get(fragment);
                assertNotNull("Adapter should be initialized", adapter);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                fail("Could not access adapter: " + e.getMessage());
            }
        });
    }

    /**
     * Verifies that the event list is properly initialized in the fragment.
     */
    @Test
    public void testEventListInitialization() {
        scenario.onFragment(fragment -> {
            // Use reflection to access private eventList
            try {
                java.lang.reflect.Field eventListField =
                        YourEventsFragment.class.getDeclaredField("eventList");
                eventListField.setAccessible(true);
                ArrayList<?> eventList = (ArrayList<?>) eventListField.get(fragment);
                assertNotNull("Event list should be initialized", eventList);
                assertTrue("Event list should be empty initially", eventList.isEmpty());
            } catch (NoSuchFieldException | IllegalAccessException e) {
                fail("Could not access eventList: " + e.getMessage());
            }
        });
    }

    /**
     * Verifies that the fetchEvents method exists in the fragment.
     */
    @Test
    public void testFetchEventsMethodCall() {
        scenario.onFragment(fragment -> {
            try {
                java.lang.reflect.Method fetchEventsMethod =
                        YourEventsFragment.class.getDeclaredMethod("fetchEvents");
                fetchEventsMethod.setAccessible(true);
                // You might want to add more sophisticated verification here
                assertNotNull("fetchEvents method should exist", fetchEventsMethod);
            } catch (NoSuchMethodException e) {
                fail("fetchEvents method not found: " + e.getMessage());
            }
        });
    }
}
