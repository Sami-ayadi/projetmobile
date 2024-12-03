package com.example.projetmobile;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class InfoActivity extends AppCompatActivity {

    private Button goToPendingElectionsPage, goToEditPage, goToResultsPage, logout;
    private CardView cardView;
    private TextView messageText;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference voterRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        initializeViews();
        setupFirebase();

        // Logout button logic
        logout.setOnClickListener(v -> {
            firebaseAuth.signOut();
            navigateToActivity(LoginActivity.class);
        });

        // Fetch voter data and update UI
        fetchVoterData();

        // Navigation button listeners
        goToPendingElectionsPage.setOnClickListener(v -> navigateToActivity(ShowAllCurrentElections.class));
        goToEditPage.setOnClickListener(v -> navigateToActivity(MainActivity.class));
        goToResultsPage.setOnClickListener(v -> navigateToActivity(ResultsActivity.class));
    }

    /**
     * Initialize UI components.
     */
    private void initializeViews() {
        goToPendingElectionsPage = findViewById(R.id.goToPendingElectionsActivity);
        goToEditPage = findViewById(R.id.goToMainPage);
        goToResultsPage = findViewById(R.id.vGoToResultsPage);
        logout = findViewById(R.id.logout);
        messageText = findViewById(R.id.messageTxt);
        cardView = findViewById(R.id.reviewTextVoter);
    }

    /**
     * Setup Firebase references.
     */
    private void setupFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser curUser = firebaseAuth.getCurrentUser();
        if (curUser != null) {
            voterRef = FirebaseDatabase.getInstance()
                    .getReference("user-data")
                    .child(curUser.getUid());
        } else {
            // Redirect to login if user is null
            navigateToActivity(LoginActivity.class);
            finish();
        }
    }

    /**
     * Fetch voter data from Firebase and update UI.
     */
    private void fetchVoterData() {
        if (voterRef != null) {
            voterRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String isVerified = snapshot.child("isVerified").getValue(String.class);
                    String message = snapshot.child("message").getValue(String.class);

                    if ("true".equalsIgnoreCase(isVerified)) {
                        messageText.setText("Profile verified. Authorized for voting.");
                        cardView.setCardBackgroundColor(Color.parseColor("#4CAF50"));
                    } else {
                        if (message == null || message.isEmpty()) {
                            message = "Profile is under review. Not yet authorized for voting.";
                        } else {
                            message = "Status is Declined. " + message;
                        }
                        messageText.setText(message);
                        cardView.setCardBackgroundColor(Color.parseColor("#F44336")); // Red for declined/review status
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    messageText.setText("Failed to fetch data. Please try again later.");
                }
            });
        }
    }

    /**
     * Navigate to the specified activity.
     *
     * @param targetActivity The target activity class.
     */
    private void navigateToActivity(Class<?> targetActivity) {
        Intent intent = new Intent(InfoActivity.this, targetActivity);
        startActivity(intent);
    }
}
