package com.example.instagram_app.Controller;

import android.content.ContentResolver;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.MyApplication;
import com.example.instagram_app.Model.Comment;
import com.example.instagram_app.Model.Notification;
import com.example.instagram_app.Model.Post;
import com.example.instagram_app.Model.User;
import com.example.instagram_app.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class Server {
    private Server(){}

    public static class Database{
        private static DatabaseReference databaseRef =
                FirebaseDatabase.getInstance().getReference();
        private static DatabaseReference SavesRef = databaseRef.child("Saves");
        private static DatabaseReference UserRef = databaseRef.child("Users");
        private static DatabaseReference CommentsRef = databaseRef.child("Comments");
        private static DatabaseReference NotificationsRef = databaseRef.child("Notifications");
        private static DatabaseReference PostsRef = databaseRef.child("Posts");
        private static DatabaseReference LikesRef = databaseRef.child("Likes");
        private static DatabaseReference FollowRef = databaseRef.child("Follow");

        public static void addUser(final User user,final Consumer<Void> onComplete,
                                   final Consumer<Optional<Exception>> onFailed){
            UserRef.child(user.getId()).setValue(user).addOnCompleteListener(task -> {
                if(task.isSuccessful())
                {
                    onComplete.accept(null);
                }
                else {
                    onFailed.accept(Optional.empty());
                }
            }).addOnFailureListener(e -> onFailed.accept(Optional.of(e)));
        }

        public static void getUser(String uid,final Consumer<User> onComplete,
                                   final Consumer<Optional<Exception>> onFailed){
            UserRef.child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user!=null) onComplete.accept(user);
                    else onFailed.accept(Optional.empty());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Exception databaseException = databaseError.toException();
                    onFailed.accept(Optional.of(databaseException));
                }
            });
        }
        public static void getCurrentUser(final Consumer<User> onComplete,
                                          final Consumer<Optional<Exception>> onFailed){
            if (!Auth.isLogin()) {
                onFailed.accept(Optional.of(new Exception("User not login")));
            }
            else
                getUser(Auth.getUid(),onComplete,onFailed);
        }

        public static void addComment(final String postid,final String stcomment
                                    ,final Consumer<Comment> onComplete,
                                      final Consumer<Optional<Exception>> onFailed){
            DatabaseReference ref = CommentsRef.child(postid);
            String cid = ref.push().getKey();
            final Comment comment=new Comment(stcomment,
                    Server.Auth.getUid(),cid);
            ref.child(cid).setValue(comment)
                    .addOnSuccessListener(aVoid -> onComplete.accept(comment))
                    .addOnFailureListener(e -> onFailed.accept(Optional.of(e)));
        }

        public static void addNotification(final String publisherId,
                                           final Notification notification,
                                           final Consumer<Void> onComplete,
                                           final Consumer<Optional<Exception>> onFailed){
            NotificationsRef.child(publisherId).push().setValue(notification)
                    .addOnCompleteListener(task -> onComplete.accept(null))
                    .addOnFailureListener(e -> onFailed.accept(Optional.of(e)));
        }

        public static void deleteNotifications(final String postid, String userid,
                                               final Consumer<Void> onComplete,
                                               final Consumer<Optional<Exception>> onFailed){
            NotificationsRef.child(userid).orderByChild("postid").equalTo(postid)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        snapshot.getRef().removeValue();
                    }
                    onComplete.accept(null);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    onFailed.accept(Optional.ofNullable(databaseError.toException()));
                }
            });

        }

        public static void getAllComments(final String postid,
                                          final Consumer<List<Comment>> onComplete,
                                          final Consumer<Optional<Exception>> onFailed){
            CommentsRef.child(postid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<Comment> comments=new ArrayList<>();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        Comment comment = snapshot.getValue(Comment.class);
                        comments.add(comment);
                    }
                    onComplete.accept(comments);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    onFailed.accept(Optional.ofNullable(
                            databaseError.toException()));
                }
            });
        }

        public static void getAllCommentsOfAllPosts(final Consumer<List<List<Comment>>> onComplete,
                                          final Consumer<Optional<Exception>> onFailed){
            CommentsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<List<Comment>> comments=new ArrayList<>();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        List<Comment> commentList = new ArrayList<>();

                        for(DataSnapshot snapshot2 : snapshot.getChildren()) {
                            Comment comment = snapshot2.getValue(Comment.class);
                            commentList.add(comment);
                        }
                        comments.add(commentList);
                    }
                    onComplete.accept(comments);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    onFailed.accept(Optional.ofNullable(
                            databaseError.toException()));
                }
            });
        }

        public static void deleteComment(final String postid, final String commentID,
                                         final Consumer<Void> onComplete,
                                         final Consumer<Optional<Exception>> onFailed) {
            CommentsRef.child(postid).child(commentID)
                    .removeValue().addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            onComplete.accept(null);
                        }
                    }).addOnFailureListener(e->onFailed.accept(Optional.of(e)));
        }

        public static void getPost(String pid,final Consumer<Post> onComplete,
                                   final Consumer<Optional<Exception>> onFailed){
            PostsRef.child(pid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Post post = dataSnapshot.getValue(Post.class);
                    if (post!=null) onComplete.accept(post);
                    else onFailed.accept(Optional.empty());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Exception databaseException = databaseError.toException();
                    onFailed.accept(Optional.of(databaseException));
                }
            });
        }

        public static void savePost(String uid, String postid,final Consumer<Void> onComplete,
                                    final Consumer<Optional<Exception>> onFailed) {
            SavesRef.child(uid).child(postid).setValue(true).addOnCompleteListener(task -> {
                if (task.isSuccessful())onComplete.accept(null);
                else onFailed.accept(Optional.empty());
            }).addOnFailureListener(e -> onFailed.accept(Optional.of(e)));
        }

        public static void removeSavedPost(String uid, String postid,final Consumer<Void> onComplete,
                                    final Consumer<Optional<Exception>> onFailed) {
            SavesRef.child(uid).child(postid).removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful())onComplete.accept(null);
                else onFailed.accept(Optional.empty());
            }).addOnFailureListener(e -> onFailed.accept(Optional.of(e)));

        }

        public static void addLike(String uid, String postid,final Consumer<Void> onComplete,
                                    final Consumer<Optional<Exception>> onFailed) {
            LikesRef.child(postid).child(uid).setValue(true).addOnCompleteListener(task -> {
                if (task.isSuccessful())onComplete.accept(null);
                else onFailed.accept(Optional.empty());
            }).addOnFailureListener(e -> onFailed.accept(Optional.of(e)));
        }

        public static void removeLike(String uid, String postid,final Consumer<Void> onComplete,
                                   final Consumer<Optional<Exception>> onFailed) {
            LikesRef.child(postid).child(uid).removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful())onComplete.accept(null);
                else onFailed.accept(Optional.empty());
            }).addOnFailureListener(e -> onFailed.accept(Optional.of(e)));
        }

        public static void removePost(String postid, final Consumer<Void> onComplete,
                                      final Consumer<Optional<Exception>> onFailed) {

            PostsRef.child(postid).removeValue()
                    .addOnCompleteListener(task -> {
                if (task.isSuccessful())onComplete.accept(null);
                else onFailed.accept(Optional.empty());
            })
                    .addOnFailureListener(e -> onFailed.accept(Optional.of(e)));
        }

        public static void isLiked(String postid, String uid, final Consumer<Boolean> onComplete,
                                   final Consumer<Optional<Exception>> onFailed){
            LikesRef.child(postid).child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) onComplete.accept(true);
                    else onComplete.accept(false);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    onFailed.accept(Optional.of(databaseError.toException()));
                }
            });
        }


        public static void getLikes(String postId, final Consumer<List<String>> onComplete,
                                    final Consumer<Optional<Exception>> onFailed) {
            List<String>list=new ArrayList<>();
            LikesRef.child(postId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    list.clear();
                    for(DataSnapshot snapshot: dataSnapshot.getChildren())
                    {
                        list.add(snapshot.getKey());
                    }
                    onComplete.accept(list);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    onFailed.accept(Optional.ofNullable(
                            databaseError.toException()));
                }
            });

        }

        public static void editPost(String postid, HashMap<String, Object> hashMap) {

            PostsRef.child(postid).updateChildren(hashMap);
        }

        public static void publisherInfo(final ImageView image_profile, final TextView username, final TextView publisher, String userId, Context mContext) {

            UserRef.child(userId).addValueEventListener(new ValueEventListener() {
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


        public static void isSaved(String postid, ImageView imageView, String uid) {

            SavesRef.child(uid).addValueEventListener(new ValueEventListener() {
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

        public static void getTextDescription(String postid, final Consumer<String> onComplete,
                                              final Consumer<Optional<Exception>> onFailed) {

            PostsRef.child(postid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    onComplete.accept(dataSnapshot.getValue(Post.class).getDescription());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    onFailed.accept(Optional.of(databaseError.toException()));
                }
            });

        }

        public static void follow(String uid, String id, boolean b) {

            if(b) {
                FollowRef.child(uid).child("following").child(id).setValue(true);
                FollowRef.child(id).child("followers").child(uid).setValue(true);
            }else {
                FollowRef.child(uid).child("following").child(id).removeValue();
                FollowRef.child(id).child("followers").child(uid).removeValue();
            }
        }

        public static void isFollowing(String uid, String userid, final Consumer<Boolean> onComplete,
                                       final Consumer<Optional<Exception>> onFailed) {

            FollowRef.child(uid).child("following").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child(userid).exists())
                        onComplete.accept(true);
                    else onComplete.accept(false);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    onFailed.accept(Optional.of(databaseError.toException()));
                }
            });

        }


        public static void readNotification(String uid, final Consumer<List> onComplete,
                                            final Consumer<Optional<Exception>> onFailed) {
            List<Notification>notificationList=new ArrayList<>();
            NotificationsRef.child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    notificationList.clear();
                    for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
                        Notification notification = snapshot.getValue(Notification.class);
                        notificationList.add(notification);
                    }

                    onComplete.accept(notificationList);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                        onFailed.accept(Optional.of(databaseError.toException()));

                }
            });

        }

        public static void getFollow(final String uid, final Boolean followers,
                                          final Consumer<List<String>> onComplete,
                                          final Consumer<Optional<Exception>> onFailed){

            String child;
            if(followers)
                child="followers";
            else child="following";

            FollowRef.child(uid).child(child).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<String> follow=new ArrayList<>();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        follow.add(snapshot.getKey());
                    }
                    onComplete.accept(follow);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    onFailed.accept(Optional.ofNullable(
                            databaseError.toException()));
                }
            });
        }

        public static void getAllPosts(final String uid,
                                          final Consumer<List<Post>> onComplete,
                                          final Consumer<Optional<Exception>> onFailed){
            PostsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<Post> posts=new ArrayList<>();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        Post post = snapshot.getValue(Post.class);
                        if(post.getPublisher().equals(uid))
                            posts.add(post);
                    }
                    onComplete.accept(posts);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    onFailed.accept(Optional.ofNullable(
                            databaseError.toException()));
                }
            });
        }

        public static void getAllPostsFromAllUsers(
                                       final Consumer<List<Post>> onComplete,
                                       final Consumer<Optional<Exception>> onFailed){
            PostsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<Post> posts=new ArrayList<>();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        Post post = snapshot.getValue(Post.class);
                            posts.add(post);
                    }
                    onComplete.accept(posts);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    onFailed.accept(Optional.ofNullable(
                            databaseError.toException()));
                }
            });
        }

        public static void checkPostsNum(
                final Consumer<Integer> onComplete,
                final Consumer<Optional<Exception>> onFailed){
            PostsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<Post> posts=new ArrayList<>();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        Post post = snapshot.getValue(Post.class);
                        posts.add(post);
                    }
                    onComplete.accept(posts.size());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    onFailed.accept(Optional.ofNullable(
                            databaseError.toException()));
                }
            });
        }

        public static void getAllSaves(String uid, final Consumer<List<String>> onComplete,
                                       final Consumer<Optional<Exception>> onFailed) {

            SavesRef.child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<String> list_saves=new ArrayList<>();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            list_saves.add(snapshot.getKey());
                    }
                    onComplete.accept(list_saves);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    onFailed.accept(Optional.ofNullable(
                            databaseError.toException()));
                }
            });

        }

        public static void searchUsers(String s, final Consumer<List<User>> onComplete,
                                       final Consumer<Optional<Exception>> onFailed) {

            UserRef.orderByChild("username")
                    .startAt(s)
                    .endAt(s+"\uf8ff").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<User> users=new ArrayList<>();

                    for(DataSnapshot snapshot:dataSnapshot.getChildren())
                    {
                        users.add(snapshot.getValue(User.class));
                    }
                    onComplete.accept(users);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        public static void getAllUsers(final Consumer<List<User>> onComplete,
                                       final Consumer<Optional<Exception>> onFailed) {
            List<User> users=new ArrayList<>();

            UserRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot snapshot:dataSnapshot.getChildren())
                    {
                        users.add(snapshot.getValue(User.class));
                    }
                    onComplete.accept(users);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        public static void updateUser(final User user,final Consumer<Void> onComplete,
                                      final Consumer<Optional<Exception>> onFailed) {
            UserRef.child(user.getId()).updateChildren(user.toMap()).addOnCompleteListener(task -> {
                if (!task.isSuccessful()) onFailed.accept(Optional.ofNullable(task.getException()));
                else onComplete.accept(null);
            }).addOnFailureListener(e -> onFailed.accept(Optional.of(e)));

        }

        public static void publishPost(String myUrl, String description, String uid, String gpsLatitude, String gpsLongitude) {

            DatabaseReference reference = PostsRef;
            String postid = reference.push().getKey();
            Post post=new Post(postid,myUrl,description,uid,gpsLatitude,gpsLongitude);

            reference.child(postid).setValue(post);

        }

        public static boolean netIsConnect(){
            ConnectivityManager connectivityManager=
                    (ConnectivityManager) MyApplication.getContext()
                            .getSystemService(Context.CONNECTIVITY_SERVICE);
            return  connectivityManager.getActiveNetworkInfo()!=null;
        }
    }

    public static class Auth{
        private static FirebaseAuth auth=FirebaseAuth.getInstance();

        public static void SignIn(final String email,final String password
                ,final Consumer<Void> onComplete,final Consumer<Optional<Exception>> onFailed){
            auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
                if(task.isSuccessful())
                {
                    onComplete.accept(null);
                }else{
                    onFailed.accept(Optional.empty());
                }
            }).addOnFailureListener(e -> onFailed.accept(Optional.of(e)));
        }
        public static void SignOut(){
            auth.signOut();
        }
        public static void SignUp(final String username, final String fullname,final String email
                ,final String password,final Consumer<Void> onComplete,
                                  final Consumer<Optional<Exception>> onFailed){
            final String imageDef="https://firebasestorage.googleapis.com/v0/b/instagramapp-3c84e.appspot.com/o/placeholder.png?alt=media&token=98a160e7-58c4-4332-94de-c99c97bfb41d";
            auth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful())
                        {
                            final String userid=getUid();
                            User user=new User(userid,username,fullname,imageDef,"");
                            Database.addUser(user,onComplete,onFailed);

                        }
                        else {
                            onFailed.accept(Optional.empty());
                        }
                    }).addOnFailureListener(e -> onFailed.accept(Optional.empty()));

        }
        @Nullable
        public static String getUid(){
            return auth.getUid();
        }
        public static boolean isLogin(){
            return auth.getCurrentUser() != null;
        }
    }


    public static class Storage{
        private static StorageTask uploadTask;
        private static StorageReference storagePostRef= FirebaseStorage.getInstance().getReference("posts");
        private static StorageReference storageProfileRef= FirebaseStorage.getInstance().getReference("profile_photos");

        public static void uploadImage(Uri mImageUri, ContentResolver cR,Boolean post, final Consumer<String> onComplete,
                                       final Consumer<Optional<Exception>> onFailed) {
            if (mImageUri != null){
                final StorageReference fileReference;
                if(post) {
                    fileReference = storagePostRef.child(System.currentTimeMillis()
                            + "." + getFileExtension(mImageUri, cR));
                }else {
                    fileReference = storageProfileRef.child(System.currentTimeMillis()
                            + "." + getFileExtension(mImageUri, cR));
                }

                uploadTask = fileReference.putFile(mImageUri);
                uploadTask.continueWithTask((Continuation) task -> {
                    if (!task.isSuccessful()) throw task.getException();
                    return fileReference.getDownloadUrl();
                }).addOnCompleteListener((OnCompleteListener<Uri>) task -> {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String miUrlOk = downloadUri.toString();

                        onComplete.accept(miUrlOk);
                    } else {
                        onFailed.accept(Optional.of(new Exception("Failed")));
//                        onComplete.accept("Failed");
                    }
                }).addOnFailureListener(e -> onFailed.accept(Optional.of(e)));

            } else {
                onFailed.accept(Optional.of(new Exception("No image selected")));
//                onComplete.accept("No image selected");
            }

        }
        private static String getFileExtension(Uri uri, ContentResolver cR){
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            return mime.getExtensionFromMimeType(cR.getType(uri));
        }

    }
}
