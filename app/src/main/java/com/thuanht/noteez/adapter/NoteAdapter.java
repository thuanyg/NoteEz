package com.thuanht.noteez.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import com.thuanht.noteez.databinding.ItemNoteGridBinding;
import com.thuanht.noteez.databinding.ItemNoteListBinding;
import com.thuanht.noteez.model.Note;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteItemViewHolder>{

    private List<Note> notes;
    private Context context;
    private OnItemClickListener listener;
    private boolean isGridLayout = false;

    public NoteAdapter(Context context, List<Note> notes, OnItemClickListener listener) {
        this.notes = notes;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NoteItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (isGridLayout) {
            // Inflate grid item layout
            ItemNoteGridBinding binding = ItemNoteGridBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new NoteItemViewHolder(binding);
        } else {
            // Inflate list item layout
            ItemNoteListBinding binding = ItemNoteListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new NoteItemViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull NoteItemViewHolder holder, int position) {
        Note note = notes.get(position);

        // Check the type of binding and perform operations accordingly
        if (holder.binding instanceof ItemNoteListBinding) {
            ItemNoteListBinding listBinding = (ItemNoteListBinding) holder.binding;
            // Handle operations specific to the list layout
            if (TextUtils.isEmpty(note.getTitle())) listBinding.tvNoteTitle.setHint("Chưa có tiêu đề");
            if (TextUtils.isEmpty(note.getNoteContent())) listBinding.tvNoteContent.setHint("Chưa có nội dung");
            listBinding.tvNoteTitle.setText(note.getTitle());
            String htmlContent = note.getNoteContent();
            CharSequence styledText = HtmlCompat.fromHtml(htmlContent, HtmlCompat.FROM_HTML_MODE_COMPACT);
            listBinding.tvNoteContent.setText(styledText);
            listBinding.tvNoteDate.setText(note.getDate());
        } else if (holder.binding instanceof ItemNoteGridBinding) {
            ItemNoteGridBinding gridBinding = (ItemNoteGridBinding) holder.binding;
            // Handle operations specific to the grid layout
            if (TextUtils.isEmpty(note.getTitle())) gridBinding.tvNoteTitle.setHint("Chưa có tiêu đề");
            if (TextUtils.isEmpty(note.getNoteContent())) gridBinding.tvNoteContent.setHint("Chưa có nội dung");
            gridBinding.tvNoteTitle.setText(note.getTitle());
            String htmlContent = note.getNoteContent();
            CharSequence styledText = HtmlCompat.fromHtml(htmlContent, HtmlCompat.FROM_HTML_MODE_COMPACT);
            gridBinding.tvNoteContent.setText(styledText);
            gridBinding.tvNoteDate.setText(note.getDate());
        }

        // Set click listener
        holder.itemView.setOnClickListener(v -> listener.onClick(note));
    }


    @Override
    public int getItemCount() {
        return notes.size();
    }

    public static class NoteItemViewHolder extends RecyclerView.ViewHolder {
        ViewBinding binding;

        public NoteItemViewHolder(@NonNull ViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnItemClickListener{
        void onClick(Note note);
    }
    // Method to toggle between list and grid layout
    public Boolean toggleLayout() {
        isGridLayout = !isGridLayout;
        notifyDataSetChanged(); // Notify adapter to refresh the layout
        return isGridLayout;
    }
}
