package com.example.bookClub.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bookClub.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpScreen extends AppCompatActivity {

    EditText usernameBox;
    EditText passwordBox;

    Button signUpButton;
    Button logInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_screen);

        usernameBox = findViewById(R.id.id_signUp_usernameBox);
        passwordBox = findViewById(R.id.id_signUp_passwordBox);

        signUpButton = findViewById(R.id.id_signUp_signUpButton);
        signUpButton.setOnClickListener(v -> {
            hideKeyboard();
            if (usernameBox.getText().length() > 0 && passwordBox.getText().length() > 0) {
                DatabaseReference databaseRef = FirebaseDatabase.getInstance(getResources().getString(R.string.databaseReference)).getReference();
                databaseRef.child(getResources().getString(R.string.ssid)).child(getResources().getString(R.string.users)).child(usernameBox.getText().toString()).get().addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.d("firebase", task.getException().toString());
                    } else {
                        Log.d("firebase", task.getResult().toString());
                        if (task.getResult().getValue() == null) {
                            User newUser = new User(usernameBox.getText().toString(), passwordBox.getText().toString());
                            databaseRef.child(getResources().getString(R.string.ssid)).child(getResources().getString(R.string.users)).child(newUser.getUsername()).setValue(newUser);
                            Toast.makeText(SignUpScreen.this, "Signing Up Successful!", Toast.LENGTH_LONG).show();
                            logIn();
                        } else {
                            Toast.makeText(SignUpScreen.this, "This username is taken", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        logInButton = findViewById(R.id.id_signUp_logInButton);
        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logIn();
            }
        });
    }

    public void logIn () {
        Intent intent = new Intent(this, LoginScreen.class);
        this.startActivity(intent);
    }

    public void hideKeyboard () {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}