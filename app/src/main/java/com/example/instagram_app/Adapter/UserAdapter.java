package com.example.instagram_app.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagram_app.Controller.Server;
import com.example.instagram_app.Fragment.ProfileFragment;
import com.example.instagram_app.Actiivity.MainActivity;
import com.example.instagram_app.Model.Notification;
import com.example.instagram_app.Model.User;
import com.example.instagram_app.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>{

    private Context mContext;
    private List<User> mUsers;
    private boolean isfragment;

    public UserAdapter(Context mContext, List<User> mUsers,boolean isfragment) {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.isfragment=isfragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.user_item,parent,false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final User user = mUsers.get(position);
        holder.btn_follow.setVisibility(View.VISIBLE);
        holder.username.setText(user.getUsername());
        holder.fullname.setText(user.getFullname());

        Glide.with(mContext).load(user.getImageurl()).into(holder.image_profile);
        isFollowing(user.getId(),holder.btn_follow);

        if(user.getId().equals(Server.Auth.getUid()))
        {
            holder.btn_follow.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (isfragment) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("profileid", user.getId());
                editor.apply();

                ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProfileFragment()).commit();
            } else {
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.putExtra("publisherid", user.getId());
                mContext.startActivity(intent);
            }
        });

        holder.btn_follow.setOnClickListener(v -> {
            if(holder.btn_follow.getText().toString().equals("follow"))
            {
                Server.Database.follow(Server.Auth.getUid(), user.getId(),true);

                addNotifications(user.getId());
            }
            else
            {
                Server.Database.follow(Server.Auth.getUid(), user.getId(),false);
            }
        });
    }

    private void addNotifications(String userid) {
        Notification notification= new Notification(Server.Auth.getUid(),"started following you","",false);

        Server.Database.addNotification(userid,notification,aVoid -> {},e -> {});

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView username;
        public TextView fullname;
        public CircleImageView image_profile;
        public Button btn_follow;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            username=itemView.findViewById(R.id.username);
            fullname=itemView.findViewById(R.id.fullname);
            image_profile=itemView.findViewById(R.id.image_profile);
            btn_follow=itemView.findViewById(R.id.btn_follow);
        }
    }

    private void isFollowing(final String userid, final Button button)
    {
         Server.Database.isFollowing(Server.Auth.getUid(),userid,aBoolean -> {
             if(aBoolean)
                 button.setText("following");
             else
                 button.setText("follow");
         },e -> {});
    }
}
