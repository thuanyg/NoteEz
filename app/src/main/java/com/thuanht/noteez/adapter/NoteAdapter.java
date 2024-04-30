package com.thuanht.noteez.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thuanht.noteez.databinding.ItemNoteListBinding;
import com.thuanht.noteez.model.Note;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteItemViewHolder>{

    private List<Note> notes;
    private Context context;
    private OnItemClickListener listener;

    public NoteAdapter(Context context, List<Note> notes, OnItemClickListener listener) {
        this.notes = notes;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NoteItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemNoteListBinding binding = ItemNoteListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new NoteItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteItemViewHolder holder, int position) {
        Note note = notes.get(position);
        if(TextUtils.isEmpty(note.getTitle())) holder.binding.tvNoteTitle.setHint("Không có tiêu đề");
        if(TextUtils.isEmpty(note.getNoteContent())) holder.binding.tvNoteContent.setVisibility(View.GONE);
        holder.binding.tvNoteTitle.setText(note.getTitle());
        holder.binding.tvNoteContent.setText(note.getNoteContent());
        holder.binding.tvNoteDate.setText(note.getDate());

        holder.itemView.setOnClickListener(v -> listener.onClick(note));
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public static class NoteItemViewHolder extends RecyclerView.ViewHolder {
        ItemNoteListBinding binding;
        public NoteItemViewHolder(@NonNull ItemNoteListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnItemClickListener{
        void onClick(Note note);
    }
}
