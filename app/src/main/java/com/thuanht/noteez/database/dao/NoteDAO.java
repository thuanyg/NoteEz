package com.thuanht.noteez.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.thuanht.noteez.model.Note;

import java.util.List;

@Dao
public interface NoteDAO {
    @Query("SELECT * FROM note ORDER BY date DESC")
    List<Note> selectAll();

    @Insert
    long insert(Note note);
}
