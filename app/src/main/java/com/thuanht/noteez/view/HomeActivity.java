package com.thuanht.noteez.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;

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

import com.thuanht.noteez.R;
import com.thuanht.noteez.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity {

    public static final String NOTE_OBJECT_INTENT_KEY = "Note_Intent_Key";
    private ActivityHomeBinding binding;
    private Menu mMenu;
    private SearchView searchView;
    private ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayShowTitleEnabled(true); // Ensure the title is displayed
        getSupportActionBar().show();

        eventHandler();
    }

    private void eventHandler() {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode() == RESULT_OK){
                            Intent intent = result.getData();
                            // Get value
                        }
                    }
                });

        binding.btnGetStared.setOnClickListener(v -> {
            goToWriteNoteActivity();
        });
    }

    private void goToWriteNoteActivity() {
        Intent intent = new Intent(this, NoteActivity.class);
        activityResultLauncher.launch(intent);
    }


    private void eventMenuHandler() {
        searchView = (SearchView) mMenu.getItem(0).getActionView();
        searchView.setQueryHint("Search your notes");

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
//        MenuItem.OnActionExpandListener onActionExpandListener = new MenuItem.OnActionExpandListener() {
//            @Override
//            public boolean onMenuItemActionExpand(@NonNull MenuItem item) {
//                return false;
//            }
//
//            @Override
//            public boolean onMenuItemActionCollapse(@NonNull MenuItem item) {
//                return false;
//            }
//        };
//        menu.findItem(R.id.mi_search).setOnActionExpandListener(onActionExpandListener);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}