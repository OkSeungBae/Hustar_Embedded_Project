package com.example.project_toilet;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TotiItemAdapter extends RecyclerView.Adapter<TotiItemAdapter.ViewHolder> {

    ArrayList<TotiInfo> items = new ArrayList<TotiInfo>();

    @NonNull
    @Override
    public TotiItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.recycleview_item, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TotiItemAdapter.ViewHolder holder, int position) {
        TotiInfo item = items.get(position);
        holder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(ArrayList<TotiInfo> items)
    {
        this.items = items;
    }

    public void updateItem(int i)
    {
        int[] update = {i, 80, 80};

        TotiInfo item = items.get(0);
        item.setRemain(update);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView textView_user;
        TextView remain[];
        TextView state[];
        Drawable imgMan;
        Drawable imgWoman;

        View itemView;
        public ViewHolder(View itemView)
        {
            super(itemView);
            this.itemView = itemView;
            textView_user = itemView.findViewById(R.id.recyc_name);

            remain = new TextView[3];
            remain[0] = itemView.findViewById(R.id.recyc_toilet);
            remain[1] = itemView.findViewById(R.id.recyc_toilet2);
            remain[2] = itemView.findViewById(R.id.recyc_toilet3);

            state = new TextView[3];
            state[0] = itemView.findViewById(R.id.state_toilet1);
            state[1] = itemView.findViewById(R.id.state_toilet2);
            state[2] = itemView.findViewById(R.id.state_toilet3);

            imgMan = itemView.getResources().getDrawable(R.drawable.toilet_man);
            imgWoman = itemView.getResources().getDrawable(R.drawable.toilet_woman);
            imgMan.setBounds(0, 0, 40, 70);
            imgWoman.setBounds(0, 0, 40, 70);

        }
        public void setItem(TotiInfo item)
        {
            int[] remainToti = item.getRemain();
            textView_user.setText(item.getName());
            if(item.getSex() == 0)
            {
                textView_user.setCompoundDrawables(null,null,imgMan,null);
            }
            else
            {
                textView_user.setCompoundDrawables(null,null,imgWoman,null);
            }

            for (int i = 0; i < 3; i++) {
                if(remainToti[i] >= 75)
                {
                    state[i].setBackground(itemView.getResources().getDrawable(R.drawable.state_border1));
                }
                else if(remainToti[i] >= 45)
                {
                    state[i].setBackground(itemView.getResources().getDrawable(R.drawable.state_border2));
                }
                else
                {
                    state[i].setBackground(itemView.getResources().getDrawable(R.drawable.state_border3));
                }
                remain[i].setText(remainToti[i] + "%");
            }


        }
    }
}
