package com.example.instagram_app.Controller;

import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.instagram_app.LoginActivity;
import com.example.instagram_app.MainActivity;
import com.example.instagram_app.Model.User;
import com.example.instagram_app.RegisterActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;
import java.util.Optional;

public class Server {

    public static class Database{
        private static DatabaseReference databaseRef =
                FirebaseDatabase.getInstance().getReference();
        private static DatabaseReference SavesRef = databaseRef.child("Saves");
        private static DatabaseReference UserRef = databaseRef.child("Users");

        public static void addUser(final User user,final OnComplete<Void> onComplete,
                                   final OnFailed<Optional<Exception>> onFailed){
            UserRef.child(user.getId()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        onComplete.onComplete(null);
                    }
                    else {
                        onFailed.onFailed(Optional.<Exception>empty());
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    onFailed.onFailed(Optional.of(e));
                }
            });
        }

        public static void getUser(String uid,final OnComplete<User> onComplete,
                                   final OnFailed<Optional<Exception>> onFailed){
            UserRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user!=null) onComplete.onComplete(user);
                    else onFailed.onFailed(Optional.<Exception>empty());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Exception databaseException = databaseError.toException();
                    onFailed.onFailed(Optional.of(databaseException));
                }
            });
        }
        public static void getCurrentUser(final OnComplete<User> onComplete,
                                          final OnFailed<Optional<Exception>> onFailed){
            getUser(Auth.getUid(),onComplete,onFailed);
        }
    }

    public static class Auth{
        private static FirebaseAuth auth=FirebaseAuth.getInstance();

        public static void SignIn(final String email,final String password
                ,final OnComplete<Void> onComplete,final OnFailed<Optional<Exception>> onFailed){
            auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        onComplete.onComplete(null);
                    }else{
                        onFailed.onFailed(Optional.<Exception>empty());
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    onFailed.onFailed(Optional.of(e));
                }
            });
        }
        public static void SignOut(){
            auth.signOut();
        }
        public static void SignUp(final String username, final String fullname,final String email
                ,final String password,final OnComplete<Void> onComplete,
                                  final OnFailed<Optional<Exception>> onFailed){
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
                                onFailed.onFailed(Optional.<Exception>empty());
                            }
                        }

                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    onFailed.onFailed(Optional.<Exception>empty());
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
