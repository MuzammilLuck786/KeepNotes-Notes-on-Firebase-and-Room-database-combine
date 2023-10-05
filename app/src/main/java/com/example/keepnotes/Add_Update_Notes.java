package com.example.keepnotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Add_Update_Notes extends AppCompatActivity {
    EditText idEdtNoteName, idEdtNoteDesc;
    Button idBtn;
    DatabaseHelper databaseHelper;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    Note note;

    NetworkUtils networkUtils;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_update_notes);
        idEdtNoteName = findViewById(R.id.idEdtNoteName);
        idEdtNoteDesc = findViewById(R.id.idEdtNoteDesc);
        idBtn = findViewById(R.id.idBtn);
        databaseHelper = DatabaseHelper.getDB(this);

        note = (Note) getIntent().getSerializableExtra("NoteData");
        if (note != null) {
            idEdtNoteName.setText(note.getTitle());
            idEdtNoteDesc.setText(note.getDescription());
            idBtn.setText("Update Note");
        }


        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Notes");

        networkUtils = new NetworkUtils();


        idBtn.setOnClickListener(v -> {
            if (NetworkUtils.isNetworkAvailable(Add_Update_Notes.this)) {
                String title = idEdtNoteName.getText().toString();
                String description = idEdtNoteDesc.getText().toString();

                if (idBtn.getText().toString().equals("Add Note")){
                    if (title.isEmpty()){
                        idEdtNoteName.setText("Enter Some Title");
                        return;
                    }
                    saveNoteOnline(title, description);
                }else {
                    if (title.isEmpty()){
                        idEdtNoteName.setText("Enter Some Title");
                        return;
                    }
                    updateNoteInFirebase();
                }

            } else {
                saveNoteOffline();
                }
            startActivity(new Intent(getApplicationContext(), NotesLists.class));
            finish();

        });

    }


    public void updateNoteInFirebase() {
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("description", idEdtNoteDesc.getText().toString());
        hashMap.put("id",note.getId());
        hashMap.put("onlineReferenceID",note.getOnlineReferenceID());
        hashMap.put("title",idEdtNoteName.getText().toString());

        databaseReference.child(note.getKey()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(Add_Update_Notes.this, "Data Updated", Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Add_Update_Notes.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void saveNoteOffline() {
        String title = idEdtNoteName.getText().toString();
        String description = idEdtNoteDesc.getText().toString();
        if (note!=null) {
            if (title.isEmpty()) {
                idEdtNoteName.setText("Enter Some Title");
            }
            databaseHelper.noteDao().addNote(new Note(note.getId(),title, description));
            Toast.makeText(this, "data stored", Toast.LENGTH_SHORT).show();
        } else {
            databaseHelper.noteDao().addNote(new Note(title,description));
            Toast.makeText(this, "data updated", Toast.LENGTH_SHORT).show();

        }
        }


    public void saveNoteOnline(String title, String description) {
        String postId = databaseReference.push().getKey();
        note = new Note();
        note.setTitle(title);
        note.setDescription(description);
        note.setOnlineReferenceID(postId);
        Log.d("Tag", "saveNoteOnline: Key :  " + postId);
        databaseReference.child(postId).setValue(note).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getApplicationContext(), "Added Data Successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(Add_Update_Notes.this, "Failed to save data ", Toast.LENGTH_SHORT).show();
            }
        });

    }

}