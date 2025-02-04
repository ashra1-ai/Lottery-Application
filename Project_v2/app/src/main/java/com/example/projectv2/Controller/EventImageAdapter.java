package com.example.projectv2.Controller;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.projectv2.R;

import java.util.List;

/**
 * Adapter for displaying a list of event images in a RecyclerView. Each item displays an image
 * and a delete button to remove the image from the event.
 */
public class EventImageAdapter extends RecyclerView.Adapter<EventImageAdapter.ImageViewHolder> {

    private static final String TAG = "EventImageAdapter";
    private final Context context;
    private final List<String> imageFilenames; // List of filenames
    private final ImageController imageController;

    /**
     * Constructs an EventImageAdapter with the specified context and list of image filenames.
     *
     * @param context        the context in which the adapter is operating
     * @param imageFilenames the list of image filenames to display
     */
    public EventImageAdapter(Context context, List<String> imageFilenames) {
        this.context = context;
        this.imageFilenames = imageFilenames; // Assume placeholders are already filtered
        this.imageController = new ImageController(); // Initialize ImageController
    }

    /**
     * Inflates the layout for each image item in the RecyclerView.
     *
     * @param parent   the ViewGroup into which the new view will be added
     * @param viewType the view type of the new view
     * @return a new ImageViewHolder that holds the view for each image item
     */
    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_image_list_object, parent, false);
        return new ImageViewHolder(view);
    }

    /**
     * Binds data to the view elements of each item in the RecyclerView.
     *
     * @param holder   the ImageViewHolder containing view elements to bind data to
     * @param position the position of the item in the RecyclerView
     */
    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String filename = imageFilenames.get(position);

        // Check if context is a valid Activity and not destroyed before loading images
        if (context instanceof Activity && ((Activity) context).isDestroyed()) {
            Log.w(TAG, "Activity is destroyed. Skipping image load.");
            return;
        }

        // Fetch and display the image
        imageController.getDownloadUrl(filename, new ImageController.ImageRetrieveCallback() {
            @Override
            public void onRetrieveSuccess(String downloadUrl) {
                Glide.with(context)
                        .load(downloadUrl)
                        .placeholder(R.drawable.placeholder_event)
                        .into(holder.imageView);
            }

            @Override
            public void onRetrieveFailure(Exception e) {
                Log.e(TAG, "Failed to fetch image: " + filename, e);
                holder.imageView.setImageResource(R.drawable.placeholder_event); // Set placeholder
            }
        });

        // Handle delete button click
        holder.deleteButton.setOnClickListener(v -> {
            Log.d(TAG, "Attempting to delete image: " + filename);

            // Delete the image
            imageController.deleteImage(filename, new ImageController.ImageDeleteCallback() {
                @Override
                public void onDeleteSuccess() {
                    Log.d(TAG, "Image deleted: " + filename);

                    // Update the UI
                    imageFilenames.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, imageFilenames.size());

                    Toast.makeText(context, "Image deleted successfully.", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onDeleteFailure(Exception e) {
                    Log.e(TAG, "Failed to delete image: " + filename, e);
                    Toast.makeText(context, "Failed to delete image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    /**
     * Returns the total number of images in the list.
     *
     * @return the total number of images
     */
    @Override
    public int getItemCount() {
        return imageFilenames.size();
    }

    /**
     * ViewHolder class for each image item in the RecyclerView.
     * Holds references to the UI elements within each image item.
     */
    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageView deleteButton;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }
}
