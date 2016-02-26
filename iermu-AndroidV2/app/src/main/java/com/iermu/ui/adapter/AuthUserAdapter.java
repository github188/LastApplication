package com.iermu.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.iermu.R;
import com.iermu.client.model.GrantUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhoushaopei on 15/8/14.
 */
public class AuthUserAdapter extends BaseAdapter {

    private Context ctx;
    private List<GrantUser> list = new ArrayList<GrantUser>();

    public AuthUserAdapter(Context context) {
        this.ctx = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder  ;
        if(convertView ==null){
            holder = new ViewHolder();
            convertView= View.inflate(ctx, R.layout.auth_user, null);
            holder.name = (TextView)convertView.findViewById(R.id.name);
            convertView.setTag(holder);
        } else {
            holder= (ViewHolder) convertView.getTag();
        }
        GrantUser item = (GrantUser) getItem(position);
        holder.name.setText(TextUtils.isEmpty(item.getName()) ? "" : item.getName());
        return convertView;
    }

    public void removeUser(int position) {
        if (list == null) return;
        list.remove(position);
        notifyDataSetChanged();
    }

    public void notifyDataChange(List<GrantUser> users) {
        if(users == null) {
            users = new ArrayList<GrantUser>();
        }
        list = users;
        notifyDataSetChanged();
    }

    class ViewHolder{
        TextView  name;
    }
}
