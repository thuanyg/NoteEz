package com.thuanht.noteez.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import com.thuanht.noteez.R;
import com.thuanht.noteez.adapter.NoteAdapter;
import com.thuanht.noteez.database.room.AppDatabase;
import com.thuanht.noteez.databinding.ActivityHomeBinding;
import com.thuanht.noteez.model.Note;
import com.thuanht.noteez.viewmodel.NoteViewModel;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    public static final int ACTION_ADD = 0;
    public static final int ACTION_UPDATE = 1;
    private ActivityHomeBinding binding;
    private Menu mMenu;
    private SearchView searchView;
    private List<Note> notes;
    private NoteAdapter adapter;
    private ActivityResultLauncher<Intent> activityResultLauncher;

    private NoteViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this).get(NoteViewModel.class);

        initUI();
        initData();
        eventHandler();
    }

    private void initUI() {
        // Config SystemUI
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayShowTitleEnabled(true); // Ensure the title is displayed
        getSupportActionBar().show();

        // Custom
        notes = new ArrayList<>();
        adapter = new NoteAdapter(this, notes, note -> goToWriteNoteActivity(ACTION_UPDATE, note));
        binding.rcvNotes.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.rcvNotes.setAdapter(adapter);
        // S/H
        if (AppDatabase.getInstance(this).noteDAO().selectAll().size() <= 0) {
            binding.viewGroupEmptyNote.setVisibility(View.VISIBLE);
            binding.btnAddNote.setVisibility(View.GONE);
        } else {
            binding.viewGroupEmptyNote.setVisibility(View.GONE);
            binding.btnAddNote.setVisibility(View.VISIBLE);
        }
    }

    private void initData() {
        viewModel.getListNote().observe(this, _notes -> {
            if (_notes != null) {
                notes.clear();
                notes.addAll(_notes);
                adapter.notifyDataSetChanged();
            }
        });
        viewModel.setNotes(this);
    }

    private void eventHandler() {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            Intent intent = result.getData();
                            // Get value
                            if (intent != null) {
                                Note note = (Note) intent.getSerializableExtra(NoteActivity.NOTE_OBJECT_DATA_KEY);
                                int action = intent.getIntExtra(NoteActivity.NOTE_ACTION_DATA_KEY, 0);
                                if (note.getTitle() == "" && note.getNoteContent() == "") {
                                    return;
                                }
                                switch (action) {
                                    case ACTION_ADD:
                                        notes.add(0, note);
                                        adapter.notifyItemInserted(0);
                                        // Insert the note
                                        InsertNote(note);
                                        break;
                                    case ACTION_UPDATE:
                                        for (Note n: notes) {
                                            if(n.getId() == note.getId()){
                                                notes.remove(n);
                                                notes.add(0, note);
                                                break;
                                            }
                                        }
                                        adapter.notifyDataSetChanged();
                                        UpdateNote(note);
                                        break;
                                }
                                binding.viewGroupEmptyNote.setVisibility(View.GONE);
                                binding.btnAddNote.setVisibility(View.VISIBLE);
                                binding.rcvNotes.smoothScrollToPosition(0);

                            }
                        }
                    }
                });



        binding.btnGetStared.setOnClickListener(v -> goToWriteNoteActivity(ACTION_ADD, null));
        binding.btnAddNote.setOnClickListener(v -> goToWriteNoteActivity(ACTION_ADD, null));
    }

    private void UpdateNote(Note note) {
        AppDatabase.getInstance(this).noteDAO().update(note);
    }


    private void InsertNote(Note note) {
        AppDatabase.getInstance(this).noteDAO().insert(note);
    }

    private void goToWriteNoteActivity(int action, Note note) {
        Intent intent = new Intent(this, NoteActivity.class);
        if (note != null) {
            intent.putExtra(NoteActivity.NOTE_OBJECT_DATA_KEY, note);
        }
        intent.putExtra(NoteActivity.NOTE_ACTION_DATA_KEY, action);
        activityResultLauncher.launch(intent);
    }


    private void eventMenuHandler() {
        searchView = (SearchView) mMenu.getItem(0).getActionView();
        searchView.setQueryHint("Search your notes");
        searchView.setPadding(3, 3, 3, 3);

        mMenu.findItem(R.id.mi_changeSeeMode).setOnMenuItemClickListener(item -> {
            if (adapter.toggleLayout()) {
                binding.rcvNotes.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                mMenu.findItem(R.id.mi_changeSeeMode).setTitle("Chế độ danh sách");
            } else {
                binding.rcvNotes.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                mMenu.findItem(R.id.mi_changeSeeMode).setTitle("Chế độ xem lưới");
            }
            return true;
        });

        mMenu.findItem(R.id.mi_setting).setOnMenuItemClickListener(item -> {
            startActivity(new Intent(HomeActivity.this, SettingActivity.class));
            return true;
        });

        mMenu.findItem(R.id.mi_sort).setOnMenuItemClickListener(item -> {
            if(adapter.toggleSortResult()){
                try {
                    notes.clear();
                    notes.addAll(AppDatabase.getInstance(this).noteDAO().sortRandom());
                    adapter.notifyDataSetChanged();
                    mMenu.findItem(R.id.mi_sort).setTitle("Sắp xếp theo ngày tạo");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                try {
                    notes.clear();
                    notes.addAll(AppDatabase.getInstance(this).noteDAO().selectAll());
                    adapter.notifyDataSetChanged();
                    mMenu.findItem(R.id.mi_sort).setTitle("Sắp xếp ngẫu nhiên");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            return true;
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<Note> listResult = AppDatabase.getInstance(HomeActivity.this).noteDAO().searchNote(newText);
                notes.clear();
                notes.addAll(listResult);
                adapter.notifyDataSetChanged();
                return false;
            }
        });
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        mMenu = menu;
        eventMenuHandler();
        // Find the submenu item
        SubMenu subMenu = mMenu.findItem(R.id.mi_sort).getSubMenu();

        // Apply the custom style to the submenu
        if (subMenu != null) {
            Context context = new ContextThemeWrapper(this, R.style.AppTheme_PopupMenuStyle);
            @SuppressLint("RestrictedApi") MenuPopupHelper menuHelper = new MenuPopupHelper(context, (MenuBuilder) subMenu);
            menuHelper.setForceShowIcon(true); // Optional, for showing icons on the submenu
            menuHelper.show();
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}