package omari.hamza.storyview.callback;

import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;

public interface OnPopupItemListener {
    void onCreatePopupMenu(@NonNull PopupMenu popupMenu, @NonNull View v);

    boolean onPopupItemSelected(@NonNull MenuItem item);
}
