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

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EventCommentAdapter extends BaseAdapter {
    private Context context;
    private List<EventComment> eventComments;

    public EventCommentAdapter(Context context, @NonNull List<EventComment> eventComments) {
        this.context = context;
        this.eventComments = eventComments;
    }

    @Override
    public int getCount() {
        return eventComments.size();
    }

    @Override
    public EventComment getItem(int position) {
        return eventComments.get(position);
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

    static class ViewHolder {
        @BindView(R.id.tv_reply) TextView reply;

        public ViewHolder(View convertView) {
            ButterKnife.bind(this, convertView);
        }
    }
}
