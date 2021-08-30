package com.example.bookClub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.bookClub.browsebooks.BrowseBooks;
import com.example.bookClub.viewprofile.ViewProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Context context = this;

    Button browseBooks;
    Button viewProfile;
    TextView welcomeText;
    TextView booksRead;

    SharedPreferences sharedPrefs;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("AUTH1", FirebaseAuth.getInstance().getCurrentUser().getUid());

        sharedPrefs = this.getSharedPreferences("sharedPref", Context.MODE_PRIVATE);
        username = sharedPrefs.getString("username", "");

        welcomeText = findViewById(R.id.id_welcomeText);
        welcomeText.setText("Welcome " + username + "!");

        booksRead = findViewById(R.id.id_booksRead);
        ArrayList<Long> bookList = new ArrayList<>();
        ArrayList<Long> readList = new ArrayList<>();
        DatabaseReference databaseRef = FirebaseDatabase.getInstance(getResources().getString(R.string.databaseReference)).getReference();
        databaseRef.child(getResources().getString(R.string.ssid)).child(getResources().getString(R.string.bookList)).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.d("viewProfileReadList", task.getException().toString());
            } else {
                Iterable<DataSnapshot> resultList = task.getResult().getChildren();
                for (DataSnapshot snap : resultList) {
                    bookList.add((Long)snap.child("bookId").getValue());
                }
            }
            Query readStatusQuery = FirebaseDatabase.getInstance(getResources().getString(R.string.databaseReference)).getReference().child(getResources().getString(R.string.ssid)).child(getResources().getString(R.string.userReadStatus)).orderByChild("username").equalTo(username);
            readStatusQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    readList.clear();
                    ArrayList<Long> readStatusList = new ArrayList<>();
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        readStatusList.add((Long)snap.child("bookId").getValue());
                    }

                    for (Long b : bookList) {
                        if (readStatusList.size() != 0) {
                            for (Long i : readStatusList) {
                                if (b.equals(i)) {
                                    readList.add(b);
                                    break;
                                }
                            }
                        }
                    }
                    Log.d("TAGLIST", readList.toString());
                    booksRead.setText("Total Books Read: " + readList.size());
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {
                    Log.d("TAGERROR", error.toException().toString());
                }
            });
        });

        browseBooks = findViewById(R.id.id_browseBooks);
        browseBooks.setOnClickListener(v -> {
            Intent intent = new Intent(context, BrowseBooks.class);
            startActivity(intent);
        });

        viewProfile = findViewById(R.id.id_viewProfile);
        viewProfile.setOnClickListener(v -> {
            Intent intent = new Intent(context, ViewProfile.class);
            startActivity(intent);
        });
    }
}