package com.thuanht.noteez.viewmodel;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.thuanht.noteez.database.room.AppDatabase;
import com.thuanht.noteez.model.Note;
import java.util.List;

public class NoteViewModel extends ViewModel {
    private MutableLiveData<List<Note>> listNote = new MutableLiveData<>();
    private MutableLiveData<Note> note = new MutableLiveData<>();

    public void setNotes(Context context){
        try {
            List<Note> list = AppDatabase.getInstance(context).noteDAO().selectAll();
            listNote.setValue(list);
            return;
        } catch (Exception e){
            e.printStackTrace();
        }
        listNote.setValue(null);
    }

    public void setNote(Note note){
        if(note!=null) this.note.setValue(note);
        else this.note.setValue(null);
    }

    public MutableLiveData<List<Note>> getListNote() {
        return listNote;
    }

    public MutableLiveData<Note> getNote() {
        return note;
    }
}
