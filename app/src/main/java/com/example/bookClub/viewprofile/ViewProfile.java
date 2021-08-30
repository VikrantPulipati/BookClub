package com.example.bookClub.viewprofile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bookClub.R;
import com.example.bookClub.browsebooks.Book;
import com.example.bookClub.browsebooks.BrowseBooks;
import com.example.bookClub.browsebooks.ReadStatus;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ViewProfile extends AppCompatActivity {

    TextView usernameHeader;
    ImageView pfp;
    TextView booksRead;

    String loggedInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        loggedInUser = ViewProfile.this.getSharedPreferences("sharedPref", Context.MODE_PRIVATE).getString("username", "");

        usernameHeader = findViewById(R.id.id_viewProfile_usernameHeader);
        usernameHeader.setText(loggedInUser);

        pfp = findViewById(R.id.id_viewProfile_pfp);

        booksRead = findViewById(R.id.id_viewProfile_booksRead);
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
            Query readStatusQuery = FirebaseDatabase.getInstance(getResources().getString(R.string.databaseReference)).getReference().child(getResources().getString(R.string.ssid)).child(getResources().getString(R.string.userReadStatus)).orderByChild("username").equalTo(loggedInUser);
            readStatusQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
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
                    booksRead.setText("Total Books Read: " + readList.size());
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {
                    Log.d("TAGERROR", error.toException().toString());
                }
            });
        });
    }
}