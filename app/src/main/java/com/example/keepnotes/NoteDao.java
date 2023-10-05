package com.example.keepnotes;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface NoteDao {
    @Query("select * from Notes")
    List<Note> getAllNotes();


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addNote(Note note);

//    @Update
//    void updateNote(Note note);
    @Query("Delete from Notes where id = :id")
    void deleteNote(int id);

}
