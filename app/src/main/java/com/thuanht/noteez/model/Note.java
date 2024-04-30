package com.thuanht.noteez.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
@Entity(tableName = "Note")
public class Note implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title, noteContent, date;

    public Note() {
    }

    public Note(int id, String title, String noteContent, String date) {
        this.id = id;
        this.title = title;
        this.noteContent = noteContent;
        this.date = date;
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

    public String getNoteContent() {
        return noteContent;
    }

    public void setNoteContent(String noteContent) {
        this.noteContent = noteContent;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
