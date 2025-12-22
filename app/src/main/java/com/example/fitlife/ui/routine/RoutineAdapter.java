package com.example.fitlife.ui.routine;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fitlife.R;
import com.example.fitlife.data.model.WorkoutRoutine;
import com.example.fitlife.data.repository.WorkoutRoutineRepository;
import com.example.fitlife.data.repository.GymLocationRepository;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.List;

public class RoutineAdapter extends RecyclerView.Adapter<RoutineAdapter.RoutineViewHolder> {

    private final List<WorkoutRoutine> routines;
    private final RoutineListActivity context;
    private final WorkoutRoutineRepository repository;
    private final GymLocationRepository locationRepository;

    public RoutineAdapter(List<WorkoutRoutine> routines, RoutineListActivity context) {
        this.routines = routines;
        this.context = context;
        this.repository = new WorkoutRoutineRepository(context.getApplication());
        this.locationRepository = new GymLocationRepository(context.getApplication());
    }

    @NonNull
    @Override
    public RoutineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_routine, parent, false);
        return new RoutineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoutineViewHolder holder, int position) {
        WorkoutRoutine routine = routines.get(position);
        holder.bind(routine);
    }

    @Override
    public int getItemCount() {
        return routines.size();
    }

    class RoutineViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvRoutineName, tvExercisePreview, tvFullExercises, tvFullEquipment, tvLocationNameSmall;
        private final CheckBox cbCompleted;
        private final ImageButton ibMenu;
        private final LinearLayout layoutExpandable, layoutLocationBadge, layoutFullLocation;
        private final ImageView ivListThumbnail, ivFullImage;
        private final MaterialCardView cardRoutine;
        private final Button btnOpenInMaps;

        public RoutineViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRoutineName = itemView.findViewById(R.id.tvRoutineName);
            tvExercisePreview = itemView.findViewById(R.id.tvExercisePreview);
            tvFullExercises = itemView.findViewById(R.id.tvFullExercises);
            tvFullEquipment = itemView.findViewById(R.id.tvFullEquipment);
            tvLocationNameSmall = itemView.findViewById(R.id.tvLocationNameSmall);
            cbCompleted = itemView.findViewById(R.id.cbCompleted);
            ibMenu = itemView.findViewById(R.id.ibMenu);
            layoutExpandable = itemView.findViewById(R.id.layoutExpandable);
            layoutLocationBadge = itemView.findViewById(R.id.layoutLocationBadge);
            layoutFullLocation = itemView.findViewById(R.id.layoutFullLocation);
            ivListThumbnail = itemView.findViewById(R.id.ivListThumbnail);
            ivFullImage = itemView.findViewById(R.id.ivFullImage);
            cardRoutine = itemView.findViewById(R.id.cardRoutine);
            btnOpenInMaps = itemView.findViewById(R.id.btnOpenInMaps);
        }

        public void bind(final WorkoutRoutine routine) {
            tvRoutineName.setText(routine.name);
            cbCompleted.setChecked(routine.isCompleted);

            if (routine.locationName != null && !routine.locationName.isEmpty()) {
                tvLocationNameSmall.setText(routine.locationName);
                layoutLocationBadge.setVisibility(View.VISIBLE);
                layoutFullLocation.setVisibility(View.VISIBLE);
                btnOpenInMaps.setOnClickListener(v -> {
                    com.example.fitlife.data.model.GymLocation loc = locationRepository.getLocationById(routine.locationId);
                    if (loc != null) {
                        String geoUri = "geo:0,0?q=" + loc.latitude + "," + loc.longitude + "(" + Uri.encode(loc.name) + ")";
                        Uri gmmIntentUri = Uri.parse(geoUri);
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
                            context.startActivity(mapIntent);
                        } else {
                            context.startActivity(new Intent(Intent.ACTION_VIEW, gmmIntentUri));
                        }
                    }
                });
            } else {
                layoutLocationBadge.setVisibility(View.GONE);
                layoutFullLocation.setVisibility(View.GONE);
            }

            if (routine.exercises != null && !routine.exercises.isEmpty()) {
                String[] lines = routine.exercises.split("\n");
                tvExercisePreview.setText(lines.length + " exercises • Tap to view details");
            } else {
                tvExercisePreview.setText("No exercises • Tap to view details");
            }

            tvFullExercises.setText(formatNumberedList(routine.exercises));
            tvFullEquipment.setText(formatBulletedList(routine.equipment));

            if (routine.imageUri != null && !routine.imageUri.isEmpty()) {
                try {
                    Uri uri = Uri.parse(routine.imageUri);
                    ivListThumbnail.setImageURI(uri);
                    ivListThumbnail.setVisibility(View.VISIBLE);
                    ivFullImage.setImageURI(uri);
                    ivFullImage.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    ivListThumbnail.setVisibility(View.GONE);
                    ivFullImage.setVisibility(View.GONE);
                }
            } else {
                ivListThumbnail.setVisibility(View.GONE);
                ivFullImage.setVisibility(View.GONE);
            }

            cardRoutine.setOnClickListener(v -> {
                if (layoutExpandable.getVisibility() == View.VISIBLE) {
                    layoutExpandable.setVisibility(View.GONE);
                    tvExercisePreview.setVisibility(View.VISIBLE);
                } else {
                    layoutExpandable.setVisibility(View.VISIBLE);
                    tvExercisePreview.setVisibility(View.GONE);
                }
            });

            ibMenu.setOnClickListener(v -> showPopupMenu(v, routine));

            cbCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (routine.isCompleted != isChecked) {
                    routine.isCompleted = isChecked;
                    repository.update(routine);
                }
            });
        }

        private void showPopupMenu(View view, WorkoutRoutine routine) {
            PopupMenu popup = new PopupMenu(context, view);
            popup.getMenu().add("Edit Routine");
            popup.getMenu().add("Send SMS Checklist");

            popup.setOnMenuItemClickListener(item -> {
                String title = item.getTitle().toString();
                if (title.equals("Edit Routine")) {
                    Intent intent = new Intent(context, EditRoutineActivity.class);
                    intent.putExtra("ROUTINE_ID", routine.id);
                    context.startActivity(intent);
                } else if (title.equals("Send SMS Checklist")) {
                    checkSmsPermission(routine);
                }
                return true;
            });
            popup.show();
        }

        private void checkSmsPermission(WorkoutRoutine routine) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.SEND_SMS}, 101);
            } else {
                showInAppSmsDialog(routine);
            }
        }

        private void showInAppSmsDialog(WorkoutRoutine routine) {
            View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_send_sms, null);
            TextInputEditText etPhone = dialogView.findViewById(R.id.etSmsPhoneNumber);
            TextInputEditText etMsg = dialogView.findViewById(R.id.etSmsMessage);
            Button btnCancel = dialogView.findViewById(R.id.btnCancelSms);
            Button btnSend = dialogView.findViewById(R.id.btnConfirmSendSms);

            String prefilledMsg = "FitLife Checklist: " + routine.name + "\n" +
                    "Location: " + (routine.locationName != null ? routine.locationName : "None") + "\n\n" +
                    "Equipment:\n" + formatBulletedList(routine.equipment) + 
                    "\n\nExercises:\n" + routine.exercises;
            etMsg.setText(prefilledMsg);

            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setView(dialogView)
                    .create();

            btnCancel.setOnClickListener(v -> dialog.dismiss());
            btnSend.setOnClickListener(v -> {
                String phone = etPhone.getText().toString().trim();
                String msg = etMsg.getText().toString().trim();

                if (phone.isEmpty()) {
                    Toast.makeText(context, "Please enter a phone number", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    SmsManager smsManager;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        smsManager = context.getSystemService(SmsManager.class);
                    } else {
                        smsManager = SmsManager.getDefault();
                    }
                    
                    if (msg.length() > 160) {
                        ArrayList<String> parts = smsManager.divideMessage(msg);
                        smsManager.sendMultipartTextMessage(phone, null, parts, null, null);
                    } else {
                        smsManager.sendTextMessage(phone, null, msg, null, null);
                    }
                    
                    Toast.makeText(context, "SMS Sent Successfully!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } catch (Exception e) {
                    Toast.makeText(context, "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

            dialog.show();
        }

        private String formatBulletedList(String text) {
            if (text == null || text.isEmpty()) return "None";
            StringBuilder sb = new StringBuilder();
            for (String s : text.split("\n")) {
                if (!s.trim().isEmpty()) sb.append("• ").append(s.trim()).append("\n");
            }
            return sb.toString().trim();
        }

        private String formatNumberedList(String text) {
            if (text == null || text.isEmpty()) return "None";
            StringBuilder sb = new StringBuilder();
            String[] lines = text.split("\n");
            for (int i = 0; i < lines.length; i++) {
                if (!lines[i].trim().isEmpty()) sb.append(i + 1).append(". ").append(lines[i].trim()).append("\n");
            }
            return sb.toString().trim();
        }
    }
}
