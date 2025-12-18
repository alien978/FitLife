package com.example.fitlife.ui.routine;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fitlife.R;
import com.example.fitlife.data.model.WorkoutRoutine;
import com.example.fitlife.data.repository.WorkoutRoutineRepository;
import java.util.ArrayList;
import java.util.List;

public class RoutineListActivity extends AppCompatActivity {

    private WorkoutRoutineRepository repository;
    private RoutineAdapter adapter;
    private List<WorkoutRoutine> routineList = new ArrayList<>();
    private View rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine_list);

        rootLayout = findViewById(R.id.main);
        repository = new WorkoutRoutineRepository(getApplication());

        RecyclerView recycler = findViewById(R.id.recyclerRoutines);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new RoutineAdapter(routineList, this);
        recycler.setAdapter(adapter);

        // Attach the custom swipe helper
        new ItemTouchHelper(new SwipeToDeleteCallback()).attachToRecyclerView(recycler);

        findViewById(R.id.btnAddRoutine).setOnClickListener(v -> 
            startActivity(new Intent(this, EditRoutineActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRoutines();
    }

    private void loadRoutines() {
        routineList.clear();
        routineList.addAll(repository.getAllRoutines());
        adapter.notifyDataSetChanged();
    }

    /**
     * Custom ItemTouchHelper.Callback to draw the delete button and show a confirmation dialog.
     */
    private class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {

        private final Drawable deleteIcon;
        private final ColorDrawable background;

        SwipeToDeleteCallback() {
            super(0, ItemTouchHelper.LEFT);
            deleteIcon = ContextCompat.getDrawable(RoutineListActivity.this, R.drawable.ic_delete);
            background = new ColorDrawable(Color.RED);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView r, @NonNull RecyclerView.ViewHolder vh, @NonNull RecyclerView.ViewHolder t) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            // Show confirmation dialog instead of deleting instantly
            showDeleteConfirmationDialog(position);
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            View itemView = viewHolder.itemView;

            if (dX < 0) { // Swiping to the left
                background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                background.draw(c);

                int iconTop = itemView.getTop() + (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
                int iconMargin = (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
                int iconLeft = itemView.getRight() - iconMargin - deleteIcon.getIntrinsicWidth();
                int iconRight = itemView.getRight() - iconMargin;
                int iconBottom = iconTop + deleteIcon.getIntrinsicHeight();

                deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                deleteIcon.draw(c);
            }
        }
    }

    private void showDeleteConfirmationDialog(int position) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Routine")
                .setMessage("Are you sure you want to delete this routine?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    WorkoutRoutine routineToDelete = routineList.get(position);
                    repository.delete(routineToDelete);
                    loadRoutines();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    // User cancelled, refresh the adapter to bring the item back
                    adapter.notifyItemChanged(position);
                })
                .setOnCancelListener(dialog -> {
                    // If user cancels by tapping outside, also bring the item back
                    adapter.notifyItemChanged(position);
                })
                .show();
    }
}
