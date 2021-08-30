package com.example.bookClub.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bookClub.MainActivity;
import com.example.bookClub.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class LoginScreen extends AppCompatActivity {

    String username;

    EditText usernameBox;
    EditText passwordBox;
    Button loginButton;

    SharedPreferences sharedPref;

    Button signUpButton;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        auth = FirebaseAuth.getInstance();
        /*auth.signInAnonymously().addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                Log.d("AUTH", auth.getCurrentUser().getUid());
            } else {
                Toast.makeText(LoginScreen.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                Log.d("AUTH", String.valueOf(task.getException()));
            }
        });*/

        auth.signInWithEmailAndPassword(getResources().getString(R.string.email), getResources().getString(R.string.password)).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                Log.d("AUTH", auth.getCurrentUser().getUid());
            } else {
                Toast.makeText(LoginScreen.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                Log.d("AUTH", String.valueOf(task.getException()));
            }
        });

        sharedPref = this.getSharedPreferences("sharedPref", Context.MODE_PRIVATE);
        username = sharedPref.getString("username", "");

        usernameBox = findViewById(R.id.id_login_usernameBox);
        passwordBox = findViewById(R.id.id_login_passwordBox);

        loginButton = findViewById(R.id.id_login_loginButton);
        loginButton.setOnClickListener(v -> {
            hideKeyboard();
            if (usernameBox.getText().length() > 0 && passwordBox.getText().length() > 0) {
                DatabaseReference databaseRef = FirebaseDatabase.getInstance(getResources().getString(R.string.databaseReference)).getReference();
                databaseRef.child(getResources().getString(R.string.ssid)).child(getResources().getString(R.string.users)).get().addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.d("firebase", task.getException().toString());
                    } else {
                        ArrayList<User> userList = new ArrayList<>();
                        Log.d("firebase", String.valueOf(task.getResult().getValue()));
                        Iterable<DataSnapshot> resultList = task.getResult().getChildren();
                        for (DataSnapshot snap : resultList) {
                            userList.add(snap.getValue(User.class));
                        }
                        Log.d("firebase", userList.toString());
                        checkCredentials(userList);
                    }
                });
            }
        });

        signUpButton = findViewById(R.id.id_login_signUpButton);
        signUpButton.setOnClickListener(v -> signUp());
    }

    public void checkCredentials (ArrayList<User> userList) {
        boolean contains = false;
        boolean containsUsername = false;
        for (User u : userList) {
            if (usernameBox.getText().toString().equals(u.getUsername())) {
                containsUsername = true;
                if (passwordBox.getText().toString().equals(u.getPassword())) {
                    contains = true;
                }
            }
        }
        if (contains) {
            username = usernameBox.getText().toString();
            try {
                login();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        else if (containsUsername) {
            Toast.makeText(LoginScreen.this, "Password Incorrect", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(LoginScreen.this, "No user found with the username " + usernameBox.getText(), Toast.LENGTH_LONG).show();
        }
    }

    public void login () throws ExecutionException, InterruptedException {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("username", username);
        editor.commit();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void hideKeyboard () {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void signUp () {
        Intent intent = new Intent(this, SignUpScreen.class);
        this.startActivity(intent);
    }
}