package com.example.bookClub.browsebooks;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookClub.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class InspectBook extends AppCompatActivity implements SubmitLocalizationDialogFragment.SubmitLocalizationDialogListener, SubmitIntensityDialogFragment.SubmitIntensityDialogListener {

    String loggedInUser;

    Book book;

    ImageView coverImage;
    TextView title;
    TextView author;
    TextView series;
    TextView intensity;
    ImageButton menuButton;
    ImageView readIcon;
    TextView category;
    TextView timestamp;

    TextView french;
    TextView german;
    TextView polish;
    TextView russian;
    TextView spanish;
    Button submitLocalization;
    Button submitIntensity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspect_book);

        loggedInUser = InspectBook.this.getSharedPreferences("sharedPref", Context.MODE_PRIVATE).getString("username", "");

        //book = getIntent().getParcelableExtra("book");
        FirebaseDatabase.getInstance(getResources().getString(R.string.databaseReference)).getReference().child(getResources().getString(R.string.ssid)).child(getResources().getString(R.string.bookList)).child(String.valueOf(getIntent().getLongExtra("bookId", 0))).get().addOnCompleteListener(task -> {
            book = task.getResult().getValue(Book.class);
            book.setReadStatus(getIntent().getBooleanExtra("readStatus", false));
            book.setBookImage(getIntent().getParcelableExtra("coverImage"));

            coverImage = findViewById(R.id.id_inspectBook_coverImage);
            coverImage.setImageBitmap(book.getBookImage());

            title = findViewById(R.id.id_inspectBook_title);
            title.setText(book.getBookTitle());

            author = findViewById(R.id.id_inspectBook_author);
            author.setText(book.getAuthorName());

            series = findViewById(R.id.id_inspectBook_series);
            if (book.getSeriesName().length() > 0) {
                series.setText(book.getSeries());
            } else {
                series.setVisibility(View.GONE);
            }

            intensity = findViewById(R.id.id_inspectBook_intensity);
            intensity.setText("Intensity: " + book.getBookIntensity());

            readIcon = findViewById(R.id.id_inspectBook_readIcon);
            if (book.getReadStatus()) {
                readIcon.setVisibility(View.VISIBLE);
            }

            category = findViewById(R.id.id_inspectBook_category);
            category.setText("Category: " + book.getCategory());

            timestamp = findViewById(R.id.id_inspectBook_timestamp);
            if (book.getReadStatus()) {
                Query readStatusQuery = FirebaseDatabase.getInstance(getResources().getString(R.string.databaseReference)).getReference().child(getResources().getString(R.string.ssid)).child(getResources().getString(R.string.userReadStatus)).orderByChild("username").equalTo(loggedInUser);
                readStatusQuery.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.d("firebase", task.getException().toString());
                        } else {
                            Iterable<DataSnapshot> resultList = task.getResult().getChildren();
                            for (DataSnapshot snap : resultList) {
                                if (snap.child("bookId").getValue().equals(book.getBookId())) {
                                    String ts = snap.getKey();
                                    SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss:SSS z");
                                    Date date = new Date();
                                    try {
                                        date = df.parse(ts);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    SimpleDateFormat ndf = new SimpleDateFormat("d MMMM yyyy");
                                    timestamp.setText("Read On: " + ndf.format(date));
                                    break;
                                }
                            }
                        }
                    }
                });
                timestamp.setVisibility(View.VISIBLE);
            }

            french = findViewById(R.id.id_inspectBook_french);
            french.setText("French: " + book.getFrenchTitle());
            german = findViewById(R.id.id_inspectBook_german);
            german.setText("German: " + book.getGermanTitle());
            polish = findViewById(R.id.id_inspectBook_polish);
            polish.setText("Polish: " + book.getPolishTitle());
            russian = findViewById(R.id.id_inspectBook_russian);
            russian.setText("Russian: " + book.getRussianTitle());
            spanish = findViewById(R.id.id_inspectBook_spanish);
            spanish.setText("Spanish: " + book.getSpanishTitle());

            submitLocalization = findViewById(R.id.id_inspectBook_submitLocalization);
            submitLocalization.setOnClickListener(v -> {
                DialogFragment newFragment = new SubmitLocalizationDialogFragment();
                newFragment.show(getSupportFragmentManager(), "localization");
            });

            submitIntensity = findViewById(R.id.id_inspectBook_submitIntensity);
            submitIntensity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogFragment newFragment = new SubmitIntensityDialogFragment();
                    newFragment.show(getSupportFragmentManager(), "intensity");
                }
            });
        });

        menuButton = findViewById(R.id.id_inspectBook_menuButton);
        menuButton.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(InspectBook.this, v);
            MenuInflater inflater = new MenuInflater(InspectBook.this);
            inflater.inflate(R.menu.book_list_item_menu, popupMenu.getMenu());
            MenuItem markAsRead = popupMenu.getMenu().findItem(R.id.id_bookList_item_menu_markRead);
            if (book.getReadStatus()) {
                markAsRead.setVisible(false);
                //markAsRead.setTitle("Mark as Unread");
            }
            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.id_bookList_item_menu_markRead) {
                    if (book.getReadStatus()) {
                        markAsRead.setTitle("Mark as Unread");
                        Query readStatusQuery = FirebaseDatabase.getInstance(getResources().getString(R.string.databaseReference)).getReference().child(getResources().getString(R.string.ssid)).child(getResources().getString(R.string.userReadStatus)).orderByChild("username").equalTo(loggedInUser);
                        readStatusQuery.get().addOnCompleteListener(task -> {
                            if (!task.isSuccessful()) {
                                Log.d("firebase", task.getException().toString());
                            } else {
                                Iterable<DataSnapshot> resultList = task.getResult().getChildren();
                                for (DataSnapshot snap : resultList) {
                                    if (snap.child("bookId").getValue().equals(book.getBookId())) {
                                        FirebaseDatabase.getInstance(getResources().getString(R.string.databaseReference)).getReference().child(getResources().getString(R.string.ssid)).child(getResources().getString(R.string.userReadStatus)).child(snap.getKey()).setValue(null);
                                        break;
                                    }
                                }
                            }
                        });
                    } else {
                        SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss:SSS z");
                        String timestamp = df.format(new Date());
                        ReadStatus newReadStatus = new ReadStatus(book.getBookId(), timestamp, loggedInUser);
                        FirebaseDatabase.getInstance(getResources().getString(R.string.databaseReference)).getReference().child(getResources().getString(R.string.ssid)).child(getResources().getString(R.string.userReadStatus)).child(timestamp).setValue(newReadStatus);
                        readIcon.setVisibility(View.VISIBLE);
                        book.setReadStatus(true);
                    }
                    return true;
                }
                else if (item.getItemId() == R.id.id_bookList_item_menu_addQuotation) {
                    //add Quotation
                    return true;
                } else {
                    return false;
                }
            });
            popupMenu.show();
        });
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        if (dialog instanceof SubmitLocalizationDialogFragment) {
            SubmitLocalizationDialogFragment frag = (SubmitLocalizationDialogFragment) dialog;
            String translatedTitle = frag.titleText.getText().toString();
            String id = book.getBookId().toString();
            switch (frag.currentLanguage) {
                case "French":
                    FirebaseDatabase.getInstance(getResources().getString(R.string.databaseReference)).getReference().child(getResources().getString(R.string.ssid)).child(getResources().getString(R.string.bookList)).child(id).child("frenchTitle").setValue(translatedTitle);
                    french.setText("French: " + translatedTitle);
                    break;
                case "German":
                    FirebaseDatabase.getInstance(getResources().getString(R.string.databaseReference)).getReference().child(getResources().getString(R.string.ssid)).child(getResources().getString(R.string.bookList)).child(id).child("germanTitle").setValue(translatedTitle);
                    german.setText("German: " + translatedTitle);
                    break;
                case "Polish":
                    FirebaseDatabase.getInstance(getResources().getString(R.string.databaseReference)).getReference().child(getResources().getString(R.string.ssid)).child(getResources().getString(R.string.bookList)).child(id).child("polishTitle").setValue(translatedTitle);
                    polish.setText("Polish: " + translatedTitle);
                    break;
                case "Russian":
                    FirebaseDatabase.getInstance(getResources().getString(R.string.databaseReference)).getReference().child(getResources().getString(R.string.ssid)).child(getResources().getString(R.string.bookList)).child(id).child("russianTitle").setValue(translatedTitle);
                    russian.setText("Russian: " + translatedTitle);
                    break;
                case "Spanish":
                    FirebaseDatabase.getInstance(getResources().getString(R.string.databaseReference)).getReference().child(getResources().getString(R.string.ssid)).child(getResources().getString(R.string.bookList)).child(id).child("spanishTitle").setValue(translatedTitle);
                    spanish.setText("Spanish: " + translatedTitle);
                    break;
                default:
                    Toast.makeText(this, "Please select a language", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        dialog.dismiss();
    }

    @Override
    public void onDialogIntensityClick(DialogFragment dialog) {
        SubmitIntensityDialogFragment frag = (SubmitIntensityDialogFragment) dialog;
        String id = book.getBookId().toString();
        FirebaseDatabase.getInstance(getResources().getString(R.string.databaseReference)).getReference().child(getResources().getString(R.string.ssid)).child(getResources().getString(R.string.bookList)).child(id).child("bookIntensity").setValue(frag.intensity);
        intensity.setText("Intensity: " + frag.intensity);
    }
}