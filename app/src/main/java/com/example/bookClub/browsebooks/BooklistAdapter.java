package com.example.bookClub.browsebooks;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookClub.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class BooklistAdapter extends RecyclerView.Adapter<BooklistAdapter.BookListHolder> {

    Context parentContext;
    ArrayList<Book> list;
    String loggedInUser;

    public BooklistAdapter (Context context, ArrayList<Book> list) {
        parentContext = context;
        this.list = list;
        loggedInUser = parentContext.getSharedPreferences("sharedPref", Context.MODE_PRIVATE).getString("username", "");
    }

    @NonNull
    @Override
    public BookListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parentContext).inflate(R.layout.holder_book_list_alt, parent, false);
        BookListHolder holder = new BookListHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull BookListHolder holder, int position) {
        Book currentBook = list.get(position);

        holder.cardView.setOnClickListener(v -> {
            Intent intent = new Intent(parentContext, InspectBook.class);
            intent.putExtra("bookId", currentBook.getBookId());
            intent.putExtra("readStatus", currentBook.getReadStatus());
            intent.putExtra("coverImage", currentBook.getBookImage());
            parentContext.startActivity(intent);
        });

        holder.coverImage.setImageBitmap(currentBook.getBookImage());
        holder.name.setText(currentBook.getBookTitle());
        if (currentBook.getSeriesName().length() > 0) {
            holder.series.setText(currentBook.getSeries());
            holder.series.setVisibility(View.VISIBLE);
        } else {
            holder.series.setVisibility(View.GONE);
        }
        holder.author.setText("By: " + currentBook.getAuthorName());

        holder.menuButton.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(parentContext, v);
            MenuInflater inflater = new MenuInflater(parentContext);
            inflater.inflate(R.menu.book_list_item_menu, popupMenu.getMenu());
            MenuItem markAsRead = popupMenu.getMenu().findItem(R.id.id_bookList_item_menu_markRead);
            if (currentBook.getReadStatus()) {
                markAsRead.setVisible(false);
                //markAsRead.setTitle("Mark as Unread");
            }
            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.id_bookList_item_menu_markRead) {
                    if (currentBook.getReadStatus()) {
                        markAsRead.setTitle("Mark as Unread");
                        Query readStatusQuery = FirebaseDatabase.getInstance().getReference(parentContext.getResources().getString(R.string.databaseReference)).child(parentContext.getResources().getString(R.string.ssid)).child(parentContext.getResources().getString(R.string.userReadStatus)).orderByChild("username").equalTo(loggedInUser);
                        readStatusQuery.get().addOnCompleteListener(task -> {
                            if (!task.isSuccessful()) {
                                Log.d("firebase", task.getException().toString());
                            } else {
                                Iterable<DataSnapshot> resultList = task.getResult().getChildren();
                                for (DataSnapshot snap : resultList) {
                                    if (snap.child("bookId").getValue().equals(currentBook.getBookId())) {
                                        FirebaseDatabase.getInstance().getReference(parentContext.getResources().getString(R.string.databaseReference)).child(parentContext.getResources().getString(R.string.ssid)).child(parentContext.getResources().getString(R.string.userReadStatus)).child(snap.getKey()).setValue(null);
                                        break;
                                    }
                                }
                            }
                        });
                    } else {
                        SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss:SSS z");
                        String timestamp = df.format(new Date());
                        ReadStatus newReadStatus = new ReadStatus(currentBook.getBookId(), timestamp, loggedInUser);
                        FirebaseDatabase.getInstance(parentContext.getResources().getString(R.string.databaseReference)).getReference().child(parentContext.getResources().getString(R.string.ssid)).child(parentContext.getResources().getString(R.string.userReadStatus)).child(timestamp).setValue(newReadStatus);
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

        if (currentBook.getReadStatus()) {
            holder.readStatusIcon.setVisibility(View.VISIBLE);
        } else {
            holder.readStatusIcon.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class BookListHolder extends RecyclerView.ViewHolder {
        //declare widgets here and do the findviewbyid stuffs

        ImageView coverImage;
        TextView name;
        TextView author;
        ImageButton menuButton;
        ImageView readStatusIcon;
        TextView series;

        CardView cardView;

        public BookListHolder (@NonNull View itemView) {
            super(itemView);

            coverImage = itemView.findViewById(R.id.id_bookList_coverImage);
            name = itemView.findViewById(R.id.id_bookList_name);
            author = itemView.findViewById(R.id.id_bookList_author);
            menuButton = itemView.findViewById(R.id.id_bookList_menuIcon);
            readStatusIcon = itemView.findViewById(R.id.id_bookList_readIcon);
            series = itemView.findViewById(R.id.id_bookList_series);

            cardView = itemView.findViewById(R.id.id_bookList_cardView);
        }
    }
}
