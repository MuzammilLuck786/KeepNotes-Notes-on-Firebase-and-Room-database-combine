package com.example.keepnotes;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Objects;

public class NotesAdapter  extends RecyclerView.Adapter<NotesAdapter.MyViewHolder>  {

    Context context;
    List<Note> notes;
    DatabaseHelper databaseHelper;
    private DatabaseReference mDatabase;
    Note note;

    public NotesAdapter(Context context, List<Note> notes) {
        this.context = context;
        this.notes = notes;
        databaseHelper=DatabaseHelper.getDB(context);
        note=new Note();
    }

    @NonNull
    @Override
    public NotesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.note_rv_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesAdapter.MyViewHolder holder, int position) {
        holder.notes.setText(notes.get(position).getTitle());

        holder.deleteNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (NetworkUtils.isNetworkAvailable(context)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context)
                            .setTitle("Delete Note")
                            .setMessage("Are you sure you want to Delete?")
                            .setIcon(R.drawable.newdelete)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Notes");
                                    ref.child(notes.get(position).getKey())
                                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
                                                        context.startActivity(new Intent(context,NotesLists.class));
                                                        ((Activity)context).finish();
                                                    }
                                                }
                                            });


                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                    builder.show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context)
                            .setTitle("Delete Note")
                            .setMessage("Are you sure you want to Delete?")
                            .setIcon(R.drawable.newdelete)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    databaseHelper.noteDao().deleteNote(notes.get(position).getId());
                                    notes.remove(position);
                                    notifyDataSetChanged();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                    builder.show();

                }



            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, Add_Update_Notes.class);
                i.putExtra("NoteData", notes.get(position));
                context.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView notes;
        ImageView deleteNote;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            notes=itemView.findViewById(R.id.note);
            deleteNote=itemView.findViewById(R.id.deleteNote);
        }
    }

}
