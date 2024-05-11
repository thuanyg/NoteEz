package com.thuanht.noteez.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.thuanht.noteez.model.Note;

import java.util.List;

@Dao
public interface NoteDAO {
    @Query("SELECT * FROM note ORDER BY date DESC")
    List<Note> selectAll();

    @Query("SELECT * FROM note WHERE id = :noteID")
    Note getNote(int noteID);
    @Insert
    long insert(Note note);

    @Update
    void update(Note note);

    @Delete
    void delete(Note note);

    @Query("DELETE FROM note")
    void deleteAll();
    @Query("SELECT * FROM note WHERE noteContent LIKE '%' || :query || '%' OR title LIKE '%' || :query || '%' ORDER BY date DESC")
    List<Note> searchNote(String query);
    @Query("SELECT * FROM note ORDER BY RANDOM()")
    List<Note> sortRandom();
}
