package com.example.awesomechat;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
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
    private List<AwesomeMessage> messages;
    private Activity activity;

    public AwesomeMessageAdapter(@NonNull Activity context, int resource, List<AwesomeMessage> messages) {
        super(context, resource, messages);
        this.messages = messages;
        this.activity = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        AwesomeMessage awesomeMessage = getItem(position);
        int layoutResource = 0;
        int viewType = getItemViewType(position);

        if (viewType == 0) {
            layoutResource = R.layout.my_message_item;
        } else {
            layoutResource = R.layout.your_message_item;
        }

        if (convertView != null) {
            viewHolder = (ViewHolder) convertView.getTag();
        }else {
            convertView = layoutInflater.inflate(layoutResource, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }

        boolean isText = awesomeMessage.getImageUrl() == null;

        if (isText) {
            viewHolder.bubbleTextView.setVisibility(View.VISIBLE);
            viewHolder.imageImageView.setVisibility(View.GONE);
            viewHolder.bubbleTextView.setText(awesomeMessage.getText());
        } else {
            viewHolder.imageImageView.setVisibility(View.VISIBLE);
            viewHolder.bubbleTextView.setVisibility(View.GONE);
            Glide.with(viewHolder.imageImageView.getContext()).load(awesomeMessage.getImageUrl()).into(viewHolder.imageImageView);
        }
        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        int flag;
        AwesomeMessage awesomeMessage = messages.get(position);

        if (awesomeMessage.isMine()) {
            flag = 0;
        } else {
            flag = 1;
        }
        return flag;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    private class ViewHolder {
        private ImageView imageImageView;
        private TextView bubbleTextView;

        public ViewHolder(View view) {
            imageImageView = view.findViewById(R.id.imageImageView);
            bubbleTextView = view.findViewById(R.id.bubbleTextView);
        }
    }
}
