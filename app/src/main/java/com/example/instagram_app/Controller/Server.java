package com.example.instagram_app.Controller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.instagram_app.Model.Comment;
import com.example.instagram_app.Model.Notification;
import com.example.instagram_app.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class Server {

    public static class Database{
        private static DatabaseReference databaseRef =
                FirebaseDatabase.getInstance().getReference();
        private static DatabaseReference SavesRef = databaseRef.child("Saves");
        private static DatabaseReference UserRef = databaseRef.child("Users");
        private static DatabaseReference CommentsRef = databaseRef.child("Comments");
        private static DatabaseReference NotificationsRef = databaseRef.child("Notifications");

        public static void addUser(final User user,final Consumer<Void> onComplete,
                                   final Consumer<Optional<Exception>> onFailed){
            UserRef.child(user.getId()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        onComplete.accept(null);
                    }
                    else {
                        onFailed.accept(Optional.<Exception>empty());
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    onFailed.accept(Optional.of(e));
                }
            });
        }

        public static void getUser(String uid,final Consumer<User> onComplete,
                                   final Consumer<Optional<Exception>> onFailed){
            UserRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user!=null) onComplete.accept(user);
                    else onFailed.accept(Optional.<Exception>empty());
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
            ref.child(cid).setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    onComplete.accept(comment);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    onFailed.accept(Optional.ofNullable(e));
                }
            });
        }

        public static void addNotification(final String publisherId,
                                           final Notification notification,
                                           final Consumer<Void> onComplete,
                                           final Consumer<Optional<Exception>> onFailed){
            NotificationsRef.child(publisherId).push().setValue(notification)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    onComplete.accept(null);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    onFailed.accept(Optional.ofNullable(e));
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
                            (Exception)databaseError.toException()));
                }
            });
        }

    }

    public static class Auth{
        private static FirebaseAuth auth=FirebaseAuth.getInstance();

        public static void SignIn(final String email,final String password
                ,final Consumer<Void> onComplete,final Consumer<Optional<Exception>> onFailed){
            auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        onComplete.accept(null);
                    }else{
                        onFailed.accept(Optional.<Exception>empty());
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    onFailed.accept(Optional.of(e));
                }
            });
        }
        public static void SignOut(){
            auth.signOut();
        }
        public static void SignUp(final String username, final String fullname,final String email
                ,final String password,final Consumer<Void> onComplete,
                                  final Consumer<Optional<Exception>> onFailed){
            final String imageDef="https://firebasestorage.googleapis.com/v0/b/instagramapp-3c84e.appspot.com/o/placeholder.png?alt=media&token=98a160e7-58c4-4332-94de-c99c97bfb41d";
            auth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                final String userid=getUid();
                                User user=new User(userid,username,fullname,imageDef,"");
                                Database.addUser(user,onComplete,onFailed);

                            }
                            else {
                                onFailed.accept(Optional.<Exception>empty());
                            }
                        }

                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    onFailed.accept(Optional.<Exception>empty());
                }
            });

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
        private static FirebaseStorage storage = FirebaseStorage.getInstance();

        public void uploadImage(String imageURI){

        }

    }
}
