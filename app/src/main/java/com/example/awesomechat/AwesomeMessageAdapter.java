package com.example.awesomechat;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import java.util.List;

public class AwesomeMessageAdapter extends ArrayAdapter<AwesomeMessage> {
    public AwesomeMessageAdapter(@NonNull Context context, int resource, List<AwesomeMessage> messages) {
        super(context, resource, messages);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.message_item, parent, false);
        }

        ImageView imageImageView = convertView.findViewById(R.id.imageImageView);
        TextView messageTextView = convertView.findViewById(R.id.messageTextView);
        TextView nameTextView = convertView.findViewById(R.id.nameTextView);

        AwesomeMessage message = getItem(position);

        if (message.getImageUrl() == null) {
            messageTextView.setVisibility(View.VISIBLE);
            messageTextView.setText(message.getText());
            imageImageView.setVisibility(View.GONE);
        } else {
            imageImageView.setVisibility(View.VISIBLE);
            Glide.with(imageImageView.getContext()).load(message.getImageUrl()).into(imageImageView);
            messageTextView.setVisibility(View.GONE);
        }

        nameTextView.setText(message.getName());
        return convertView;
    }
}
