package com.example.bookClub.browsebooks;

public class ReadStatus {

    private long bookId;
    private String timeStamp;
    private String username;

    public ReadStatus (long bookId, String timeStamp, String username) {
        this.bookId = bookId;
        this.timeStamp = timeStamp;
        this.username = username;
    }

    public ReadStatus () {

    }

    public long getBookId () {
        return this.bookId;
    }

    public String getTimeStamp () {
        return this.timeStamp;
    }

    public String getUsername () {
        return this.username;
    }

    public String toString () {
        return this.username + " read " + this.bookId + " at " + this.timeStamp;
    }
}
