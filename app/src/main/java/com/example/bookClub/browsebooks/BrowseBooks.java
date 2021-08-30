package com.example.bookClub.browsebooks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookClub.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class BrowseBooks extends AppCompatActivity {

    RecyclerView bookListRecycler;
    ProgressBar progressBar;

    ArrayList<Book> bookList;
    BooklistAdapter adapter;

    String loggedInUser;

    EditText filterText;
    ImageButton filterOptions;
    Button clearFilters;
    Button applyFilters;
    static final int FILTER_OPTION_TITLE = 0;
    static final int FILTER_OPTION_AUTHOR = 1;
    static final int FILTER_OPTION_SERIES = 2;
    static final int FILTER_OPTION_READ_ONLY = 3;
    static final int FILTER_OPTION_UNREAD_ONLY = 4;
    static final int FILTER_OPTION_DEFAULT = -1;
    int currentFilterOption = FILTER_OPTION_DEFAULT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_books);

        loggedInUser = this.getSharedPreferences("sharedPref", Context.MODE_PRIVATE).getString("username", "");

        bookListRecycler = findViewById(R.id.id_bookListRecycler);
        progressBar = findViewById(R.id.id_browseBooks_progressBar);
        progressBar.setVisibility(View.VISIBLE);

        filterText = findViewById(R.id.id_browse_filterText);

        filterOptions = findViewById(R.id.id_browse_filterOptions);
        filterOptions.setOnClickListener(v -> {
            hideKeyboard();
            PopupMenu filterOptionsMenu = new PopupMenu(BrowseBooks.this, v);
            MenuInflater inflater = new MenuInflater(BrowseBooks.this);
            inflater.inflate(R.menu.browse_filter_options_menu, filterOptionsMenu.getMenu());

            switch (currentFilterOption) {
                case FILTER_OPTION_TITLE:
                    MenuItem optionTitle = filterOptionsMenu.getMenu().findItem(R.id.id_browse_filterOptions_title);
                    SpannableString s = new SpannableString(optionTitle.getTitle().toString());
                    s.setSpan(new StyleSpan(Typeface.BOLD), 0, s.length(), 0);
                    optionTitle.setTitle(s);
                    break;
                case FILTER_OPTION_SERIES:
                    MenuItem optionSeries = filterOptionsMenu.getMenu().findItem(R.id.id_browse_filterOptions_series);
                    SpannableString s1 = new SpannableString(optionSeries.getTitle().toString());
                    s1.setSpan(new StyleSpan(Typeface.BOLD), 0, s1.length(), 0);
                    optionSeries.setTitle(s1);
                    break;
                case FILTER_OPTION_AUTHOR:
                    MenuItem optionAuthor = filterOptionsMenu.getMenu().findItem(R.id.id_browse_filterOptions_authorName);
                    SpannableString s2 = new SpannableString(optionAuthor.getTitle().toString());
                    s2.setSpan(new StyleSpan(Typeface.BOLD), 0, s2.length(), 0);
                    optionAuthor.setTitle(s2);
                    break;
                case FILTER_OPTION_READ_ONLY:
                    MenuItem optionReadOnly = filterOptionsMenu.getMenu().findItem(R.id.id_browse_filterOptions_readOnly);
                    SpannableString s3 = new SpannableString(optionReadOnly.getTitle().toString());
                    s3.setSpan(new StyleSpan(Typeface.BOLD), 0, s3.length(), 0);
                    optionReadOnly.setTitle(s3);
                    break;
                case FILTER_OPTION_UNREAD_ONLY:
                    MenuItem optionUnreadOnly = filterOptionsMenu.getMenu().findItem(R.id.id_browse_filterOptions_unreadOnly);
                    SpannableString s4 = new SpannableString(optionUnreadOnly.getTitle().toString());
                    s4.setSpan(new StyleSpan(Typeface.BOLD), 0, s4.length(), 0);
                    optionUnreadOnly.setTitle(s4);
                    break;
                default:
                    break;
            }

            filterOptionsMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.id_browse_filterOptions_title) {
                    if (currentFilterOption == FILTER_OPTION_TITLE) {
                        currentFilterOption = FILTER_OPTION_DEFAULT;
                        filterText.setHint("Search Books");
                    } else {
                        currentFilterOption = FILTER_OPTION_TITLE;
                        filterText.setHint("Search By Title");
                    }
                    return true;
                }
                else if (item.getItemId() == R.id.id_browse_filterOptions_series) {
                    if (currentFilterOption == FILTER_OPTION_SERIES) {
                        currentFilterOption = FILTER_OPTION_DEFAULT;
                        filterText.setHint("Search Books");
                    } else {
                        currentFilterOption = FILTER_OPTION_SERIES;
                        filterText.setHint("Search By Series");
                    }
                    return true;
                }
                else if (item.getItemId() == R.id.id_browse_filterOptions_authorName) {
                    if (currentFilterOption == FILTER_OPTION_AUTHOR) {
                        currentFilterOption = FILTER_OPTION_DEFAULT;
                        filterText.setHint("Search Books");
                    } else {
                        currentFilterOption = FILTER_OPTION_AUTHOR;
                        filterText.setHint("Search By Author");
                    }
                    return true;
                }
                else if (item.getItemId() == R.id.id_browse_filterOptions_readOnly) {
                    if (currentFilterOption == FILTER_OPTION_READ_ONLY) {
                        currentFilterOption = FILTER_OPTION_DEFAULT;
                        filterText.setHint("Search Books");
                    } else {
                        currentFilterOption = FILTER_OPTION_READ_ONLY;
                        filterText.setHint("Search By Read Only");
                    }
                    return true;
                }
                else if (item.getItemId() == R.id.id_browse_filterOptions_unreadOnly) {
                    if (currentFilterOption == FILTER_OPTION_UNREAD_ONLY) {
                        currentFilterOption = FILTER_OPTION_DEFAULT;
                        filterText.setHint("Search Books");
                    } else {
                        currentFilterOption = FILTER_OPTION_UNREAD_ONLY;
                        filterText.setHint("Search By Unread Only");
                    }
                    return true;
                }
                else {
                    return false;
                }
            });
            filterOptionsMenu.show();
        });

        clearFilters = findViewById(R.id.id_browse_clearFilters);
        clearFilters.setOnClickListener(v -> {
            hideKeyboard();
            currentFilterOption = FILTER_OPTION_DEFAULT;
            filterText.setText("");
            filterText.setHint("Search Books");
        });

        applyFilters = findViewById(R.id.id_browse_applyFilters);
        applyFilters.setOnClickListener(v -> {
            hideKeyboard();
            applyFilters();
        });

        bookList = new ArrayList<>();
        adapter = new BooklistAdapter(this, bookList);
        bookListRecycler.setAdapter(adapter);
        bookListRecycler.setLayoutManager(new LinearLayoutManager(BrowseBooks.this, LinearLayoutManager.VERTICAL, false));

        DatabaseReference databaseRef = FirebaseDatabase.getInstance(getResources().getString(R.string.databaseReference)).getReference();
        databaseRef.child(getResources().getString(R.string.ssid)).child(getResources().getString(R.string.bookList)).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.d("TAGBOOK", task.getException().toString());
            } else {
                Iterable<DataSnapshot> resultList = task.getResult().getChildren();
                for (DataSnapshot snap : resultList) {
                    Book newBook = snap.getValue(Book.class);
                    Log.d("TAGBOOK", newBook.toString());
                    Bitmap bookImage = null;
                    try {
                        bookImage = new GetImageTask().execute(snap.child("bookImageURL").getValue().toString()).get();
                        bookImage = Bitmap.createScaledBitmap(bookImage, 230, 346, false);
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                    newBook.setBookImage(bookImage);
                    bookList.add(newBook);
                }
            }
            Query readStatusQuery = FirebaseDatabase.getInstance(getResources().getString(R.string.databaseReference)).getReference().child(getResources().getString(R.string.ssid)).child(getResources().getString(R.string.userReadStatus)).orderByChild("username").equalTo(loggedInUser);
            readStatusQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    ArrayList<ReadStatus> readStatusList = new ArrayList<>();
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        readStatusList.add(snap.getValue(ReadStatus.class));
                    }

                    for (Book b : bookList) {
                        if (readStatusList.size() == 0) {
                            b.setReadStatus(false);
                        } else {
                            for (ReadStatus status : readStatusList) {
                                if (status.getBookId() == b.getBookId()) {
                                    b.setReadStatus(true);
                                    break;
                                } else {
                                    b.setReadStatus(false);
                                }
                            }
                        }
                    }
                    generateList();
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {
                    Log.d("TAGERROR", error.toException().toString());
                }
            });
        });

        Query readStatusQuery = FirebaseDatabase.getInstance(getResources().getString(R.string.databaseReference)).getReference().child(getResources().getString(R.string.ssid)).child(getResources().getString(R.string.userReadStatus)).orderByChild("username").equalTo(loggedInUser);
        readStatusQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                ArrayList<ReadStatus> readStatusList = new ArrayList<>();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    readStatusList.add(snap.getValue(ReadStatus.class));
                }

                for (Book b : bookList) {
                    if (readStatusList.size() == 0) {
                        b.setReadStatus(false);
                    } else {
                        for (ReadStatus status : readStatusList) {
                            if (status.getBookId() == b.getBookId()) {
                                b.setReadStatus(true);
                                break;
                            } else {
                                b.setReadStatus(false);
                            }
                        }
                    }
                }
                generateList();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Log.d("TAGERROR", error.toException().toString());
            }
        });
    }

    public void applyFilters () {
        bookListRecycler.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        switch (currentFilterOption) {
            case FILTER_OPTION_TITLE:
                filterByTitle();
                break;
            case FILTER_OPTION_SERIES:
                filterBySeries();
                break;
            case FILTER_OPTION_AUTHOR:
                filterByAuthor();
                break;
            case FILTER_OPTION_READ_ONLY:
                filterByReadOnly();
                break;
            case FILTER_OPTION_UNREAD_ONLY:
                filterByUnreadOnly();
                break;
            case FILTER_OPTION_DEFAULT:
                filterDefault();
                break;
            default:
                break;
        }
    }

    public void filterDefault () {
        bookList.clear();
        DatabaseReference databaseRef = FirebaseDatabase.getInstance(getResources().getString(R.string.databaseReference)).getReference();
        databaseRef.child(getResources().getString(R.string.ssid)).child(getResources().getString(R.string.bookList)).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.d("TAGBOOK", task.getException().toString());
            } else {
                Iterable<DataSnapshot> resultList = task.getResult().getChildren();
                for (DataSnapshot snap : resultList) {
                    Book newBook = snap.getValue(Book.class);
                    Log.d("TAGBOOK", newBook.toString());
                    Bitmap bookImage = null;
                    try {
                        bookImage = new GetImageTask().execute(snap.child("bookImageURL").getValue().toString()).get();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    newBook.setBookImage(bookImage);
                    if (newBook.getBookTitle().toLowerCase().contains(filterText.getText().toString().toLowerCase()) ||
                            newBook.getSeriesName().toLowerCase().contains(filterText.getText().toString().toLowerCase()) ||
                            newBook.getAuthorName().toLowerCase().contains(filterText.getText().toString().toLowerCase())) {
                        bookList.add(newBook);
                    }
                }
            }
            Query readStatusQuery = FirebaseDatabase.getInstance(getResources().getString(R.string.databaseReference)).getReference().child(getResources().getString(R.string.ssid)).child(getResources().getString(R.string.userReadStatus)).orderByChild("username").equalTo(loggedInUser);
            readStatusQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    ArrayList<ReadStatus> readStatusList = new ArrayList<>();
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        readStatusList.add(snap.getValue(ReadStatus.class));
                    }

                    for (Book b : bookList) {
                        if (readStatusList.size() == 0) {
                            b.setReadStatus(false);
                        } else {
                            for (ReadStatus status : readStatusList) {
                                if (status.getBookId() == b.getBookId()) {
                                    b.setReadStatus(true);
                                    break;
                                } else {
                                    b.setReadStatus(false);
                                }
                            }
                        }
                    }
                    generateList();
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {
                    Log.d("TAGERROR", error.toException().toString());
                }
            });
        });
    }

    public void filterByTitle () {
        bookList.clear();
        DatabaseReference databaseRef = FirebaseDatabase.getInstance(getResources().getString(R.string.databaseReference)).getReference();
        databaseRef.child(getResources().getString(R.string.ssid)).child(getResources().getString(R.string.bookList)).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.d("TAGBOOK", task.getException().toString());
            } else {
                Iterable<DataSnapshot> resultList = task.getResult().getChildren();
                for (DataSnapshot snap : resultList) {
                    Book newBook = snap.getValue(Book.class);
                    Log.d("TAGBOOK", newBook.toString());
                    Bitmap bookImage = null;
                    try {
                        bookImage = new GetImageTask().execute(snap.child("bookImageURL").getValue().toString()).get();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    newBook.setBookImage(bookImage);
                    if (newBook.getBookTitle().toLowerCase().contains(filterText.getText().toString().toLowerCase())) {
                        bookList.add(newBook);
                    }
                }
            }
            Query readStatusQuery = FirebaseDatabase.getInstance(getResources().getString(R.string.databaseReference)).getReference().child(getResources().getString(R.string.ssid)).child(getResources().getString(R.string.userReadStatus)).orderByChild("username").equalTo(loggedInUser);
            readStatusQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    ArrayList<ReadStatus> readStatusList = new ArrayList<>();
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        readStatusList.add(snap.getValue(ReadStatus.class));
                    }

                    for (Book b : bookList) {
                        if (readStatusList.size() == 0) {
                            b.setReadStatus(false);
                        } else {
                            for (ReadStatus status : readStatusList) {
                                if (status.getBookId() == b.getBookId()) {
                                    b.setReadStatus(true);
                                    break;
                                } else {
                                    b.setReadStatus(false);
                                }
                            }
                        }
                    }
                    generateList();
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {
                    Log.d("TAGERROR", error.toException().toString());
                }
            });
        });
    }

    public void filterBySeries () {
        bookList.clear();
        DatabaseReference databaseRef = FirebaseDatabase.getInstance(getResources().getString(R.string.databaseReference)).getReference();
        databaseRef.child(getResources().getString(R.string.ssid)).child(getResources().getString(R.string.bookList)).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.d("TAGBOOK", task.getException().toString());
                } else {
                    Iterable<DataSnapshot> resultList = task.getResult().getChildren();
                    for (DataSnapshot snap : resultList) {
                        Book newBook = snap.getValue(Book.class);
                        Log.d("TAGBOOK", newBook.toString());
                        Bitmap bookImage = null;
                        try {
                            bookImage = new GetImageTask().execute(snap.child("bookImageURL").getValue().toString()).get();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        newBook.setBookImage(bookImage);
                        if (newBook.getSeries().toLowerCase().contains(filterText.getText().toString().toLowerCase())) {
                            bookList.add(newBook);
                        }
                    }
                }
                Query readStatusQuery = FirebaseDatabase.getInstance(getResources().getString(R.string.databaseReference)).getReference().child(getResources().getString(R.string.ssid)).child(getResources().getString(R.string.userReadStatus)).orderByChild("username").equalTo(loggedInUser);
                readStatusQuery.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        ArrayList<ReadStatus> readStatusList = new ArrayList<>();
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            readStatusList.add(snap.getValue(ReadStatus.class));
                        }

                        for (Book b : bookList) {
                            if (readStatusList.size() == 0) {
                                b.setReadStatus(false);
                            } else {
                                for (ReadStatus status : readStatusList) {
                                    if (status.getBookId() == b.getBookId()) {
                                        b.setReadStatus(true);
                                        break;
                                    } else {
                                        b.setReadStatus(false);
                                    }
                                }
                            }
                        }
                        generateList();
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                        Log.d("TAGERROR", error.toException().toString());
                    }
                });
            }
        });
    }

    public void filterByAuthor () {
        bookList.clear();
        DatabaseReference databaseRef = FirebaseDatabase.getInstance(getResources().getString(R.string.databaseReference)).getReference();
        databaseRef.child(getResources().getString(R.string.ssid)).child(getResources().getString(R.string.bookList)).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.d("TAGBOOK", task.getException().toString());
                } else {
                    Iterable<DataSnapshot> resultList = task.getResult().getChildren();
                    for (DataSnapshot snap : resultList) {
                        Book newBook = snap.getValue(Book.class);
                        Log.d("TAGBOOK", newBook.toString());
                        Bitmap bookImage = null;
                        try {
                            bookImage = new GetImageTask().execute(snap.child("bookImageURL").getValue().toString()).get();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        newBook.setBookImage(bookImage);
                        if (newBook.getAuthorName().toLowerCase().contains(filterText.getText().toString().toLowerCase())) {
                            bookList.add(newBook);
                        }
                    }
                }
                Query readStatusQuery = FirebaseDatabase.getInstance(getResources().getString(R.string.databaseReference)).getReference().child(getResources().getString(R.string.ssid)).child(getResources().getString(R.string.userReadStatus)).orderByChild("username").equalTo(loggedInUser);
                readStatusQuery.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        ArrayList<ReadStatus> readStatusList = new ArrayList<>();
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            readStatusList.add(snap.getValue(ReadStatus.class));
                        }

                        for (Book b : bookList) {
                            if (readStatusList.size() == 0) {
                                b.setReadStatus(false);
                            } else {
                                for (ReadStatus status : readStatusList) {
                                    if (status.getBookId() == b.getBookId()) {
                                        b.setReadStatus(true);
                                        break;
                                    } else {
                                        b.setReadStatus(false);
                                    }
                                }
                            }
                        }
                        generateList();
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                        Log.d("TAGERROR", error.toException().toString());
                    }
                });
            }
        });
    }

    public void filterByReadOnly () {
        bookList.clear();
        DatabaseReference databaseRef = FirebaseDatabase.getInstance(getResources().getString(R.string.databaseReference)).getReference();
        databaseRef.child(getResources().getString(R.string.ssid)).child(getResources().getString(R.string.bookList)).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.d("TAGBOOK", task.getException().toString());
                } else {
                    Iterable<DataSnapshot> resultList = task.getResult().getChildren();
                    for (DataSnapshot snap : resultList) {
                        Book newBook = snap.getValue(Book.class);
                        Log.d("TAGBOOK", newBook.toString());
                        Bitmap bookImage = null;
                        try {
                            bookImage = new GetImageTask().execute(snap.child("bookImageURL").getValue().toString()).get();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        newBook.setBookImage(bookImage);
                        bookList.add(newBook);
                    }
                }
                Query readStatusQuery = FirebaseDatabase.getInstance(getResources().getString(R.string.databaseReference)).getReference().child(getResources().getString(R.string.ssid)).child(getResources().getString(R.string.userReadStatus)).orderByChild("username").equalTo(loggedInUser);
                readStatusQuery.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        ArrayList<ReadStatus> readStatusList = new ArrayList<>();
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            readStatusList.add(snap.getValue(ReadStatus.class));
                        }
                        ArrayList<Book> readBooks = new ArrayList<>();
                        for (Book b : bookList) {
                            if (readStatusList.size() == 0) {
                                b.setReadStatus(false);
                            } else {
                                for (ReadStatus status : readStatusList) {
                                    if (status.getBookId() == b.getBookId()) {
                                        b.setReadStatus(true);
                                        readBooks.add(b);
                                        break;
                                    } else {
                                        b.setReadStatus(false);
                                    }
                                }
                            }
                        }
                        bookList.clear();
                        for (Book b : readBooks) {
                            bookList.add(b);
                        }
                        generateList();
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                        Log.d("TAGERROR", error.toException().toString());
                    }
                });
            }
        });
    }

    public void filterByUnreadOnly () {
        bookList.clear();
        DatabaseReference databaseRef = FirebaseDatabase.getInstance(getResources().getString(R.string.databaseReference)).getReference();
        databaseRef.child(getResources().getString(R.string.ssid)).child(getResources().getString(R.string.bookList)).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.d("TAGBOOK", task.getException().toString());
                } else {
                    Iterable<DataSnapshot> resultList = task.getResult().getChildren();
                    for (DataSnapshot snap : resultList) {
                        Book newBook = snap.getValue(Book.class);
                        Log.d("TAGBOOK", newBook.toString());
                        Bitmap bookImage = null;
                        try {
                            bookImage = new GetImageTask().execute(snap.child("bookImageURL").getValue().toString()).get();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        newBook.setBookImage(bookImage);
                        bookList.add(newBook);
                    }
                }
                Query readStatusQuery = FirebaseDatabase.getInstance(getResources().getString(R.string.databaseReference)).getReference().child(getResources().getString(R.string.ssid)).child(getResources().getString(R.string.userReadStatus)).orderByChild("username").equalTo(loggedInUser);
                readStatusQuery.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        ArrayList<ReadStatus> readStatusList = new ArrayList<>();
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            readStatusList.add(snap.getValue(ReadStatus.class));
                        }
                        ArrayList<Book> unreadBooks = new ArrayList<>();
                        for (Book b : bookList) {
                            if (readStatusList.size() == 0) {
                                b.setReadStatus(false);
                            } else {
                                for (ReadStatus status : readStatusList) {
                                    if (status.getBookId() == b.getBookId()) {
                                        b.setReadStatus(true);
                                        break;
                                    } else {
                                        b.setReadStatus(false);
                                    }
                                }
                            }
                            if (b.getReadStatus() == false) {
                                unreadBooks.add(b);
                            }
                        }
                        bookList.clear();
                        for (Book b : unreadBooks) {
                            bookList.add(b);
                        }
                        generateList();
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                        Log.d("TAGERROR", error.toException().toString());
                    }
                });
            }
        });
    }

    public void clearFilters () {
        bookList.clear();
        DatabaseReference databaseRef = FirebaseDatabase.getInstance(getResources().getString(R.string.databaseReference)).getReference();
        databaseRef.child(getResources().getString(R.string.ssid)).child(getResources().getString(R.string.bookList)).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.d("TAGBOOK", task.getException().toString());
                } else {
                    Iterable<DataSnapshot> resultList = task.getResult().getChildren();
                    for (DataSnapshot snap : resultList) {
                        Book newBook = snap.getValue(Book.class);
                        Log.d("TAGBOOK", newBook.toString());
                        Bitmap bookImage = null;
                        try {
                            bookImage = new GetImageTask().execute(snap.child("bookImageURL").getValue().toString()).get();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        newBook.setBookImage(bookImage);
                        bookList.add(newBook);
                    }
                }
                Query readStatusQuery = FirebaseDatabase.getInstance(getResources().getString(R.string.databaseReference)).getReference().child(getResources().getString(R.string.ssid)).child(getResources().getString(R.string.userReadStatus)).orderByChild("username").equalTo(loggedInUser);
                readStatusQuery.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        ArrayList<ReadStatus> readStatusList = new ArrayList<>();
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            readStatusList.add(snap.getValue(ReadStatus.class));
                        }

                        for (Book b : bookList) {
                            if (readStatusList.size() == 0) {
                                b.setReadStatus(false);
                            } else {
                                for (ReadStatus status : readStatusList) {
                                    if (status.getBookId() == b.getBookId()) {
                                        b.setReadStatus(true);
                                        break;
                                    } else {
                                        b.setReadStatus(false);
                                    }
                                }
                            }
                        }
                        generateList();
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                        Log.d("TAGERROR", error.toException().toString());
                    }
                });
            }
        });
    }

    public class GetImageTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {

            String stringURL = strings[0];
            Bitmap img = null;
            try {
                InputStream in = new java.net.URL(stringURL).openStream();
                img = BitmapFactory.decodeStream(in);
                in.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return img;
        }
    }

    public void generateList () {
        progressBar.setVisibility(View.GONE);
        bookListRecycler.setVisibility(View.VISIBLE);

        //Parcelable recyclerViewState = bookListRecycler.getLayoutManager().onSaveInstanceState();
        adapter.notifyDataSetChanged();
        //bookListRecycler.getLayoutManager().onRestoreInstanceState(recyclerViewState);
    }

    public void hideKeyboard () {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}