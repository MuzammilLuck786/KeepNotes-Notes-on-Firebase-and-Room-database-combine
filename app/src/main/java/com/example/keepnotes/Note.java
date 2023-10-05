package com.example.keepnotes;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "Notes")
public class Note implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "Title")
    private String title;

    @ColumnInfo(name = "Description")
    private String description;
    @ColumnInfo(name = "OnlineId")
    private String onlineReferenceID;


    @ColumnInfo(name = "Note_key")
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getOnlineReferenceID() {
        return onlineReferenceID;
    }

    public void setOnlineReferenceID(String onlineReferenceID) {
        this.onlineReferenceID = onlineReferenceID;
    }

    public Note() {
    }

    public Note(int id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }

    @Ignore
    public Note(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
