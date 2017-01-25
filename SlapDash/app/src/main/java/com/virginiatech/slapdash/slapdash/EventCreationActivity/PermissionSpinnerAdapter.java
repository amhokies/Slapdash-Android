package com.virginiatech.slapdash.slapdash.EventCreationActivity;

import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.virginiatech.slapdash.slapdash.R;

import java.util.List;

/**
 * Created by nima on 10/11/16.
 */

public class PermissionSpinnerAdapter implements SpinnerAdapter {
    private List<String> permissionA;

    public PermissionSpinnerAdapter(List<String> permissionA){
        super();
        this.permissionA = permissionA;
    }
    @Override
    public View getDropDownView(int i, View view, ViewGroup viewGroup) {
        View item = LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.simple_permission_item, viewGroup, false);

        TextView permission = (TextView) item.findViewById(R.id.simple_permission_item);
        permission.setText(permissionA.get(i));
        return item;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {}

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {}

    @Override
    public int getCount() {
        return permissionA.size();
    }

    @Override
    public Object getItem(int i) {
        return permissionA.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View item = LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.simple_permission_item, viewGroup, false);

        TextView permission = (TextView) item.findViewById(R.id.simple_permission_item);
        permission.setText(permissionA.get(i));
        return item;
    }

    @Override
    public int getItemViewType(int i) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return permissionA.isEmpty();
    }
}