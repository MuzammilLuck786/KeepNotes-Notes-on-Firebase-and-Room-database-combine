package com.example.keepnotes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotesLists extends AppCompatActivity {
    DatabaseHelper databaseHelper;
    RecyclerView rvNotes;

    FloatingActionButton addNotes;
    NotesAdapter notesAdapter;
    Button offlineNotes;

    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    List<Note> noteArrayList;

    NetworkUtils networkUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_lists);

        addNotes = findViewById(R.id.addNotes);
        rvNotes = findViewById(R.id.rvNotes);
        offlineNotes=findViewById(R.id.offlineNotes);
        networkUtils = new NetworkUtils();



        addNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Add_Update_Notes.class));
            }
        });

        offlineNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),OfflineNotes.class));
            }
        });



    }

    private void fetchNoteOffline() {
        offlineNotes.setVisibility(View.GONE);
        databaseHelper = DatabaseHelper.getDB(this);
        rvNotes.setHasFixedSize(true);
        rvNotes.setLayoutManager(new LinearLayoutManager(NotesLists.this));
        List<Note> notes = databaseHelper.noteDao().getAllNotes();
        notesAdapter = new NotesAdapter(NotesLists.this, notes);
        rvNotes.setAdapter(notesAdapter);

    }

    public void fetchNoteOnline() {
        offlineNotes.setVisibility(View.VISIBLE);

        databaseReference = FirebaseDatabase.getInstance().getReference("Notes");

        rvNotes.setHasFixedSize(true);
        rvNotes.setLayoutManager(new LinearLayoutManager(NotesLists.this));

        noteArrayList = new ArrayList<>();
        notesAdapter = new NotesAdapter(NotesLists.this, noteArrayList);
        rvNotes.setAdapter(notesAdapter);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot noteSnapshot : dataSnapshot.getChildren()) {
                    Note note = noteSnapshot.getValue(Note.class);
                    note.setKey(noteSnapshot.getKey());
                    noteArrayList.add(note);
                }
                notesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(NotesLists.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                // Handle errors here
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        if (NetworkUtils.isNetworkAvailable(NotesLists.this)) {
            Toast.makeText(this, "Internet is available", Toast.LENGTH_LONG).show();
            fetchNoteOnline();
        } else {
            Toast.makeText(this, "You are in offline mood", Toast.LENGTH_LONG).show();
            fetchNoteOffline();
        }
    }
}