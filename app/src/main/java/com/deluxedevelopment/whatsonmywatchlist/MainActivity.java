package com.deluxedevelopment.whatsonmywatchlist;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.deluxedevelopment.whatsonmywatchlist.adapter.WatchlistAdapter;
import com.deluxedevelopment.whatsonmywatchlist.database.WatchlistDbHelper;
import com.deluxedevelopment.whatsonmywatchlist.dialog.AddEditDialog;
import com.deluxedevelopment.whatsonmywatchlist.model.WatchItem;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements WatchlistAdapter.OnItemClickListener {
    private RecyclerView recyclerView;
    private WatchlistAdapter adapter;
    private WatchlistDbHelper dbHelper;
    private ArrayList<WatchItem> watchItems;
    private TextView emptyTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);

        dbHelper = new WatchlistDbHelper(this);
        watchItems = new ArrayList<>();

        recyclerView = findViewById(R.id.recycler_watchlist);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        emptyTextView = findViewById(R.id.text_empty);

        adapter = new WatchlistAdapter(this);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fab_add);
        fab.setOnClickListener(v -> showAddItemDialog());

        loadWatchItems();
    }

    @Override
    public void onItemClick(WatchItem item) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_item, null);
        AddEditDialog dialog = new AddEditDialog(this, dialogView);
        dialog.setItem(item);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Update");
        builder.setView(dialogView);

        final androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
        MaterialButton dismissButton = dialogView.findViewById(R.id.btnAddToWatchlist);
        TextInputEditText titleInput = dialogView.findViewById(R.id.input_title);
        CoordinatorLayout layoutMain = findViewById(R.id.layoutMain);
        ChipGroup statusChipGroup = dialogView.findViewById(R.id.statusChipGroup);
        dismissButton.setOnClickListener(v -> {
            if (titleInput.getText().toString().trim().isEmpty()) {
                Snackbar.make(layoutMain, "title field cannot be blank", Snackbar.LENGTH_LONG).show();
            } else if(statusChipGroup.getCheckedChipId() == View.NO_ID){
                Snackbar.make(layoutMain, "did you watch this?", Snackbar.LENGTH_LONG).show();
            } else {
                WatchItem updatedItem = dialog.getItem();
                updatedItem.setId(item.getId());
                updateWatchItem(updatedItem);
                adapter.updateItem(updatedItem);
                alertDialog.dismiss();
                showToast(getApplicationContext(), "updated");
            }
        });
    }

    @Override
    public void onItemLongClick(WatchItem item) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Delete Show?")
                .setMessage("Are you sure you want to do this? This cannot be undone.")
                .setPositiveButton("Yes", (dialog, which) -> {
                    deleteWatchItem(item);
                    adapter.removeItem(item);
                    showToast(getApplicationContext(), "deleted");
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void showAddItemDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_item, null);
        AddEditDialog dialog = new AddEditDialog(this, dialogView);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Add a Show");
        builder.setView(dialogView);

        final androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
        MaterialButton dismissButton = dialogView.findViewById(R.id.btnAddToWatchlist);
        TextInputEditText titleInput = dialogView.findViewById(R.id.input_title);
        CoordinatorLayout layoutMain = findViewById(R.id.layoutMain);
        ChipGroup statusChipGroup = dialogView.findViewById(R.id.statusChipGroup);
        dismissButton.setOnClickListener(v -> {
            if (titleInput.getText().toString().trim().isEmpty()) {
                Snackbar.make(layoutMain, "title field cannot be blank", Snackbar.LENGTH_LONG).show();
            } else if(statusChipGroup.getCheckedChipId() == View.NO_ID){
                Snackbar.make(layoutMain, "did you watch this?", Snackbar.LENGTH_LONG).show();
            } else {
                WatchItem newItem = dialog.getItem();
                saveWatchItem(newItem);
                loadWatchItems();
                alertDialog.dismiss();
                updateEmptyState(watchItems);
                showToast(getApplicationContext(), "added");
            }
        });
    }

    // Update other methods to handle empty state
    private void saveWatchItem(WatchItem item) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(WatchlistDbHelper.COLUMN_TITLE, item.getTitle());
        values.put(WatchlistDbHelper.COLUMN_TYPE, item.getType());
        values.put(WatchlistDbHelper.COLUMN_STATUS, item.getStatus().toString());
        values.put(WatchlistDbHelper.COLUMN_LIKED, item.getLiked() != null ?
                (item.getLiked() ? 1 : 0) : null);

        db.insert(WatchlistDbHelper.TABLE_WATCHLIST, null, values);
        loadWatchItems(); // Reload to update empty state
    }

    private void deleteWatchItem(WatchItem item) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(WatchlistDbHelper.TABLE_WATCHLIST,
                WatchlistDbHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(item.getId())});

        watchItems.remove(item);
        adapter.removeItem(item);
        updateEmptyState(watchItems);
    }

    private void updateWatchItem(WatchItem item) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(WatchlistDbHelper.COLUMN_TITLE, item.getTitle());
        values.put(WatchlistDbHelper.COLUMN_TYPE, item.getType());
        values.put(WatchlistDbHelper.COLUMN_STATUS, item.getStatus().toString());
        values.put(WatchlistDbHelper.COLUMN_LIKED, item.getLiked() != null ?
                (item.getLiked() ? 1 : 0) : null);

        db.update(WatchlistDbHelper.TABLE_WATCHLIST,
                values,
                WatchlistDbHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(item.getId())});
    }

    @SuppressLint("Range")
    private void loadWatchItems() {
        watchItems.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(WatchlistDbHelper.TABLE_WATCHLIST,
                null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            WatchItem item = new WatchItem(
                    cursor.getString(cursor.getColumnIndex(WatchlistDbHelper.COLUMN_TITLE)),
                    cursor.getString(cursor.getColumnIndex(WatchlistDbHelper.COLUMN_TYPE))
            );
            item.setId(cursor.getLong(cursor.getColumnIndex(WatchlistDbHelper.COLUMN_ID)));
            item.setStatus(WatchItem.WatchStatus.valueOf(
                    cursor.getString(cursor.getColumnIndex(WatchlistDbHelper.COLUMN_STATUS))));

            int likedColumnIndex = cursor.getColumnIndex(WatchlistDbHelper.COLUMN_LIKED);
            if (!cursor.isNull(likedColumnIndex)) {
                item.setLiked(cursor.getInt(likedColumnIndex) == 1);
            }

            watchItems.add(item);
        }
        cursor.close();
        adapter.updateItems(watchItems);
        updateEmptyState(watchItems);
    }
    @Override
    public void onStatusChange(WatchItem item, WatchItem.WatchStatus newStatus) {
        item.setStatus(newStatus);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(WatchlistDbHelper.COLUMN_STATUS, newStatus.toString());

        db.update(WatchlistDbHelper.TABLE_WATCHLIST,
                values,
                WatchlistDbHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(item.getId())});

        adapter.updateItem(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Search Shows...");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterItems(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterItems(newText);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void filterItems(String query) {
        if (query.isEmpty()) {
            adapter.updateItems(watchItems);
            updateEmptyState(watchItems);
            loadWatchItems();
            return;
        }

        String lowerQuery = query.toLowerCase();
        List<WatchItem> filteredList = watchItems.stream()
                .filter(item -> item.getTitle().toLowerCase().contains(lowerQuery))
                .collect(Collectors.toList());

        adapter.updateItems(filteredList);
        updateEmptyState(filteredList);
    }
    private void showToast(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
    private void updateEmptyState(List<WatchItem> items) {
        if (items.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyTextView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyTextView.setVisibility(View.GONE);
        }
    }
}