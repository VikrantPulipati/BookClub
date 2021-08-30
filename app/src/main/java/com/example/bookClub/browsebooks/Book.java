package com.example.bookClub.browsebooks;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class Book implements Parcelable {

    private Long bookId;
    private String bookTitle;
    private String authorName;
    private String bookIntensity;

    private String seriesName;
    private String bookNumber;

    private String frenchTitle;
    private String germanTitle;
    private String polishTitle;
    private String russianTitle;
    private String spanishTitle;

    private String readOrder;
    private String category;

    private boolean readStatus;
    private Bitmap bookImage;


    public Book () {

    }

    protected Book(Parcel in) {
        if (in.readByte() == 0) {
            bookId = null;
        } else {
            bookId = in.readLong();
        }
        bookTitle = in.readString();
        authorName = in.readString();
        bookIntensity = in.readString();
        seriesName = in.readString();
        bookNumber = in.readString();
        frenchTitle = in.readString();
        germanTitle = in.readString();
        polishTitle = in.readString();
        russianTitle = in.readString();
        spanishTitle = in.readString();
        readOrder = in.readString();
        category = in.readString();
        readStatus = in.readByte() != 0;
        bookImage = in.readParcelable(Bitmap.class.getClassLoader());
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    public Long getBookId () {
        return this.bookId;
    }

    public String getBookTitle() {
        return this.bookTitle;
    }

    public String getAuthorName () {
        return this.authorName;
    }

    public String getBookIntensity () {
        return this.bookIntensity;
    }

    public String getSeries () {
        return this.seriesName + ": Book " + this.bookNumber;
    }

    public String getSeriesName () {
        return this.seriesName;
    }

    public String getBookNumber () {
        return this.bookNumber;
    }

    public String toString () {
        return this.bookTitle;
    }

    public boolean getReadStatus () {
        return this.readStatus;
    }

    public void setReadStatus (boolean status) {
        this.readStatus = status;
    }

    public void setBookImage (Bitmap image) {
        this.bookImage = image;
    }

    public Bitmap getBookImage () {
        return this.bookImage;
    }

    public String getReadOrder () {
        return this.readOrder;
    }

    public String getCategory () {
        return this.category;
    }

    public String getFrenchTitle() {
        return this.frenchTitle;
    }

    public String getGermanTitle () {
        return this.germanTitle;
    }

    public String getPolishTitle () {
        return this.polishTitle;
    }

    public String getRussianTitle () {
        return this.russianTitle;
    }

    public String getSpanishTitle () {
        return this.spanishTitle;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (bookId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(bookId);
        }
        dest.writeString(bookTitle);
        dest.writeString(authorName);
        dest.writeString(bookIntensity);
        dest.writeString(seriesName);
        dest.writeString(bookNumber);
        dest.writeString(frenchTitle);
        dest.writeString(germanTitle);
        dest.writeString(polishTitle);
        dest.writeString(russianTitle);
        dest.writeString(spanishTitle);
        dest.writeString(readOrder);
        dest.writeString(category);
        dest.writeByte((byte) (readStatus ? 1 : 0));
        dest.writeParcelable(bookImage, flags);
    }
}
