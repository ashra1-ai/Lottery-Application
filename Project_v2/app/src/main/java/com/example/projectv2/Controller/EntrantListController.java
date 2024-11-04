package com.example.projectv2.Controller;

import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntrantListController {

    private FirebaseFirestore db;

    public EntrantListController() {
        db = FirebaseFirestore.getInstance();
    }

    public void addEntrantListField(String eventId) {
        // Create empty arrays for each subfield
        List<String> emptyList = new ArrayList<>(); // Modifiable lists

        // Create a map to represent the entrantList field with empty arrays
        Map<String, Object> entrantListMap = new HashMap<>();
        entrantListMap.put("Attendees", emptyList);
        entrantListMap.put("Unlucky", emptyList);
        entrantListMap.put("Declined", emptyList);
        entrantListMap.put("Removed", emptyList);
        entrantListMap.put("EntrantList", emptyList);

        // Add the map to the Firestore document
        db.collection("events").document(eventId)
                .update("entrantList", entrantListMap)
                .addOnSuccessListener(aVoid -> {
                    System.out.println("Entrant list field with empty arrays added successfully!");
                })
                .addOnFailureListener(e -> {
                    System.err.println("Error adding entrant list field: " + e.getMessage());
                });
    }
}