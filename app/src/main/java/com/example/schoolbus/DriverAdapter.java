package com.example.schoolbus;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class DriverAdapter extends RecyclerView.Adapter<DriverAdapter.DriverViewHolder> {
    private final Context mContext;
    private final List<StudentInformation> mStudentInformation;

    public DriverAdapter(Context context, List<StudentInformation> studentInformation) {
        mContext = context;
        mStudentInformation = studentInformation;
    }

    @NonNull
    @Override
    public DriverViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.list_driver, parent, false);
        return new DriverViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DriverViewHolder holder, int position) {
        StudentInformation studentCurrent = mStudentInformation.get(position);
        holder.textViewName.setText(studentCurrent.getName());
        holder.textViewStage.setText(studentCurrent.getStage());
        holder.gender.setText(studentCurrent.getGender());
        Picasso.get().load(studentCurrent.getImageUrl())
                .fit().centerCrop()
                .into(holder.imageView);
        holder.goLocation.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + studentCurrent.getLatitude() + "," + studentCurrent.getLongitude()));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            holder.goLocation.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mStudentInformation.size();
    }

    public static class DriverViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewName;
        public TextView textViewStage;

        public TextView gender;

        public ImageView imageView;
        public Button goLocation;

        public DriverViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.name_view);
            textViewStage = itemView.findViewById(R.id.stage_view);
            imageView = itemView.findViewById(R.id.driver_icon);
            gender = itemView.findViewById(R.id.gender_view);
            goLocation = itemView.findViewById(R.id.goToLocation);
        }
    }
}
