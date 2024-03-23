package com.fyp2024.parentalcontrol.androidapp.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class BrowserHistoryMonitor {

    private static final String TAG = "BrowserHistoryMonitor";
    private Context context;
    private FirebaseFirestore db;

    public BrowserHistoryMonitor(Context context) {
        this.context = context;
        db = FirebaseFirestore.getInstance();
    }

    public void monitorBrowserHistory() {
        // Assuming you have a list of URLs to monitor
        List<String> urls = new ArrayList<>();
        // Add URLs to the list

        // Log or send to Firebase Firestore
        for (String url : urls) {
            Log.d(TAG, "URL: " + url);
            sendToFirebase(url);
        }
    }

    private void sendToFirebase(String url) {
        // Assuming you have a "history" collection in Firestore
        db.collection("history").add(new HistoryItem(url))
                .addOnSuccessListener(documentReference -> Log.d(TAG, "History item added to Firestore"))
                .addOnFailureListener(e -> Log.e(TAG, "Error adding history item to Firestore", e));
    }
}

// Model class for history item
class HistoryItem {
    private String url;

    public HistoryItem(String url) {
        this.url = url;
    }

    // Getters and setters
}
