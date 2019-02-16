package com.foursquare.takehome;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public final class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.MyViewHolder> {
    private List<Person> personList;

    public PersonAdapter(List<Person> list){

        this.personList = list;

    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_row,viewGroup,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        Person p = personList.get(i);
        Date a = new Date(p.getArriveTime());
        Date b = new Date(p.getLeaveTime());
        SimpleDateFormat aS = new SimpleDateFormat("hh:mm aa",Locale.US);
        SimpleDateFormat bS = new SimpleDateFormat("hh:mm aa",Locale.US);
        String time = aS.format(a) + " - " + bS.format(b);
        myViewHolder.visitor.setText(p.getName());
        myViewHolder.time.setText(time);
    }


    @Override
    public int getItemCount() {
        return personList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView visitor;
        public TextView time;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            visitor = itemView.findViewById(R.id.visitor_name);
            time = itemView.findViewById(R.id.visitor_time);
        }
    }
}
