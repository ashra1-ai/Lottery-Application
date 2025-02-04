package com.example.projectv2.Controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.projectv2.Model.Event;
import com.example.projectv2.R;
import com.example.projectv2.View.EventLandingPageOrganizerActivity;
import com.example.projectv2.View.EventLandingPageUserActivity;

import java.util.List;

/**
 * Adapter class for displaying a list of available events in a RecyclerView on the home screen.
 * Handles loading event data, including images, and navigating to the appropriate event detail page based on the user's role.
 */
public class AvailableEventsAdapter extends RecyclerView.Adapter<AvailableEventsAdapter.ViewHolder> {

    private static final String TAG = "AvailableEventsAdapter";
    private final List<Event> eventList;
    private final Context context;
    private final String userRole;

    /**
     * Constructor for initializing the AvailableEventsAdapter with the context and list of events.
     *
     * @param context   the context in which the RecyclerView is being used
     * @param eventList the list of events to display
     */
    public AvailableEventsAdapter(Context context, List<Event> eventList) {
        this.context = context;
        this.eventList = eventList;

        SharedPreferences preferences = context.getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
        this.userRole = preferences.getString("userRole", "user"); // Default to "user" if role not found
    }

    /**
     * Inflates the layout for each RecyclerView item and creates a ViewHolder.
     *
     * @param parent   the parent ViewGroup
     * @param viewType the view type of the new View
     * @return a new ViewHolder instance for the inflated item layout
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.homescreen_available_events_list_object, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Binds the data of an event to the corresponding views in the ViewHolder.
     * Also handles loading the event's image and setting up a click listener for navigation.
     *
     * @param holder   the ViewHolder for the current item
     * @param position the position of the item in the data list
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = eventList.get(position);

        holder.eventName.setText(event.getName());
        holder.eventDate.setText(event.getDeadline());
        holder.eventPrice.setText(event.getTicketPrice() != null && !event.getTicketPrice().equals("0") ? "$" + event.getTicketPrice() : "Free");
        holder.eventDescription.setText(event.getDetail());

        // Fetch and display the event image
        ImageController imageController = new ImageController();
        imageController.retrieveImage(event.getName(), new ImageController.ImageRetrieveCallback() {
            @Override
            public void onRetrieveSuccess(String downloadUrl) {
                if (context instanceof FragmentActivity) {
                    FragmentActivity activity = (FragmentActivity) context;
                    if (!activity.isDestroyed() && !activity.isFinishing()) {
                        Glide.with(context)
                                .load(downloadUrl)
                                .placeholder(R.drawable.placeholder_event) // Placeholder while loading
                                .error(R.drawable.placeholder_event) // Placeholder if loading fails
                                .centerCrop()
                                .into(holder.backgroundImage); // Set the image in the ImageView
                    } else {
                        Log.w(TAG, "Activity is destroyed or finishing. Skipping image load.");
                    }
                }
            }

            @Override
            public void onRetrieveFailure(Exception e) {
                Log.e(TAG, "Failed to retrieve image for event: " + event.getName(), e);
                holder.backgroundImage.setImageResource(R.drawable.placeholder_event); // Fallback to placeholder
            }
        });

        // Set up the click listener for navigation based on user role
        holder.itemView.setOnClickListener(v -> {
            Intent intent = "organizer".equals(userRole) ?
                    new Intent(context, EventLandingPageOrganizerActivity.class) :
                    new Intent(context, EventLandingPageUserActivity.class);

            intent.putExtra("name", event.getName());
            intent.putExtra("details", event.getDetail());
            intent.putExtra("rules", event.getRules());
            intent.putExtra("deadline", event.getDeadline());
            intent.putExtra("startDate", event.getStartDate());
            intent.putExtra("price", event.getTicketPrice());
            intent.putExtra("eventID", event.getEventID());
            @SuppressLint("HardwareIds")
            String deviceID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            intent.putExtra("user", deviceID);
            intent.putExtra("owner", event.getOwner());
            context.startActivity(intent);
        });
    }

    /**
     * Returns the total number of events in the data list.
     *
     * @return the size of the event list
     */
    @Override
    public int getItemCount() {
        return eventList.size();
    }

    /**
     * Updates the event list in the adapter and refreshes the RecyclerView.
     *
     * @param newEvents the new list of events to display
     */
    public void updateEventList(List<Event> newEvents) {
        this.eventList.clear();
        this.eventList.addAll(newEvents);
        notifyDataSetChanged();
    }

    /**
     * ViewHolder class that holds the views for displaying an event's details in the RecyclerView.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView eventName, eventDate, eventPrice, eventDescription;
        public ImageView backgroundImage;

        /**
         * Constructor for initializing the ViewHolder with the corresponding views.
         *
         * @param view the item view for the ViewHolder
         */
        public ViewHolder(View view) {
            super(view);
            eventName = view.findViewById(R.id.available_event_name_text);
            eventDate = view.findViewById(R.id.available_event_date_text);
            eventPrice = view.findViewById(R.id.available_event_price_text);
            eventDescription = view.findViewById(R.id.available_event_description_text);
            backgroundImage = view.findViewById(R.id.backgroundImage);
        }
    }
}
