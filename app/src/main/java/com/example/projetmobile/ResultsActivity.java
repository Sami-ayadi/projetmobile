package com.example.projetmobile;


import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ResultsActivity extends AppCompatActivity {
    private LinearLayout showAllDoneElections;
    private DatabaseReference electionDbRef, electionCandidatesRef;
    private ArrayList<String> candidates = new ArrayList<>();
    private Button goBackToInfoPageResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#304567"));
        colors.add(Color.parseColor("#309967"));
        colors.add(Color.parseColor("#476567"));
        colors.add(Color.parseColor("#890567"));
        colors.add(Color.parseColor("#a35567"));
        colors.add(Color.parseColor("#ff5f67"));
        colors.add(Color.parseColor("#3ca567"));

        goBackToInfoPageResults = findViewById(R.id.backToInfoPageFromResults);
        goBackToInfoPageResults.setOnClickListener(v -> {
            if (LoginActivity.curUserType.equals("ec")) {
                startActivity(new Intent(getApplicationContext(), ECInfoActivity.class));
            } else if (LoginActivity.curUserType.equals("eca")) {
                startActivity(new Intent(getApplicationContext(), ECAMainActivity.class));
            } else if (LoginActivity.curUserType.equals("voter")) {
                startActivity(new Intent(getApplicationContext(), InfoActivity.class));
            }
        });

        showAllDoneElections = findViewById(R.id.allDoneElections);
        electionDbRef = FirebaseDatabase.getInstance().getReference();
        electionCandidatesRef = electionDbRef.child("election-candidates");

        electionCandidatesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot candidateSnapshot : snapshot.getChildren()) {
                    candidates.add(candidateSnapshot.child("pName").getValue(String.class));
                }

                DatabaseReference electionDbRef2 = electionDbRef.child("elections").getRef();
                electionDbRef2.addValueEventListener(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot electionSnapshot : snapshot.getChildren()) {
                            if (electionSnapshot.child("isDone").getValue(Boolean.class)) {
                                // Create a card for each election
                                CardView newCard = new CardView(getApplicationContext());
                                LinearLayout newLinearLayout = new LinearLayout(getApplicationContext());
                                newLinearLayout.setOrientation(LinearLayout.VERTICAL);

                                // Election Name Header
                                TextView electionNameTxt = new TextView(getApplicationContext());
                                electionNameTxt.setGravity(Gravity.CENTER_HORIZONTAL);
                                String electionName = electionSnapshot.child("electionName").getValue(String.class);
                                electionNameTxt.setText("Election: " + electionName);
                                electionNameTxt.setTextSize(24);
                                electionNameTxt.setTextColor(Color.BLACK);
                                newLinearLayout.addView(electionNameTxt);

                                // Create a TableLayout to display results
                                TableLayout tableLayout = new TableLayout(getApplicationContext());
                                tableLayout.setLayoutParams(new TableLayout.LayoutParams(
                                        TableLayout.LayoutParams.MATCH_PARENT,
                                        TableLayout.LayoutParams.WRAP_CONTENT));
                                tableLayout.setStretchAllColumns(true);

                                // Table Header Row
                                TableRow headerRow = new TableRow(getApplicationContext());
                                TextView candidateHeader = new TextView(getApplicationContext());
                                candidateHeader.setText("Candidate");
                                candidateHeader.setTextSize(18);
                                candidateHeader.setGravity(Gravity.CENTER);
                                candidateHeader.setPadding(8, 8, 8, 8);
                                candidateHeader.setTextColor(Color.BLACK);

                                TextView votesHeader = new TextView(getApplicationContext());
                                votesHeader.setText("Votes");
                                votesHeader.setTextSize(18);
                                votesHeader.setGravity(Gravity.CENTER);
                                votesHeader.setPadding(8, 8, 8, 8);
                                votesHeader.setTextColor(Color.BLACK);

                                headerRow.addView(candidateHeader);
                                headerRow.addView(votesHeader);
                                tableLayout.addView(headerRow);

                                // Populate Table Rows with Candidate Results
                                for (String candidate : candidates) {
                                    TableRow tableRow = new TableRow(getApplicationContext());

                                    TextView candidateName = new TextView(getApplicationContext());
                                    candidateName.setText(candidate);
                                    candidateName.setTextSize(16);
                                    candidateName.setGravity(Gravity.CENTER);
                                    candidateName.setPadding(8, 8, 8, 8);
                                    candidateName.setTextColor(Color.DKGRAY);

                                    TextView candidateVotes = new TextView(getApplicationContext());
                                    Integer votes = electionSnapshot.child("candidate-results").child(candidate).getValue(Integer.class);
                                    candidateVotes.setText(votes != null ? String.valueOf(votes) : "0");
                                    candidateVotes.setTextSize(16);
                                    candidateVotes.setGravity(Gravity.CENTER);
                                    candidateVotes.setPadding(8, 8, 8, 8);
                                    candidateVotes.setTextColor(Color.DKGRAY);

                                    tableRow.addView(candidateName);
                                    tableRow.addView(candidateVotes);
                                    tableLayout.addView(tableRow);
                                }

                                // Add TableLayout to Card
                                newLinearLayout.addView(tableLayout);

                                // Add CardView to Layout
                                newCard.addView(newLinearLayout);
                                showAllDoneElections.addView(newCard);
                            }
                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
