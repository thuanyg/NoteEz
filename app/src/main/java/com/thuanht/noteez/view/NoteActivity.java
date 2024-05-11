package com.thuanht.noteez.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.thuanht.noteez.R;
import com.thuanht.noteez.databinding.ActivityNoteBinding;
import com.thuanht.noteez.model.Note;
import com.thuanht.noteez.utils.DateUtils;
import com.thuanht.noteez.utils.DialogUtils;
import com.thuanht.noteez.viewmodel.NoteViewModel;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import jp.wasabeef.richeditor.RichEditor;

public class NoteActivity extends AppCompatActivity {
    public static final String NOTE_OBJECT_DATA_KEY = "Note_Data_Key";
    public static final String NOTE_ACTION_DATA_KEY = "Note_Action_Key";
    public int ACTION = 0;
    private ActivityNoteBinding binding;
    private TextView mPreview;
    private RichEditor mEditor;
    private int noteReceivedID = 0;
    private NoteViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNoteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this).get(NoteViewModel.class);

        initUI();
        initEditorTool();
        eventHandler();
        noteEditEventHanlder();
    }

    @SuppressLint("DefaultLocale")
    private void initDataFromHome(Note note) {
        binding.txtTitle.setText(note.getTitle());
        binding.tvDate.setText(note.getDate());
        Spanned spannedText = Html.fromHtml(note.getNoteContent());
        mPreview.setText(note.getNoteContent());
        binding.tvCountNote.setText(String.format("%d", spannedText.length()));
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
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        getSupportActionBar().setDisplayShowTitleEnabled(true); // Ensure the title is displayed
        getSupportActionBar().show();
    }

    @SuppressLint({"UseCompatTextViewDrawableApis", "DefaultLocale"})
    private void initEditorTool() {
        mPreview = binding.tvPreview;
        mEditor = binding.editor;
        mEditor.setEditorHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        mEditor.setEditorFontSize(16);
        mEditor.setPadding(10, 5, 0, 5);
        mEditor.setEditorFontColor(Color.BLACK);
        mEditor.setEditorBackgroundColor(Color.WHITE);
        mEditor.setPadding(10, 10, 10, 10);
        mEditor.setPlaceholder("Write your note here ...");
        mEditor.setOnTextChangeListener(text -> {
            Note note = new Note();
            note.setNoteContent(text);
            viewModel.setNote(note);

            if (text.isEmpty()) {
                ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.grey_icon));
                binding.btnUndo.setCompoundDrawableTintList(colorStateList);
            } else {
                ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.black));
                binding.btnUndo.setCompoundDrawableTintList(colorStateList);
            }

        });

        viewModel.getNote().observe(this, note -> {
            Spanned spannedText = Html.fromHtml(note.getNoteContent()); // Convert HTML to plain text
            mPreview.setText(note.getNoteContent());
            binding.tvCountNote.setText(String.format("%d", spannedText.length()));
        });
    }

    private void noteEditEventHanlder() {
        AtomicReference<Boolean> isBold = new AtomicReference<>(false);
        AtomicReference<Boolean> isUnderline = new AtomicReference<>(false);
        AtomicReference<Boolean> isItalic = new AtomicReference<>(false);

        int colorActive = ContextCompat.getColor(this, R.color.grey_toolNote_bg);
        int colorWhite = ContextCompat.getColor(this, R.color.white);
        ColorStateList csl_ToolActiveColor = ColorStateList.valueOf(colorActive);
        ColorStateList csl_ToolUnActiveColor = ColorStateList.valueOf(colorWhite);

        binding.btnUndo.setOnClickListener(v -> mEditor.undo());
        binding.btnRedo.setOnClickListener(v -> mEditor.redo());

        binding.btnToolBold.setOnClickListener(v -> {
            mEditor.setBold();
            if (isBold.get()) {
                binding.btnToolBold.setBackgroundTintList(csl_ToolUnActiveColor);
                isBold.set(false);
            } else {
                binding.btnToolBold.setBackgroundTintList(csl_ToolActiveColor);
                isBold.set(true);
            }
        });

        binding.btnToolUnderline.setOnClickListener(v -> {
            mEditor.setUnderline();
            if (isUnderline.get()) {
                binding.btnToolUnderline.setBackgroundTintList(csl_ToolUnActiveColor);
                isUnderline.set(false);
            } else {
                binding.btnToolUnderline.setBackgroundTintList(csl_ToolActiveColor);
                isUnderline.set(true);
            }
        });
        binding.btnToolItalic.setOnClickListener(v -> {
            mEditor.setItalic();
            if (isItalic.get()) {
                binding.btnToolItalic.setBackgroundTintList(csl_ToolUnActiveColor);
                isItalic.set(false);
            } else {
                binding.btnToolItalic.setBackgroundTintList(csl_ToolActiveColor);
                isItalic.set(true);
            }
        });
        binding.btnToolList.setOnClickListener(v -> mEditor.setBullets());
        binding.btnToolAdjLeft.setOnClickListener(v -> mEditor.setAlignLeft());
        binding.btnToolAdjCenter.setOnClickListener(v -> mEditor.setAlignCenter());
        binding.btnToolAdjRight.setOnClickListener(v -> mEditor.setAlignRight());
    }

    boolean doNothing = false;

    private void eventHandler() {
        mEditor.focusEditor();
        binding.toolbarNote.setNavigationOnClickListener(v -> DialogUtils.getInstance().showDialog(this, "Bạn chắc chắn muốn thoát? Ghi chú sẽ không được lưu", this::finish));
        // Get intent data
        Intent receivedIntent = getIntent();
        if (receivedIntent != null) {
            try {
                Note note = (Note) receivedIntent.getSerializableExtra(NOTE_OBJECT_DATA_KEY);
                noteReceivedID = note.getId();
                initDataFromHome(note);
            } catch (Exception e) {
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
        if (!doNothing) {
            Note note = new Note();
            note.setTitle(title);
            note.setNoteContent(content);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                note.setDate(DateUtils.getInstance().getCurrentDateTime());
            }
            Intent receivedIntent = getIntent();
            if (receivedIntent != null) {
                try {
                    ACTION = receivedIntent.getIntExtra(NOTE_ACTION_DATA_KEY, 0);
                    if (ACTION == HomeActivity.ACTION_UPDATE) {
                        note.setId(noteReceivedID);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            NavigateToHome(ACTION, note);
            finish();
        }
    }

    public void NavigateToHome(int action, Note note) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            note.setDate(DateUtils.getInstance().getCurrentDateTime());
        }
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra(NOTE_OBJECT_DATA_KEY, note);
        intent.putExtra(NOTE_ACTION_DATA_KEY, action);
        setResult(RESULT_OK, intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu_edit, menu);
        menu.findItem(R.id.mi_save).setOnMenuItemClickListener(item -> {
            backAndSave();
            return false;
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        DialogUtils.getInstance().showDialog(this, "Bạn chắc chắn muốn thoát? Ghi chú sẽ không được lưu", () -> finish());
    }
}