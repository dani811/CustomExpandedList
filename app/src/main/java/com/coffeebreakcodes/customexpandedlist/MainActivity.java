package com.coffeebreakcodes.customexpandedlist;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kagan Kartal on 24.02.2018.
 */

public class MainActivity extends AppCompatActivity {

    private List<Item> itemList;
    private Item selectedItem;
    private int lastSelectedPosition = 0;

    private boolean isRecentlyClosed = false;       //Flag for collapsed list item

    private RecyclerView recyclerView;
    private TextView textViewLabel;
    private ImageView imageViewLeft;
    private ImageView imageViewRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        textViewLabel = (TextView) findViewById(R.id.textViewLabel);
        imageViewLeft = (ImageView) findViewById(R.id.imageViewLeft);
        imageViewRight = (ImageView) findViewById(R.id.imageViewRight);

        final Item item = getItemListFromAsset();
        if (item != null) {
            itemList = new ArrayList<>(item.getItemList());

            //Initial positioning
            selectedItem = itemList.get(0);
            itemList.get(0).setSelected(true);
            textViewLabel.setText(selectedItem.getTitle());
            imageViewLeft.setVisibility(View.GONE);

            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            //Divider for RecyclerView
            recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(),
                    DividerItemDecoration.VERTICAL));
            recyclerView.setAdapter(new ItemRWAdapter(this, itemList));

            imageViewLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (lastSelectedPosition > 0) {
                        selectItem(lastSelectedPosition - 1, Const.NAV_LEFT);
                    }
                }
            });

            imageViewRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (lastSelectedPosition < itemList.size() - 1) {
                        selectItem(lastSelectedPosition + 1, Const.NAV_RIGHT);
                    }
                }
            });
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final AlertDialog alertDialog = builder.create();
            alertDialog.setTitle("Uyarı");
            alertDialog.setMessage("Hata oluştu. Hata kodu: 1108");
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Tekrar Dene", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    alertDialog.dismiss();
                    init();
                }
            });
            alertDialog.show();
        }
    }

    /**
     * Controls the selected item and updates the interface
     *
     * @param position position of selected item (int)
     * @param navType  type of navigation source (int)
     */
    public void selectItem(int position, int navType) {
        //Checks whether the current selected item has children to expand/collapse
        if (navType == Const.NAV_RIGHT && itemList.get(position - 1).getItemList().size() > 0 &&
                !itemList.get(position - 1).isExpanded()) {
            position = position - 1;
        } else if (navType == Const.NAV_LEFT && itemList.get(position + 1).getItemList().size() > 0 &&
                itemList.get(position + 1).isExpanded()) {
            position = position + 1;
        }

        //Visibility of navigation buttons on top of screen
        imageViewLeft.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
        imageViewRight.setVisibility(position == itemList.size() - 1 ? View.GONE : View.VISIBLE);

        isRecentlyClosed = false;

        selectedItem = itemList.get(position);
        textViewLabel.setText(selectedItem.getTitle());
        itemList.get(position).setSelected(true);

        if (position != lastSelectedPosition) {
            itemList.get(lastSelectedPosition).setSelected(false);
        }
        lastSelectedPosition = position;

        //Delete children of collapsed list item from list
        if (selectedItem.getItemList().size() > 0) {
            if (selectedItem.isExpanded() && navType != Const.NAV_RIGHT) {
                deleteItemChildren(selectedItem, position);
            }
        }

        List<Item> updatedList = new ArrayList<>();

        //Add selected item and items till the selected item
        for (int i = 0; i < position + 1; i++) {
            updatedList.add(itemList.get(i));
        }

        //Add items if selected item has children
        if (selectedItem.getItemList().size() > 0 && !selectedItem.isExpanded() &&
                !isRecentlyClosed && navType != Const.NAV_LEFT) {
            updatedList.get(updatedList.size() - 1).setExpanded(true);
            for (int i = 0; i < selectedItem.getItemList().size(); i++) {
                Item item = selectedItem.getItemList().get(i);
                item.setExpanded(false);
                item.setHierarchy(selectedItem.getHierarchy() + 1);
                updatedList.add(item);
            }
        }

        //Add items coming after selected item
        for (int i = position + 1; i < itemList.size(); i++) {
            updatedList.add(itemList.get(i));
        }

        itemList = updatedList;
        recyclerView.setAdapter(new ItemRWAdapter(this, updatedList));
        if (navType != Const.NAV_BASIC) {
            recyclerView.getLayoutManager().scrollToPosition(position);
        }
    }

    /**
     * Gets JSON data from .txt file and converts to Item object
     *
     * @return
     */
    private Item getItemListFromAsset() {
        String json = "";
        JSONObject objTable = null;

        try {
            InputStream inputStream = getAssets().open("tree.txt");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            json = new String(buffer, "UTF-8");

        } catch (IOException ex) {
            ex.printStackTrace();
            Toast.makeText(
                    this, "Dosya okunurken hata oluştu!", Toast.LENGTH_SHORT).show();
            return null;
        }

        try {
            JSONObject obj = new JSONObject(json);
            objTable = obj.getJSONObject("tableofcontents");

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(
                    this, "Dosya okunurken hata oluştu!", Toast.LENGTH_SHORT).show();
            return null;
        }

        return new Gson().fromJson(objTable.toString(), Item.class);
    }

    /**
     * Deletes all related children of selected parent recursively
     * in n-leveled parent-child list
     *
     * @param parentItem selected item
     * @param position   position of selected item
     */
    private void deleteItemChildren(Item parentItem, int position) {
        for (int i = 0; i < parentItem.getItemList().size(); i++) {
            Item innerItem = itemList.get(position + 1);
            itemList.remove(position + 1);

            if (innerItem.isExpanded() && innerItem.getItemList().size() > 0) {
                deleteItemChildren(innerItem, position);
            }
        }
        itemList.get(position).setExpanded(false);
        isRecentlyClosed = true;
    }
}

