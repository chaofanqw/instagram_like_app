package net.dunrou.mobile.bean;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.dunrou.mobile.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EventCommentAdapter extends BaseAdapter {
    private Context context;
    private List<EventComment> eventComments;
    private List<EventComment> displayComments;
    private EventAdapter.ViewHolder parent;

    public EventCommentAdapter(Context context, @NonNull List<EventComment> eventComments, EventAdapter.ViewHolder holder) {
        this.context = context;
        this.eventComments = eventComments;
        if (eventComments.size() <= 3) {
            this.displayComments = eventComments;
        } else {
            this.displayComments = new ArrayList<>();
            this.displayComments.add(this.eventComments.get(0));
            this.displayComments.add(this.eventComments.get(1));
            this.displayComments.add(this.eventComments.get(2));
        }
        parent = holder;
    }

    public void setData(List<EventComment> displayComments) {
        this.displayComments = displayComments;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return displayComments.size();
    }

    @Override
    public EventComment getItem(int position) {
        return displayComments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_feed_reply, null);
            convertView.setTag(new ViewHolder(convertView));
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        EventComment replyItem = getItem(position);
        SpannableString msp = new SpannableString(replyItem.getFeedCommentUsername() + ": " + replyItem.getFeedCommentContent());
        msp.setSpan(new ForegroundColorSpan(0xff6b8747), 0, replyItem.getFeedCommentUsername().length() + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.reply.setText(msp);
        return convertView;
    }

    public void reply() {
        if (displayComments.size() == 3) {
            parent.commentNumber.setVisibility(View.GONE);
            setData(eventComments);
        } else {
            parent.commentNumber.setVisibility(View.VISIBLE);
            List<EventComment> single = new ArrayList<>();
            single.add(eventComments.get(0));
            single.add(eventComments.get(1));
            single.add(eventComments.get(2));
            setData(single);
        }
    }

    public int getTotal() {
        return eventComments.size();
    }

    class ViewHolder {
        @BindView(R.id.tv_reply) TextView reply;

        public ViewHolder(View convertView) {
            ButterKnife.bind(this, convertView);
        }

        @OnClick(R.id.tv_reply)
        public void changeCommentStatus() {
            reply.setClickable(false);
            if (getTotal() > 3) {
                reply();
            }
            reply.setClickable(true);
        }
    }
}
