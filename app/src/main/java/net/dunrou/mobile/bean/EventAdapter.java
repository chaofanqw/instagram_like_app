package net.dunrou.mobile.bean;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.ninegrid.ImageInfo;
import com.lzy.ninegrid.NineGridView;
import com.lzy.ninegrid.preview.NineGridViewClickAdapter;
import com.lzy.widget.CircleImageView;
import com.squareup.picasso.Picasso;

import net.dunrou.mobile.R;
import net.dunrou.mobile.base.firebaseClass.FirebaseEventComment;
import net.dunrou.mobile.base.firebaseClass.FirebaseEventLike;
import net.dunrou.mobile.network.firebaseNetwork.FirebaseUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.inputmethod.EditorInfo.IME_ACTION_DONE;

public class EventAdapter extends BaseAdapter {
    private Context context;
    private List<EventItem> data;
    private LayoutInflater mInflater;
    private String user;

    public void setData(List<EventItem> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public EventAdapter(Context context, List<EventItem> data, String username) {
        this.context = context;
        this.data = data;
        mInflater = LayoutInflater.from(context);
        this.user = username;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public EventItem getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_feed, parent, false);
            convertView.setTag(new ViewHolder(convertView));
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        EventItem item = getItem(position);

        holder.information.setText(item.getEventPostId());
        holder.postIndex.setText(String.valueOf(position));
        if (item.getContent() == null) {
            holder.content.setVisibility(View.GONE);
        } else {
            holder.content.setVisibility(View.VISIBLE);
            holder.content.setText(item.getContent());
        }
        holder.username.setText(item.getUserId());
        holder.createTime.setText(item.getCreateTime());
        String defaultAvatar = "android.resource://net.dunrou.mobile/" + R.drawable.profile_n;
        setImage(context, holder.avatar, item.avatar == null ? defaultAvatar : item.avatar.smallPicUrl);

        ArrayList<ImageInfo> imageInfo = new ArrayList<>();
        List<EventPic> imageDetails = item.getAttachments();
        if (imageDetails != null) {
            for (EventPic imageDetail : imageDetails) {
                ImageInfo info = new ImageInfo();
                info.setThumbnailUrl(imageDetail.smallImageUrl);
                info.setBigImageUrl(imageDetail.imageUrl);
                imageInfo.add(info);
            }
        }
        holder.nineGrid.setAdapter(new NineGridViewClickAdapter(context, imageInfo));
        holder.likeNumber.setText(String.valueOf(item.likeCount));

        if (item.feedReplies == null) {
            holder.comments.setVisibility(View.GONE);
            holder.commentNumber.setText("0");
        } else {
            holder.comments.setVisibility(View.VISIBLE);
            holder.comments.setAdapter(new EventCommentAdapter(context, item.getFeedReplies()));
            holder.commentNumber.setText(String.valueOf(item.getFeedReplies().size()));
        }

        if (item.selfLike) {
            holder.likeButton.setImageResource(R.drawable.like_p);
            holder.selfLikeInformation.setText("1");
        } else {
            holder.likeButton.setImageResource(R.drawable.like_n);
            holder.selfLikeInformation.setText("0");
        }
        holder.likePad.setClickable(true);

        return convertView;
    }

    private void setImage(Context context, ImageView imageView, String url) {
//        Glide.with(context).load(url)//
//                .placeholder(R.drawable.ic_default_color)//
//                .error(R.drawable.ic_default_color)//
//                .diskCacheStrategy(DiskCacheStrategy.ALL)//
//                .into(imageView);
        Picasso.with(context).load(url)//
                .placeholder(R.drawable.ic_default_color)//
                .error(R.drawable.ic_default_color)//
                .into(imageView);
    }

    class ViewHolder {
        @BindView(R.id.tv_content) TextView content;
        @BindView(R.id.nineGrid) NineGridView nineGrid;
        @BindView(R.id.tv_username) TextView username;
        @BindView(R.id.tv_createTime) TextView createTime;
        @BindView(R.id.avatar) CircleImageView avatar;
        @BindView(R.id.lv_comments) ListView comments;
        @BindView(R.id.like_button) ImageView likeButton;
        @BindView(R.id.like_pad) LinearLayout likePad;
        @BindView(R.id.like_number) TextView likeNumber;
        @BindView(R.id.comment_pad) LinearLayout commentPad;
        @BindView(R.id.comment_number) TextView commentNumber;
        @BindView(R.id.information) TextView information;
        @BindView(R.id.self_like) TextView selfLikeInformation;
        @BindView(R.id.post_index) TextView postIndex;

        private PopupWindow editWindow;
        private View rootView;

        public ViewHolder(View convertView) {
            rootView = convertView;
            ButterKnife.bind(this, convertView);
        }

        @OnClick(R.id.comment_pad)
        public void comment() {
            final View editView = mInflater.inflate(R.layout.comment_input, null);
            editWindow = new PopupWindow(editView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            editWindow.setOutsideTouchable(true);
            editWindow.setFocusable(true);
            editWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));

            final RelativeLayout sendButton = editView.findViewById(R.id.send_button);
            final EditText replyEdit = editView.findViewById(R.id.reply);
            replyEdit.setFocusable(true);
            replyEdit.requestFocus();
            replyEdit.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                @Override
                public void afterTextChanged(Editable editable) {
                    if (editable.toString().trim().length() == 0) {
                        sendButton.setClickable(false);
                    } else {
                        sendButton.setClickable(true);
                    }
                }
            });
            replyEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    Date date = Calendar.getInstance().getTime();
                    String text = replyEdit.getText().toString();
                    if (text.trim().length() != 0) {
                        editWindow.dismiss();
                        FirebaseEventComment firebaseEventComment = new FirebaseEventComment(user, information.getText().toString(), text, date);
                        new FirebaseUtil().insertComment(firebaseEventComment, Integer.parseInt(postIndex.getText().toString()));
                        return false;
                    }
                    return true;
                }
            });
            sendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    editWindow.dismiss();
                    Date date = Calendar.getInstance().getTime();
                    String text = replyEdit.getText().toString();
                    FirebaseEventComment firebaseEventComment = new FirebaseEventComment(user, information.getText().toString(), text, date);
                    new FirebaseUtil().insertComment(firebaseEventComment, Integer.parseInt(postIndex.getText().toString()));
                }
            });
            sendButton.setClickable(false);
            // 以下两句不能颠倒
            editWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
            editWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            editWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
            // 显示键盘
            final InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

            editWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    if (imm.isActive())
                        imm.toggleSoftInput(0, InputMethodManager.RESULT_SHOWN);
                }
            });
        }

        @OnClick(R.id.like_pad)
        public void like() {
            likePad.setClickable(false);
            Date date = Calendar.getInstance().getTime();
            FirebaseEventLike firebaseEventLike = new FirebaseEventLike(information.getText().toString(), user, date, selfLikeInformation.getText().equals("0"));
            new FirebaseUtil().insertLike(firebaseEventLike, Integer.parseInt(postIndex.getText().toString()));
        }

//        @OnClick(R.id.delete)
//        public void delete(View view) {
//            final GlobalDialog delDialog = new GlobalDialog(context);
//            delDialog.setCanceledOnTouchOutside(true);
//            delDialog.getTitle().setText(R.string.remove_tip_title);
//            delDialog.getContent().setText(R.string.remove_tip);
//            delDialog.setLeftBtnText("Cancel");
//            delDialog.setRightBtnText("Confirm");
//            delDialog.setLeftOnclick(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Toast.makeText(context, "Cancel", Toast.LENGTH_SHORT).show();
//                    delDialog.dismiss();
//                }
//            });
//            delDialog.setRightOnclick(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Toast.makeText(context, "Confirm", Toast.LENGTH_SHORT).show();
//                    delDialog.dismiss();
//                }
//            });
//            delDialog.show();
//        }

//        @Override
//        public void onClick(View v) {
//            switch (v.getId()) {
//                case R.id.favour:
//                    Toast.makeText(context, "Like + 1", Toast.LENGTH_SHORT).show();
//                    if (window != null) window.dismiss();
//                    break;
//                case R.id.comment:
//                    View editView = mInflater.inflate(R.layout.comment_input, null);
//                    editWindow = new PopupWindow(editView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                    editWindow.setOutsideTouchable(true);
//                    editWindow.setFocusable(true);
//                    editWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
//
//                    EditText replyEdit = (EditText) editView.findViewById(R.id.reply);
//                    replyEdit.setFocusable(true);
//                    replyEdit.requestFocus();
//                    // 以下两句不能颠倒
//                    editWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
//                    editWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
//                    editWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
//                    // 显示键盘
//                    final InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
//
//                    editWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
//                        @Override
//                        public void onDismiss() {
//                            if (imm.isActive())
//                                imm.toggleSoftInput(0, InputMethodManager.RESULT_SHOWN);
//                        }
//                    });
//                    if (window != null) window.dismiss();
//                    break;
//            }
//        }
    }
}
