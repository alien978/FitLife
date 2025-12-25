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
import com.example.fitlife.utils.SessionManager;
import java.util.ArrayList;
import java.util.List;

public class RoutineListActivity extends AppCompatActivity {

    private WorkoutRoutineRepository repository;
    private RoutineAdapter adapter;
    private SessionManager sessionManager;
    private List<WorkoutRoutine> routineList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine_list);

        repository = new WorkoutRoutineRepository(getApplication());
        sessionManager = new SessionManager(this);

        RecyclerView recycler = findViewById(R.id.recyclerRoutines);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new RoutineAdapter(routineList, this);
        recycler.setAdapter(adapter);

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
        int userId = sessionManager.getUserId();
        routineList.clear();
        // SECURE: Only load routines for the current user
        routineList.addAll(repository.getAllRoutines(userId));
        adapter.notifyDataSetChanged();
    }

    private class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {
        private final Drawable deleteIcon;
        private final ColorDrawable background;

        SwipeToDeleteCallback() {
            super(0, ItemTouchHelper.LEFT);
            deleteIcon = ContextCompat.getDrawable(RoutineListActivity.this, R.drawable.ic_delete);
            background = new ColorDrawable(Color.RED);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView r, @NonNull RecyclerView.ViewHolder vh, @NonNull RecyclerView.ViewHolder t) { return false; }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            showDeleteConfirmationDialog(viewHolder.getAdapterPosition());
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView r, @NonNull RecyclerView.ViewHolder vh, float dX, float dY, int s, boolean active) {
            super.onChildDraw(c, r, vh, dX, dY, s, active);
            View itemView = vh.itemView;
            if (dX < 0) {
                background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                background.draw(c);
                int iconTop = itemView.getTop() + (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
                int iconMargin = (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
                deleteIcon.setBounds(itemView.getRight() - iconMargin - deleteIcon.getIntrinsicWidth(), iconTop, itemView.getRight() - iconMargin, iconTop + deleteIcon.getIntrinsicHeight());
                deleteIcon.draw(c);
            }
        }
    }

    private void showDeleteConfirmationDialog(int position) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Routine")
                .setMessage("Are you sure?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    repository.delete(routineList.get(position));
                    loadRoutines();
                })
                .setNegativeButton("Cancel", (dialog, which) -> adapter.notifyItemChanged(position))
                .setOnCancelListener(dialog -> adapter.notifyItemChanged(position))
                .show();
    }
}
