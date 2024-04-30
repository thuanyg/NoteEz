package com.thuanht.noteez.view;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.thuanht.noteez.R;
import com.thuanht.noteez.database.room.AppDatabase;
import com.thuanht.noteez.databinding.ActivityNoteBinding;
import com.thuanht.noteez.model.Note;
import com.thuanht.noteez.utils.DateUtils;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import jp.wasabeef.richeditor.RichEditor;

public class NoteActivity extends AppCompatActivity {
    public static final String NOTE_OBJECT_DATA_KEY = "Note_Data_Key";

    private ActivityNoteBinding binding;
    private Menu mMenu;
    private TextView mPreview;
    private RichEditor mEditor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNoteBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        initUI();
        initEditorTool();
        eventHandler();
        noteEditEventHanlder();
    }

    private void initDataFromHome(Note note) {
        binding.txtTitle.setText(note.getTitle());
        binding.tvDate.setText(note.getDate());
        Spanned spannedText = Html.fromHtml(note.getNoteContent());
        mPreview.setText(note.getNoteContent());
        binding.tvCountNote.setText(spannedText.length() + "");
        mEditor.setHtml(note.getNoteContent());
    }

    private void initUI() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding.tvDate.setText(DateUtils.getInstance().getCurrentDateTime());
        }
        setSupportActionBar(binding.toolbarNote);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayShowTitleEnabled(true); // Ensure the title is displayed
        getSupportActionBar().show();
    }

    private void initEditorTool() {
        mPreview = binding.tvPreview;
        mEditor = binding.editor;
        mEditor.setEditorHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        mEditor.setEditorFontSize(16);
        mEditor.setPadding(10, 5, 0, 5);
        mEditor.setEditorFontColor(Color.BLACK);
        mEditor.setEditorBackgroundColor(Color.WHITE);
        mEditor.setPadding(10, 10, 10, 10);
        //mEditor.setBackground("https://raw.githubusercontent.com/wasabeef/art/master/chip.jpg");
        mEditor.setPlaceholder("Write your note here ...");

        mEditor.setOnTextChangeListener(new RichEditor.OnTextChangeListener() {
            @Override
            public void onTextChange(String text) {
                if(text.isEmpty()){
                    ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.grey_icon));
                    binding.btnUndo.setCompoundDrawableTintList(colorStateList);
                } else {
                    ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.black));
                    binding.btnUndo.setCompoundDrawableTintList(colorStateList);
                }

                Spanned spannedText = Html.fromHtml(text); // Convert HTML to plain text
                mPreview.setText(text);
                binding.tvCountNote.setText(spannedText.length() + "");
            }
        });
    }

    private void noteEditEventHanlder() {
        binding.btnUndo.setOnClickListener(v -> mEditor.undo());
        binding.btnRedo.setOnClickListener(v -> mEditor.redo());
    }
    boolean doNothing = false;
    private void eventHandler() {
        mEditor.focusEditor();
        mEditor.requestFocus();
        binding.toolbarNote.setNavigationOnClickListener(v -> {
            backAndSave();
        });
        // Get intent data
        Intent receivedIntent = getIntent();
        if(receivedIntent != null){
            try {
                Note note = (Note) receivedIntent.getSerializableExtra(NOTE_OBJECT_DATA_KEY);
                initDataFromHome(note);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void backAndSave() {
        String title = binding.txtTitle.getText().toString();
        String content = mPreview.getText().toString();
        if (TextUtils.isEmpty(title) && TextUtils.isEmpty(content)) {
            doNothing = true;
            finish();
        }
        if(!doNothing){
            Note note = new Note();
            note.setTitle(title);
            note.setNoteContent(content);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                note.setDate(DateUtils.getInstance().getCurrentDateTime());
            }
            NavigateToHome(note);
            finish();
        }
    }

    public void NavigateToHome(Note note){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            note.setDate(DateUtils.getInstance().getCurrentDateTime());
        }
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra(NOTE_OBJECT_DATA_KEY, note);
        setResult(RESULT_OK, intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu_edit, menu);
        mMenu = menu;
        mMenu.findItem(R.id.mi_save).setOnMenuItemClickListener(item -> {
            backAndSave();
            return false;
        });
        return super.onCreateOptionsMenu(menu);
    }
}