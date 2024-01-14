package com.example.bookit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.bookit.model.User;

import java.util.List;


public class BlockUsersAdapter extends ArrayAdapter<User> {

    public BlockUsersAdapter(Context context, List<User> users) {
        super(context, 0, users);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        User user = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_user_blocking, parent, false);
        }

        TextView nameTextView = convertView.findViewById(R.id.commentTextView);
        TextView lastnameTextView = convertView.findViewById(R.id.gradeTextView);
        TextView usernameTextView = convertView.findViewById(R.id.authorTextView);
        TextView blockUserTextView = convertView.findViewById(R.id.isBlocked);



        if (user != null) {
            nameTextView.setText("Name: " + user.getName());
            lastnameTextView.setText("Lastname: " + user.getLastName());
            usernameTextView.setText("Email: " + user.getEmail());
            blockUserTextView.setText("User blocked: " + user.isBlocked());
        }

        return convertView;
    }
}
