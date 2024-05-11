package com.thuanht.noteez.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import com.thuanht.noteez.R;
import com.thuanht.noteez.database.room.AppDatabase;
import com.thuanht.noteez.databinding.ItemNoteGridBinding;
import com.thuanht.noteez.databinding.ItemNoteListBinding;
import com.thuanht.noteez.model.Note;
import com.thuanht.noteez.utils.DateUtils;
import com.thuanht.noteez.viewmodel.HomeViewModel;

import java.util.ArrayList;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteItemViewHolder> {

    private List<Note> notes;
    private List<Note> noteSelectedList = new ArrayList<>();
    private Context context;
    private OnItemClickListener listener;
    private boolean isGridLayout = false;
    private boolean isEnable = false;
    private boolean isSeletedAll = false;
    private HomeViewModel viewModel;
    private boolean isRandom = false;

    public NoteAdapter(Context context, List<Note> notes, OnItemClickListener listener) {
        this.notes = notes;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NoteItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        viewModel = new ViewModelProvider((ViewModelStoreOwner) this.context).get(HomeViewModel.class);
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
            if (TextUtils.isEmpty(note.getTitle()))
                listBinding.tvNoteTitle.setHint("Chưa có tiêu đề");
            if (TextUtils.isEmpty(note.getNoteContent()))
                listBinding.tvNoteContent.setHint("Chưa có nội dung");
            listBinding.tvNoteTitle.setText(note.getTitle());
            String htmlContent = note.getNoteContent();
            CharSequence styledText = HtmlCompat.fromHtml(htmlContent, HtmlCompat.FROM_HTML_MODE_COMPACT);
            listBinding.tvNoteContent.setText(styledText);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                listBinding.tvNoteDate.setText(DateUtils.convertToRelativeTime(note.getDate()));
            } else {
                listBinding.tvNoteDate.setText(note.getDate());
            }
        } else if (holder.binding instanceof ItemNoteGridBinding) {
            ItemNoteGridBinding gridBinding = (ItemNoteGridBinding) holder.binding;
            // Handle operations specific to the grid layout
            if (TextUtils.isEmpty(note.getTitle()))
                gridBinding.tvNoteTitle.setHint("Chưa có tiêu đề");
            if (TextUtils.isEmpty(note.getNoteContent()))
                gridBinding.tvNoteContent.setHint("Chưa có nội dung");
            gridBinding.tvNoteTitle.setText(note.getTitle());
            String htmlContent = note.getNoteContent();
            CharSequence styledText = HtmlCompat.fromHtml(htmlContent, HtmlCompat.FROM_HTML_MODE_COMPACT);
            gridBinding.tvNoteContent.setText(styledText);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                gridBinding.tvNoteDate.setText(DateUtils.convertToRelativeTime(note.getDate()));
            } else {
                gridBinding.tvNoteDate.setText(note.getDate());
            }
        }

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            if (isEnable) {
                ClickItem(holder);
            } else {
                listener.onClick(note);
            }
        });
        int greyColor = ContextCompat.getColor(context, R.color.grey_toolNote_bg);
        if (isSeletedAll) {
            if (holder.binding instanceof ItemNoteListBinding) {
                ((ItemNoteListBinding) holder.binding).checkboxSelection.setVisibility(View.VISIBLE);
                holder.binding.getRoot().setBackgroundTintList(ColorStateList.valueOf(greyColor));
            }
            if (holder.binding instanceof ItemNoteGridBinding) {
                ((ItemNoteGridBinding) holder.binding).checkboxSelection.setVisibility(View.VISIBLE);
                holder.binding.getRoot().setBackgroundTintList(ColorStateList.valueOf(greyColor));
            }
        } else {
            if (holder.binding instanceof ItemNoteListBinding) {
                ((ItemNoteListBinding) holder.binding).checkboxSelection.setVisibility(View.GONE);
                holder.binding.getRoot().setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            }
            if (holder.binding instanceof ItemNoteGridBinding) {
                ((ItemNoteGridBinding) holder.binding).checkboxSelection.setVisibility(View.GONE);
                holder.binding.getRoot().setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            }
        }

        holder.itemView.setOnLongClickListener(v -> {
            if (!isEnable) {
                ActionMode.Callback callback = new ActionMode.Callback() {
                    @Override
                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                        MenuInflater menuInflater = mode.getMenuInflater();
                        menuInflater.inflate(R.menu.menu_action_delete, menu);
                        return true;
                    }

                    @Override
                    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                        isEnable = true;
                        ClickItem(holder);
                        viewModel.getMutableLiveData().observe((LifecycleOwner) context, new Observer<String>() {
                            @Override
                            public void onChanged(String s) {
                                mode.setTitle(String.format("Đã chọn %s", s));
                            }
                        });
                        return true;
                    }

                    @Override
                    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                        int menuItem = item.getItemId();
                        if (menuItem == R.id.btnDelete) {
                            if(noteSelectedList.size() == notes.size()){
                                AppDatabase.getInstance(context).noteDAO().deleteAll();
                                notes.clear();
                                noteSelectedList.clear();
                                notifyDataSetChanged();
                                mode.finish();
                                return true;
                            }


                            for (Note note : noteSelectedList) {
                                notes.remove(note);
                                AppDatabase.getInstance(context).noteDAO().delete(note);
                            }
                            notifyDataSetChanged();
                            mode.finish();
                        }

                        if (menuItem == R.id.btnSelectAll) {
                            if (notes.size() == noteSelectedList.size()) {
                                Toast.makeText(context, "Đã chọn tất cả", Toast.LENGTH_SHORT).show();
                                return true;
                            } else {
                                isSeletedAll = true;
                                noteSelectedList.clear();
                                noteSelectedList.addAll(notes);
                            }
                            viewModel.setText(String.valueOf(noteSelectedList.size()));
                            notifyDataSetChanged();
                        }

                        if (menuItem == R.id.btnUnselectAll) {
                            isSeletedAll = false;
                            noteSelectedList.clear();
                            viewModel.setText(String.valueOf(noteSelectedList.size()));
                            notifyDataSetChanged();
                        }
                        return true;
                    }

                    @Override
                    public void onDestroyActionMode(ActionMode mode) {
                        isEnable = false;
                        isSeletedAll = false;
                        noteSelectedList.clear();
                        notifyDataSetChanged();
                    }
                };
                ((AppCompatActivity) v.getContext()).startActionMode(callback);
            } else ClickItem(holder);
            return true;
        });
    }

    private void ClickItem(NoteItemViewHolder holder) {
        int greyColor = ContextCompat.getColor(context, R.color.grey_toolNote_bg);
        Note note = notes.get(holder.getAdapterPosition());
        if (holder.binding instanceof ItemNoteListBinding) {
            if (((ItemNoteListBinding) holder.binding).checkboxSelection.getVisibility() == View.GONE) {
                ((ItemNoteListBinding) holder.binding).checkboxSelection.setVisibility(View.VISIBLE);
                holder.binding.getRoot().setBackgroundTintList(ColorStateList.valueOf(greyColor));
                noteSelectedList.add(note);
            } else {
                ((ItemNoteListBinding) holder.binding).checkboxSelection.setVisibility(View.GONE);
                holder.binding.getRoot().setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                noteSelectedList.remove(note);
            }
        }
        if (holder.binding instanceof ItemNoteGridBinding) {
            if (((ItemNoteGridBinding) holder.binding).checkboxSelection.getVisibility() == View.GONE) {
                ((ItemNoteGridBinding) holder.binding).checkboxSelection.setVisibility(View.VISIBLE);
                holder.binding.getRoot().setBackgroundTintList(ColorStateList.valueOf(greyColor));
                noteSelectedList.add(note);
            } else {
                ((ItemNoteGridBinding) holder.binding).checkboxSelection.setVisibility(View.GONE);
                holder.binding.getRoot().setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                noteSelectedList.remove(note);
            }
        }
        viewModel.setText(String.valueOf(noteSelectedList.size()));
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

    public interface OnItemClickListener {
        void onClick(Note note);
    }

    // Method to toggle between list and grid layout
    public Boolean toggleLayout() {
        isGridLayout = !isGridLayout;
        notifyDataSetChanged(); // Notify adapter to refresh the layout
        return isGridLayout;
    }

    public Boolean toggleSortResult() {
        isRandom = !isRandom;
        notifyDataSetChanged(); // Notify adapter to refresh the layout
        return isRandom;
    }
}
