package com.example.bookit.utils.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookit.R;
import com.example.bookit.model.User;
import com.example.bookit.retrofit.api.UserApi;
import com.example.bookit.utils.asyncTasks.OwnerReportGuestTask;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class OwnerReportGuestAdapter extends RecyclerView.Adapter<OwnerReportGuestAdapter.ViewHolder> {

    private List<User> userList;
    private UserApi userApi;

    public OwnerReportGuestAdapter(List<User> userList, UserApi userApi) {
        this.userList = userList;
        this.userApi = userApi;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_card_info, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userList.get(position);

        if (user.isReported()){
            holder.submitReportButton.setEnabled(false);
            holder.submitReportButton.setText("Already reported");
        }

        holder.nameTextView.setText(String.format("Name: %s", user.getName()));
        holder.lastNameTextView.setText(String.format("Last Name: %s", user.getLastName()));
        holder.emailTextView.setText(String.format("Email: %s", user.getEmail()));
        holder.phoneTextView.setText(String.format("Phone: %s", user.getPhone()));
        holder.addressTextView.setText(String.format("Address: %s", user.getAddress()));

        holder.submitReportButton.setOnClickListener(view -> {
            if (canSubmit(holder.reasonTF.getText().toString())){
              new OwnerReportGuestTask(userApi, holder, user).execute();
            }
            else {
                Snackbar.make(holder.itemView, "You can't report a user without a reason", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    public boolean canSubmit(String text){
        return text.trim().length() != 0;
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, lastNameTextView, emailTextView, phoneTextView, addressTextView, reasonTF;
        public Button submitReportButton;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.userOverview);
            lastNameTextView = itemView.findViewById(R.id.lastNameOverview);
            emailTextView = itemView.findViewById(R.id.emailOverview);
            phoneTextView = itemView.findViewById(R.id.phoneOverview);
            addressTextView = itemView.findViewById(R.id.addressOverview);
            submitReportButton = itemView.findViewById(R.id.reportUserBtn);
            reasonTF = itemView.findViewById(R.id.reasonOverview);
        }
    }
}
