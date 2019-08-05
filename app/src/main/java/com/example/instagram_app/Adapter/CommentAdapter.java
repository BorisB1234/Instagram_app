package com.example.instagram_app.Adapter;

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

import com.bumptech.glide.Glide;
import com.example.instagram_app.Controller.Server;
import com.example.instagram_app.MainActivity;
import com.example.instagram_app.Model.Comment;
import com.example.instagram_app.Model.User;
import com.example.instagram_app.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    final private Context mContext;
    final private List<Comment> mComment;
    final private String postid;


    public CommentAdapter(final Context mContext,final String postid) {

        this.mContext = mContext;
        this.mComment = new ArrayList<>();
        this.postid = postid;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.comment_item, viewGroup, false);
        return new CommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {

        final Comment comment = mComment.get(i);
        Server.Database.getUser(comment.getPublisher(), new Consumer<User>() {
            @Override
            public void accept(User user) {
                viewHolder.setData(user.getUsername(),user.getImageurl(),comment);
            }
        }, new Consumer<Optional<Exception>>() {
            @Override
            public void accept(Optional<Exception> e) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mComment.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView image_profile;
        private TextView username, comment;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            image_profile = itemView.findViewById(R.id.image_profile);
            username = itemView.findViewById(R.id.username);
            comment = itemView.findViewById(R.id.comment);
        }



        public void setData(String username,String imageURL,final Comment comment){
            this.username.setText(username);
            Glide.with(mContext).load(imageURL).into(image_profile);
            this.comment.setText(comment.getComment());


            this.comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, MainActivity.class);
                    intent.putExtra("publisherid", comment.getPublisher());
                    mContext.startActivity(intent);
                }
            });

            this.image_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, MainActivity.class);
                    intent.putExtra("publisherid", comment.getPublisher());
                    mContext.startActivity(intent);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(comment.getPublisher().equals(Server.Auth.getUid())){
                        AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                        alertDialog.setTitle("Do you want to delete?");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "No",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int which) {
                                        dialogInterface.dismiss();
                                    }
                                });
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int which) {
                                        FirebaseDatabase.getInstance()
                                                .getReference("Comments")
                                                .child(postid).child(comment.getCommentid())
                                                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Toast.makeText(mContext, "Deleted!", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                        dialogInterface.dismiss();
                                    }
                                });
                        alertDialog.show();
                    }
                    return true;
                }
            });

        }
    }

    public void setmComment(List<Comment> mComment) {
        this.mComment.clear();
        this.mComment.addAll(mComment);
        this.notifyDataSetChanged();
    }
}
