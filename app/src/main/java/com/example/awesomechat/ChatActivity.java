package com.example.awesomechat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private ListView messageListView;
    private AwesomeMessageAdapter adapter;
    private ProgressBar progressBar;
    private ImageButton sendImageButton;
    private EditText messageEditText;
    private Button sendButton;
    private String userName;
    private String recipientUserId;
    private String recipientUserName;

    private static final int RC_IMAGE_PICKER = 123;

    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private DatabaseReference messagesDatabaseReference;
    private ChildEventListener messagesChildEventListener;
    private DatabaseReference usersDatabaseReference;
    private ChildEventListener usersChildEventListener;
    private StorageReference chatImageStorageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        auth = FirebaseAuth.getInstance();

        Intent intent = getIntent();

        if (intent != null) {
            userName = intent.getStringExtra("userName");
            recipientUserId = intent.getStringExtra("recipientUserId");
            recipientUserName = intent.getStringExtra("recipientUserName");
        }

        setTitle("Chat with " + recipientUserName);

        database = FirebaseDatabase.getInstance("https://awesome-chat-72589-default-rtdb.europe-west1.firebasedatabase.app/");
        storage = FirebaseStorage.getInstance();
        messagesDatabaseReference = database.getReference().child("messages");
        usersDatabaseReference = database.getReference().child("users");
        chatImageStorageReference = storage.getReference().child("chat_images");

        messageListView = findViewById(R.id.messageListView);
        List<AwesomeMessage> awesomeMessages = new ArrayList<>();
        adapter = new AwesomeMessageAdapter(this, R.layout.message_item, awesomeMessages);
        messageListView.setAdapter(adapter);
        progressBar = findViewById(R.id.progressBar);
        sendImageButton = findViewById(R.id.sendImageButton);
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);

        progressBar.setVisibility(ProgressBar.INVISIBLE);

        messageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.toString().trim().length() > 0) {
                    sendButton.setEnabled(true);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        messageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(500)});

        sendImageButton.setOnClickListener(view -> {
            Intent sendImageButtonIntent = new Intent(Intent.ACTION_GET_CONTENT);
            sendImageButtonIntent.setType("image/*");
            sendImageButtonIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            startActivityForResult(Intent.createChooser(sendImageButtonIntent, "Choose an image"), RC_IMAGE_PICKER);
        });

        sendButton.setOnClickListener(view -> {
            AwesomeMessage message = new AwesomeMessage();
            message.setText(messageEditText.getText().toString());
            message.setName(userName);
            message.setSender(auth.getCurrentUser().getUid());
            message.setRecipient(recipientUserId);
            message.setImageUrl(null);

            messagesDatabaseReference.push().setValue(message);

            messageEditText.setText("");
        });

        messagesChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                AwesomeMessage message = snapshot.getValue(AwesomeMessage.class);

                if (message.getSender().equals(auth.getCurrentUser().getUid()) && message.getRecipient().equals(recipientUserId)) {
                    message.setMine(true);
                    adapter.add(message);
                } else if (message.getRecipient().equals(auth.getCurrentUser().getUid()) && message.getSender().equals(recipientUserId)) {
                    message.setMine(false);
                    adapter.add(message);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        messagesDatabaseReference.addChildEventListener(messagesChildEventListener);

        usersChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                User user = snapshot.getValue(User.class);

                if (user.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    userName = user.getName();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        usersDatabaseReference.addChildEventListener(usersChildEventListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.sign_out:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ChatActivity.this, SignInActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_IMAGE_PICKER && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            StorageReference imageReference = chatImageStorageReference.child(selectedImageUri.getLastPathSegment());
            UploadTask uploadTask = imageReference.putFile(selectedImageUri);

            Task<Uri> urlTask = uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return imageReference.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    AwesomeMessage message = new AwesomeMessage();
                    message.setImageUrl(downloadUri.toString());
                    message.setName(userName);
                    message.setSender(auth.getCurrentUser().getUid());
                    message.setRecipient(recipientUserId);
                    messagesDatabaseReference.push().setValue(message);
                } else {
                    // Handle failures
                    // ...
                }
            });
        }
    }
}