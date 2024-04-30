package com.thuanht.noteez.view;

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
import com.thuanht.noteez.databinding.ActivityNoteBinding;
import com.thuanht.noteez.utils.DateUtils;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import jp.wasabeef.richeditor.RichEditor;

public class NoteActivity extends AppCompatActivity {
    private ActivityNoteBinding binding;
    private Menu mMenu;
    private RichEditor mEditor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNoteBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
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

        initEditorTool();
        eventHandler();
        noteEditEventHanlder();
    }

    private void initEditorTool() {
        TextView mPreview = binding.tvPreview;
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

    private void eventHandler() {
        mEditor.focusEditor();
        mEditor.requestFocus();


        binding.toolbarNote.setNavigationOnClickListener(v -> {
            finish();
        });
        binding.btnAddIMG.setOnClickListener(v -> {
//            EditText editText = findViewById(R.id.txtNoteContent);
//            SpannableStringBuilder builder = new SpannableStringBuilder(editText.getText());
//
//// Lấy vị trí hiện tại của con trỏ trong EditText
//            int cursorPosition = editText.getSelectionStart();
//
//// Chèn hình ảnh vào vị trí hiện tại của con trỏ trong văn bản của EditText
//            Drawable drawable = getResources().getDrawable(R.drawable.illustration); // Thay your_image bằng ID của hình ảnh bạn muốn chèn
//            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
//            ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
//            builder.insert(cursorPosition, " "); // Chèn một ký tự trắng trước khi chèn hình ảnh để tránh việc hình ảnh bị chồng lên văn bản
//            builder.setSpan(imageSpan, cursorPosition, cursorPosition + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
//            editText.setText(builder);
//            editText.setSelection(cursorPosition + 1); // Di chuyển con trỏ sang sau hình ảnh
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu_edit, menu);
        mMenu = menu;
        return super.onCreateOptionsMenu(menu);
    }
}