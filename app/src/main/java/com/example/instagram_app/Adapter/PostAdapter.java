package com.example.instagram_app.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.instagram_app.CommentsActivity;
import com.example.instagram_app.Controller.Server;
import com.example.instagram_app.FollowersActivity;
import com.example.instagram_app.Fragment.PostDetailFragment;
import com.example.instagram_app.Fragment.ProfileFragment;
import com.example.instagram_app.Model.Comment;
import com.example.instagram_app.Model.Notification;
import com.example.instagram_app.Model.Post;
import com.example.instagram_app.Model.User;
import com.example.instagram_app.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    public Context mContext;
    public List<Post> mPost;


    public PostAdapter(Context mContext, List<Post> mPost) {
        this.mContext = mContext;
        this.mPost = mPost;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_item,viewGroup,false);
        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {


        final Post post = mPost.get(i);

        Glide.with(mContext).load(post.getPostimage())
                .apply(new RequestOptions().placeholder(R.drawable.placeholder))
                .into(viewHolder.post_image);

        if("".equals(post.getDescription())){
            viewHolder.description.setVisibility(View.GONE);
        } else {
            viewHolder.description.setVisibility((View.VISIBLE));
            viewHolder.description.setText(post.getDescription());
        }

        publisherInfo(viewHolder.image_profile,viewHolder.username,viewHolder.publisher,post.getPublisher());
        isLiked(post.getPostid(),viewHolder.like);
        nrLikes(viewHolder.likes,post.getPostid());
        getComments(post.getPostid(), viewHolder.comments);
        isSaved(post.getPostid(), viewHolder.save);

        viewHolder.image_profile.setOnClickListener(v -> {
            SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
            editor.putString("profileid",post.getPublisher());
            editor.apply();

            ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new ProfileFragment()).commit();
        });

        viewHolder.username.setOnClickListener(v -> {
            SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
            editor.putString("profileid",post.getPublisher());
            editor.apply();

            ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new ProfileFragment()).commit();
        });

        viewHolder.publisher.setOnClickListener(v -> {
            SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
            editor.putString("profileid",post.getPublisher());
            editor.apply();

            ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new ProfileFragment()).commit();
        });

        viewHolder.post_image.setOnClickListener(v -> {
            SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
            editor.putString("postid",post.getPostid());
            editor.apply();

            ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new PostDetailFragment()).commit();
        });

        viewHolder.save.setOnClickListener(v -> {
            if(viewHolder.save.getTag().equals("save")){
                Server.Database.savePost(Server.Auth.getUid(),post.getPostid(),
                        aVoid -> {Toast.makeText(mContext, "Post saved", Toast.LENGTH_SHORT).show();},e -> {});

            }else {

                Server.Database.removeSavedPost(Server.Auth.getUid(),post.getPostid(),aVoid -> {Toast.makeText(mContext, "Post removed", Toast.LENGTH_SHORT).show();},e -> {});
            }
        });

        viewHolder.like.setOnClickListener(view -> {
            if(viewHolder.like.getTag().equals("like")){

                Server.Database.addLike(Server.Auth.getUid(),post.getPostid(),
                        aVoid -> {Toast.makeText(mContext, "Post liked", Toast.LENGTH_SHORT).show();},e -> {});

                addNotifications(post.getPublisher(),post.getPostid());
            }else {
                Server.Database.removeLike(Server.Auth.getUid(),post.getPostid(),
                        aVoid -> {Toast.makeText(mContext, "Like removed", Toast.LENGTH_SHORT).show();},e -> {});

            }
        });

        viewHolder.comment.setOnClickListener(v -> {
            Intent intent = new Intent (mContext, CommentsActivity.class);
            intent.putExtra("postid", post.getPostid());
            intent.putExtra("publisherid", post.getPublisher());
            mContext.startActivity(intent);
        });

        viewHolder.comments.setOnClickListener(v -> {
            Intent intent = new Intent (mContext, CommentsActivity.class);
            intent.putExtra("postid", post.getPostid());
            intent.putExtra("publisherid", post.getPublisher());
            mContext.startActivity(intent);
        });

        viewHolder.likes.setOnClickListener(v -> {

            Intent intent=new Intent(mContext, FollowersActivity.class);
            intent.putExtra("id",post.getPostid());
            intent.putExtra("title","likes");
            mContext.startActivity(intent);
        });

        viewHolder.more.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(mContext, view);
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                switch (menuItem.getItemId()){
                    case R.id.edit:
                        editPost(post.getPostid());
                        return true;
                    case R.id.delete:

                        ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                new ProfileFragment()).commit();

                        Server.Database.removePost(post.getPostid(),
                                aVoid -> {
                                    deleteNotifications(post.getPostid(), Server.Auth.getUid());
                                    mPost.remove(post);
                                    notifyDataSetChanged();
                            },e -> {});

                        return true;
                    case R.id.report:
                        Toast.makeText(mContext, "Reported clicked!", Toast.LENGTH_SHORT).show();
                        return true;
                    default:
                        return false;
                }
            });
            popupMenu.inflate(R.menu.post_menu);
            if (!post.getPublisher().equals(Server.Auth.getUid())){
                popupMenu.getMenu().findItem(R.id.edit).setVisible(false);
                popupMenu.getMenu().findItem(R.id.delete).setVisible(false);
            }
            popupMenu.show();
        });

    }

    @Override
    public int getItemCount() {
        return mPost.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView image_profile,post_image,like,comment,save,more;
        public TextView username, likes, publisher, description, comments;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image_profile = itemView.findViewById(R.id.image_profile);
            post_image = itemView.findViewById(R.id.post_image);
            like = itemView.findViewById(R.id.like);
            comment = itemView.findViewById(R.id.comment);
            save = itemView.findViewById(R.id.save);
            username = itemView.findViewById(R.id.username);
            likes = itemView.findViewById(R.id.likes);
            publisher = itemView.findViewById(R.id.publisher);
            description = itemView.findViewById(R.id.description);
            comments = itemView.findViewById(R.id.comments);
            more = itemView.findViewById(R.id.more);

        }
    }

    private void getComments(String postid, final TextView txtcomments){

        Server.Database.getAllComments(postid,
                comments -> txtcomments.setText("View All " + comments.size() + " Comments"), e -> {});

    }

    private void isLiked(String postid, final ImageView imageView){


        Server.Database.isLiked(postid, Server.Auth.getUid(), isLiked -> {
            if (isLiked){
                imageView.setImageResource(R.drawable.ic_liked);
                imageView.setTag("liked");
            }else {
                imageView.setImageResource(R.drawable.ic_like);
                imageView.setTag("like");
            }
        }, e -> {

        });

    }

    private void addNotifications(String userid,String postid){

        Notification notification = new Notification(Server.Auth.getUid(),"liked your post",postid,true);
        Server.Database.addNotification(userid,notification,aVoid -> {},e -> {});

    }
    private void deleteNotifications(final String postid, String userid){
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);
//        reference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
//                    Notification notification = snapshot.getValue(Notification.class);
//                    if (notification.getPostid().equals(postid)){
//                        snapshot.getRef().removeValue()
//                                .addOnCompleteListener(task -> Toast.makeText(mContext, "Deleted!", Toast.LENGTH_SHORT).show());
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

        Server.Database.deleteNotifications(postid, userid,
                aVoid -> Toast.makeText(mContext, "Deleted!", Toast.LENGTH_SHORT).show(), e -> {});
    }
    private void nrLikes(final TextView likes, String postId){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes").child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                likes.setText(dataSnapshot.getChildrenCount()+" likes");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void publisherInfo(final ImageView image_profile, final TextView username, final TextView publisher, String userId){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Glide.with(mContext).load(user.getImageurl()).into(image_profile);
                username.setText(user.getUsername());
                publisher.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void isSaved(final String postid, final ImageView imageView){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Saves")
                .child(Server.Auth.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(postid).exists()){
                    imageView.setImageResource(R.drawable.ic_save_black);
                    imageView.setTag("saved");
                }else {
                    imageView.setImageResource(R.drawable.ic_savee_black);
                    imageView.setTag("save");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void editPost(final String postid){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("Edit Post");

        final EditText editText = new EditText(mContext);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        editText.setLayoutParams(lp);
        alertDialog.setView(editText);

        getText(postid, editText);

        alertDialog.setPositiveButton("Edit",
                (dialog, which) -> {
                    HashMap<String,Object> hashMap = new HashMap<>();
                    hashMap.put("description",editText.getText().toString());

                    FirebaseDatabase.getInstance().getReference("Posts")
                            .child(postid).updateChildren(hashMap);
                });
        alertDialog.setNegativeButton("Cancel",
                (dialogInterface, which) -> dialogInterface.dismiss());
        alertDialog.show();
    }

    private void getText(String postid, final EditText editText){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts")
                .child(postid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                editText.setText(dataSnapshot.getValue(Post.class).getDescription());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
