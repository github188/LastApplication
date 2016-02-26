package com.iermu.ui.adapter;

//

import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iermu.R;
import com.iermu.client.model.CamComment;
import com.iermu.client.util.DateUtil;
import com.iermu.ui.util.Util;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by zhoushaopei on 15/6/27.
 */
public class PublicLiveCommentsAdapter extends BaseAdapter implements View.OnClickListener {
    private FragmentActivity context;
    private List<CamComment> comments = new ArrayList<CamComment>();
    private PublicCommentListListener listener;
    private String uid;
    private int count;
    private String title;

    public PublicLiveCommentsAdapter(FragmentActivity context, PublicCommentListListener listener, String uid) {
        this.context = context;
        this.listener = listener;
        this.comments = new ArrayList<CamComment>();
        this.uid = uid;
    }

    public void notifyDataSetChanged(List<CamComment> list, int count) {
        this.count = count;
        this.comments = list;
        if (list == null) {
            this.comments = new ArrayList<CamComment>();
        }
        notifyDataSetChanged();
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int getCount() {
        return comments.size() + 1;
    }

    @Override
    public CamComment getItem(int position) {
        return comments.get(position - 1);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (position == 0) {
            convertView = View.inflate(context, R.layout.view_public_live_list_header, null);
            TextView textViewTitle = (TextView) convertView.findViewById(R.id.textViewTitle);
            TextView textViewNum = (TextView) convertView.findViewById(R.id.textViewNum);
            View buttonReport = convertView.findViewById(R.id.textViewReport);
            textViewTitle.setText(title);
            String commentTxt = context.getResources().getString(R.string.comment_txt);
            textViewNum.setText(String.format(commentTxt, count));
            buttonReport.setOnClickListener(this);
            return convertView;
        } else  {
            CamComment comment = getItem(position);
            if (comment.getUid().equals(uid)) {
                convertView = View.inflate(context, R.layout.item_public_live_comment_mine, null);
            } else {
                convertView = View.inflate(context, R.layout.item_public_live_comment_other, null);
            }

            ImageView imageViewAvator = (ImageView) convertView.findViewById(R.id.imageViewAvator);
            TextView textViewName = (TextView) convertView.findViewById(R.id.textViewName);
            TextView textViewContent = (TextView) convertView.findViewById(R.id.textViewContent);

            String avator = TextUtils.isEmpty(comment.getAvator()) ? "default" : comment.getAvator();
            Picasso.with(context)
                    .load(avator)
                    .placeholder(R.drawable.avator_img)
                    .into(imageViewAvator);
            if (comment.getUid().equals(uid)) {
                textViewName.setVisibility(View.GONE);
            }
            String date = DateUtil.formatDate(new Date(Long.parseLong(comment.getDate()) * 1000), context.getResources().getString(R.string.SHORT_DATE_FORMAT3));
            textViewName.setText(comment.getOwnerName() + "   " + date);
            textViewContent.setText(comment.getContent());

            return convertView;
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.textViewReport) {
            if (listener != null) {
                listener.onReportClick();;
            }
        }
    }

    public interface PublicCommentListListener {
        /**
         * 点击举报回调
         */
        void onReportClick();
    }
}
