package com.example.bookit.utils;

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
import com.example.bookit.utils.asyncTasks.GuestReportOwnerTask;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GuestReportOwnerAdapter extends RecyclerView.Adapter<GuestReportOwnerAdapter.ViewHolder> {
    private List<User> userList;
    private UserApi userApi;
    public GuestReportOwnerAdapter(List<User> reportableUsers, UserApi userApi) {
        this.userList = reportableUsers;
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
               new GuestReportOwnerTask(holder, userApi, user).execute();
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
        public TextView nameTextView, lastNameTextView, emailTextView, phoneTextView, addressTextView, reasonTF;
        public Button submitReportButton;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTF_report_card_info);
            lastNameTextView = itemView.findViewById(R.id.lastNameTF_report_card_info);
            emailTextView = itemView.findViewById(R.id.emailTF_report_card_info);
            phoneTextView = itemView.findViewById(R.id.phoneTF_report_card_info);
            addressTextView = itemView.findViewById(R.id.addressTF_report_card_info);
            submitReportButton = itemView.findViewById(R.id.submitReportBtn_report_card_info);
            reasonTF = itemView.findViewById(R.id.textarea_report_card_info);
        }
    }
}
