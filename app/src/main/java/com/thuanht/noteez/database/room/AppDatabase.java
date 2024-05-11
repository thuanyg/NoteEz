package com.thuanht.noteez.database.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.thuanht.noteez.database.dao.NoteDAO;
import com.thuanht.noteez.model.Note;

@Database(entities = Note.class, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "NoteEz_Database")
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }

    public abstract NoteDAO noteDAO();
}
