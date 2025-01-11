package com.deluxedevelopment.whatsonmywatchlist.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.deluxedevelopment.whatsonmywatchlist.R;
import com.deluxedevelopment.whatsonmywatchlist.model.WatchItem;
import com.google.android.material.textfield.TextInputLayout;

public class AddEditDialog {
    private final View dialogView;
    private final TextInputEditText titleInput;
    private final MaterialButtonToggleGroup toggleGroup;
    private final ChipGroup statusChipGroup;
    private final Button thumbsUpButton;
    private final Button thumbsDownButton;
    private final MaterialButton addButton;
    private final LinearLayout ratingLayout;
    private Boolean isLiked = null;

    @SuppressLint("WrongViewCast")
    public AddEditDialog(Context context, View dialogView) {
        this.dialogView = dialogView;

        titleInput = dialogView.findViewById(R.id.input_title);
        toggleGroup = dialogView.findViewById(R.id.toggleGroup);
        statusChipGroup = dialogView.findViewById(R.id.statusChipGroup);
        thumbsUpButton = dialogView.findViewById(R.id.btnThumsUp);
        thumbsDownButton = dialogView.findViewById(R.id.btnThumpsDown);
        addButton = dialogView.findViewById(R.id.btnAddToWatchlist);
        ratingLayout = dialogView.findViewById(R.id.ratingLayout);

        setupRatingButtons();
        setupStatusChipGroup();
    }

    private void setupStatusChipGroup() {
        statusChipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            Chip chip = group.findViewById(checkedId);
            if (chip != null) {
                boolean isWatched = chip.getId() == R.id.chipWatched;
                ratingLayout.setVisibility(isWatched ? View.VISIBLE : View.GONE);
                if (!isWatched) {
                    // Reset rating when status changes to not watched
                    isLiked = null;
                    updateRatingButtons();
                }
            }
        });
    }

    private void setupRatingButtons() {
        thumbsUpButton.setOnClickListener(v -> {
            // Toggle between liked and neutral
            isLiked = !Boolean.TRUE.equals(isLiked);
            updateRatingButtons();
        });

        thumbsDownButton.setOnClickListener(v -> {
            // Toggle between disliked and neutral
            isLiked = Boolean.FALSE.equals(isLiked) ? null : false;
            updateRatingButtons();
        });
    }

    private void updateRatingButtons() {
        // Update button states based on isLiked value
        thumbsUpButton.setSelected(Boolean.TRUE.equals(isLiked));
        thumbsDownButton.setSelected(Boolean.FALSE.equals(isLiked));
    }

    public void setItem(WatchItem item) {
        titleInput.setText(item.getTitle());
        toggleGroup.check(item.getType().equals("Movie") ? R.id.btnMovie : R.id.btnWebSeries);

        Chip statusChip = item.getStatus() == WatchItem.WatchStatus.WATCHED ?
                dialogView.findViewById(R.id.chipWatched) :
                dialogView.findViewById(R.id.chipWantToWatch);
        statusChip.setChecked(true);

        // Set initial like status and update button states
        isLiked = item.getLiked();
        updateRatingButtons();

        addButton.setText("Update");
        addButton.setIcon(null);
    }

    public WatchItem getItem() {
        String title = titleInput.getText().toString();
        String type = toggleGroup.getCheckedButtonId() == R.id.btnMovie ? "Movie" : "Series";

        WatchItem item = new WatchItem(title, type);
        item.setStatus(statusChipGroup.getCheckedChipId() == R.id.chipWatched ?
                WatchItem.WatchStatus.WATCHED : WatchItem.WatchStatus.NOT_WATCHED);
        item.setLiked(isLiked);

        return item;
    }

    public void setOnAddClickListener(View.OnClickListener listener) {
        addButton.setOnClickListener(listener);
    }
}