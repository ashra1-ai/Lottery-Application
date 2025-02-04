package com.example.projectv2.View;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectv2.Controller.AvailableEventsAdapter;
import com.example.projectv2.Controller.EventController;
import com.example.projectv2.Model.Event;
import com.example.projectv2.R;

import java.util.ArrayList;

/**
 * Fragment for displaying available events.
 */
public class AvailableEventsFragment extends Fragment {

    private EventController eventController;
    public AvailableEventsAdapter adapter;

    /**
     * Required empty public constructor
     */
    public AvailableEventsFragment() {
        // Required empty public constructor
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to.
     *                           The fragment should not add the view itself, but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_available_events, container, false);

        // Initialize RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewAvailableEvents);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AvailableEventsAdapter(getContext(), new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // Initialize EventController
        eventController = new EventController(getActivity());

        // Fetch all events
        refreshEventsFromFirestore();

        return view;
    }

    /**
     * Refreshes the list of available events from Firestore.
     */
    public void refreshEventsFromFirestore() {
        Log.d("AvailableEventsFragment", "Refreshing available events...");
        eventController.fetchEvents(new EventController.EventCallback() {
            @Override
            public void onEventListLoaded(ArrayList<Event> events) {
                Log.d("AvailableEventsFragment", "Fetched " + events.size() + " events.");
                adapter.updateEventList(events);
            }

            @Override
            public void onEventCreated(String eventId) {
                // Not used for refreshing
            }

            @Override
            public void onError(Exception e) {
                Log.e("AvailableEventsFragment", "Error refreshing events", e);
            }
        });
    }
}