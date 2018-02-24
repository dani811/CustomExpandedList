package com.coffeebreakcodes.customexpandedlist;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Kagan Kartal on 24.02.2018.
 */

public class ItemRWAdapter extends RecyclerView.Adapter<ItemRWAdapter.ViewHolder> {

    private MainActivity activity;
    private List<Item> itemList;


    public ItemRWAdapter(MainActivity activity, List<Item> itemList) {
        this.activity = activity;
        this.itemList = itemList;
    }

    @Override
    public ItemRWAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list, parent, false);
        return new ItemRWAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ItemRWAdapter.ViewHolder holder, final int position) {
        Item item = itemList.get(position);
        holder.textViewTitle.setText(item.getTitle());

        //Places and changes images of items with children
        if (item.getItemList().size() > 0) {
            if (item.isExpanded()) {
                holder.imageViewIcon.setImageResource(R.drawable.ic_remove_white_48dp);
            } else {
                holder.imageViewIcon.setImageResource(R.drawable.ic_add_white_48dp);
            }
        } else {
            holder.imageViewIcon.setVisibility(View.GONE);
        }

        //Arranges the indent of rows in order to hierarchy
        RelativeLayout.LayoutParams params =
                (RelativeLayout.LayoutParams) holder.textViewTitle.getLayoutParams();
        params.setMargins(Const.MARGIN_START +
                (item.getHierarchy() * Const.MARGIN_INDENT), 0, 0, 0);
        holder.textViewTitle.setLayoutParams(params);

        //Changes background color of rows by selection
        if (item.isSelected()) {
            holder.layoutHolder.setBackgroundColor(
                    activity.getResources().getColor(R.color.colorRed));
        } else {
            holder.layoutHolder.setBackgroundColor(
                    activity.getResources().getColor(R.color.colorGrey));
        }

        holder.layoutHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.selectItem(position, Const.NAV_BASIC);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        ImageView imageViewIcon;
        RelativeLayout layoutHolder;

        public ViewHolder(View view) {
            super(view);
            textViewTitle = (TextView) view.findViewById(R.id.textViewTitle);
            imageViewIcon = (ImageView) view.findViewById(R.id.imageViewIcon);
            layoutHolder = (RelativeLayout) view.findViewById(R.id.layoutHolder);
        }
    }
}
