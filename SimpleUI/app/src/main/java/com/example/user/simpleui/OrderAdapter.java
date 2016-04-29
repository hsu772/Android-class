package com.example.user.simpleui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2016/4/25.
 * This class is for simple_list_item_1
 */
public class OrderAdapter extends BaseAdapter{
    List<Order> orders; //store the order
    LayoutInflater inflater; //auto score in and score out

    // capture order auto
    public OrderAdapter(Context context, List<Order> orders){
        this.inflater = LayoutInflater.from(context);
        this.orders = orders;
    }

    @Override
    public int getCount() {
        return orders.size();// return the list number dependent by order
    }

    @Override
    public Object getItem(int position) {
        return orders.get(position); // get the item # information when select #
    }

    @Override
    // for use in internet, not use in this case.
    public long getItemId(int position) {
        return position;
    }

    @Override
    // when list view before, it will call getview by each time.
    // position: to know which item be select
    // converView:
    public View getView(int position, View convertView, ViewGroup parent) {

        Holder holder;

        if(convertView == null){ // if converView is null, mean it's the first time to get the converView.
            convertView = inflater.inflate(R.layout.listview_item, null);

            // holder to store the id information
            holder = new Holder();

           // TextView drinkName = (TextView) convertView.findViewById(R.id.drinkName);
           // TextView note = (TextView) convertView.findViewById(R.id.note);
            holder.drinkName = (TextView) convertView.findViewById(R.id.drinkName);
            holder.note = (TextView) convertView.findViewById(R.id.note);
            holder.storeInfo = (TextView) convertView.findViewById(R.id.store);

            convertView.setTag(holder); // store the id information at some place.

        }
        else{
            holder = (Holder) convertView.getTag();
        }

        holder.drinkName.setText(orders.get(position).getDrinkName());
        holder.note.setText(orders.get(position).getNote());
        holder.storeInfo.setText(orders.get(position).getStoreInfo());

        return convertView;
    }

    // creat class Holder for capture UI
    class Holder {
        TextView drinkName;
        TextView note;
        TextView storeInfo;
    }
}
