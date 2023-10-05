package com.example.keepnotes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.List;

public class OfflineNotes extends AppCompatActivity {

    RecyclerView offlineRv;
    DatabaseHelper databaseHelper;
    NotesAdapter notesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_notes);

        offlineRv=findViewById(R.id.offlineRv);
        databaseHelper=DatabaseHelper.getDB(this);
        offlineRv.setHasFixedSize(true);
        offlineRv.setLayoutManager(new LinearLayoutManager(this));
        List<Note> notes = databaseHelper.noteDao().getAllNotes();
        notesAdapter = new NotesAdapter(OfflineNotes.this, notes);
        offlineRv.setAdapter(notesAdapter);

    }
}