package com.example.projectv2.View;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.example.projectv2.Controller.ImageController;
import com.example.projectv2.Utils.topBarUtils;
import com.example.projectv2.R;

/**
 * EventEditActivity allows users to edit the poster image for an event.
 * The user can select a new image from their device and upload it to Firebase Storage.
 */
public class EventEditActivity extends AppCompatActivity {

    private static final String TAG = "EventEditActivity";
    private static final int REQUEST_CODE_SELECT_IMAGE = 3;

    private ImageView eventPosterImageView;
    private Uri selectedImageUri;
    private String eventName;
    private ImageController imageController;
    private ProgressDialog progressDialog;

    /**
     * Called when the activity is created. Initializes the UI elements, sets the top bar, and loads the existing event poster.
     *
     * @param savedInstanceState if the activity is being re-initialized after previously being shut down, this Bundle contains the data it most recently supplied in {@link #onSaveInstanceState}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_edit_poster);

        topBarUtils.topBarSetup(this, "Edit event", View.INVISIBLE);

        // Initialize views
        eventPosterImageView = findViewById(R.id.create_event_pic_view);
        Button editPosterButton = findViewById(R.id.create_event_next_button);

        imageController = new ImageController();
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        // Retrieve event name from intent
        eventName = getIntent().getStringExtra("name"); // Match the key used in the sending activity


        if (eventName == null || eventName.isEmpty()) {
            Toast.makeText(this, "Invalid event name. Please try again.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Invalid or missing event name in intent");
            finish();
            return;
        }

        Log.d(TAG, "onCreate: Event Name = " + eventName);

        // Load existing event poster
        loadEventPoster();

        // Set up button to select a new image
        editPosterButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
        });
    }

    /**
     * Loads the current event poster from Firebase Storage.
     */
    /**
     * Loads the current event poster from Firebase Storage.
     * If no poster exists, allow the user to edit and upload a new image.
     */
    private void loadEventPoster() {
        progressDialog.setMessage("Checking for existing event poster...");
        progressDialog.show();

        imageController.retrieveImage(eventName, new ImageController.ImageRetrieveCallback() {

            // Callbacks for image retrieval
            @Override
            public void onRetrieveSuccess(String downloadUrl) {
                progressDialog.dismiss();
                // Poster exists, load it into the ImageView
                Glide.with(EventEditActivity.this)
                        .load(downloadUrl)
                        .placeholder(R.drawable.placeholder_event)
                        .into(eventPosterImageView);
                Log.d(TAG, "Event poster loaded successfully: " + downloadUrl);
            }

            // If the poster does not exist, allow the user to edit and upload a new image
            @Override
            public void onRetrieveFailure(Exception e) {
                progressDialog.dismiss();
                // Poster does not exist, allow user to edit and upload a new image
                Log.e(TAG, "No existing poster found for event: " + eventName, e);
                // Set the placeholder image or keep the ImageView empty
                eventPosterImageView.setImageResource(R.drawable.placeholder_event);
            }
        });
    }


    /**
     * Handles the result of the image selection.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();

            if (selectedImageUri != null) {
                Log.d(TAG, "Image selected: " + selectedImageUri);
                // Validate selected image
                if (isValidImage(selectedImageUri)) {
                    eventPosterImageView.setImageURI(selectedImageUri);
                    updateEventPoster();
                } else {
                    Toast.makeText(this, "Invalid image type. Only JPG and PNG are allowed.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Invalid image type: " + selectedImageUri);
                }
            }
        }
    }

    /**
     * Validates the selected image's type.
     */
    private boolean isValidImage(Uri imageUri) {
        ContentResolver resolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String type = mime.getExtensionFromMimeType(resolver.getType(imageUri));

        return "jpg".equalsIgnoreCase(type) || "jpeg".equalsIgnoreCase(type) || "png".equalsIgnoreCase(type);
    }

    /**
     * Updates the event poster in Firebase Storage.
     */
    private void updateEventPoster() {
        if (selectedImageUri == null) {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Updating event poster...");
        progressDialog.show();

        // Replace special characters and spaces with underscores
        String sanitizedEventName = eventName.trim()
                .replaceAll("[/\\-?!@#$%^Z&*()]+", "_") // Replace special characters with underscores
                .replaceAll("\\s+", "_");

        String filePath = "event_posters/event_posters_" + sanitizedEventName + ".jpg";

        // Upload the new image and overwrite the existing file
        imageController.uploadImage(selectedImageUri, filePath, new ImageController.ImageUploadCallback() {

            // Callbacks for image upload
            @Override
            public void onUploadSuccess(String downloadUrl) {
                progressDialog.dismiss();
                Toast.makeText(EventEditActivity.this, "Poster updated successfully", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Poster updated successfully: " + downloadUrl);

                // Refresh the image in the ImageView
                Glide.with(EventEditActivity.this)
                        .load(downloadUrl)
                        .placeholder(R.drawable.placeholder_event)
                        .signature(new ObjectKey(System.currentTimeMillis())) // Cache-busting
                        .into(eventPosterImageView);

                finish();
            }

            // If the upload fails, display an error message
            @Override
            public void onUploadFailure(Exception e) {
                progressDialog.dismiss();
                String errorMessage = "Failed to update poster. Please check your internet connection.";
                Toast.makeText(EventEditActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                Log.e(TAG, errorMessage, e);
            }
        });
    }

    /**
     * Called when the activity is paused. Dismisses the progress dialog if it is showing.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
