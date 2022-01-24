package com.example.bodyworks.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bodyworks.Modals.WorkOut;
import com.example.bodyworks.R;
import com.example.bodyworks.eventBus.MyUpdateEvent;
import com.google.firebase.database.FirebaseDatabase;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MyWorkOutAdapter extends RecyclerView.Adapter<MyWorkOutAdapter.MyCartViewHolder> {

    private Context context;
    private List<WorkOut> workoutList;
    private String Uid;

    public MyWorkOutAdapter(Context context, List<WorkOut> workoutList, Activity activity,String uid) {
        this.context = context;
        this.workoutList = workoutList;
        this.Uid = uid;

    }

    @NonNull
    @Override
    public MyCartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyCartViewHolder((LayoutInflater.from(context).inflate(R.layout.workout_list_item,parent,false)));
    }

    @Override
    public void onBindViewHolder(@NonNull MyCartViewHolder holder, int position) {
        holder.nametxt.setText(new StringBuffer("Name. ").append(workoutList.get(position).getName()));
        holder.durationtxt.setText(new StringBuffer().append(workoutList.get(position).getDuration()));
        holder.counttxt.setText(new StringBuffer().append(workoutList.get(position).getCount()));
        holder.descriptiontxt.setText(new StringBuffer().append(workoutList.get(position).getDescription()));


        holder.deleteBtn.setOnClickListener(v ->{
            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setTitle("Delete workout")
                    .setMessage("Do you really want to delete " + workoutList.get(position).getName().toString() + "from list")
                    .setNegativeButton("CANCEL", (dialog1, which) -> dialog1.dismiss())
                    .setPositiveButton("YES", (dialog12, which) -> {
                        notifyItemRemoved(position);
                        deleteFromFirebase(workoutList.get(position));
                        dialog12.dismiss();
                    }).create();
            dialog.show();
        });



    }

    private void deleteFromFirebase(WorkOut workOut) {

        FirebaseDatabase.getInstance().getReference("User").child(Uid).child("WorkOut").child(workOut.getId())
                .removeValue()
                .addOnSuccessListener(aVoid -> {
                    EventBus.getDefault().postSticky(new MyUpdateEvent());

                });
    }

    @Override
    public int getItemCount() {
        return workoutList.size();
    }

    public class MyCartViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.deleteBtn)
        ImageView deleteBtn;
        ImageView imageView;
        @BindView(R.id.nametxt)
        TextView nametxt;
        @BindView(R.id.durationtxt)
        TextView durationtxt;
        @BindView(R.id.descriptiontxt)
        TextView descriptiontxt;
        @BindView(R.id.counttxt)
        TextView counttxt;

        Unbinder unbinder;
        public MyCartViewHolder(@NonNull View itemView) {
            super(itemView);

            unbinder = ButterKnife.bind(this,itemView);

        }
    }
}
