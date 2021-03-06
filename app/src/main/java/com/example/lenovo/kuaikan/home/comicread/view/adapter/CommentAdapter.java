package com.example.lenovo.kuaikan.home.comicread.view.adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lenovo.kuaikan.R;
import com.example.lenovo.kuaikan.community.comment.model.data.CommentBean;
import com.example.lenovo.kuaikan.community.comment.view.CommentActivity;
import com.example.lenovo.kuaikan.utils.ListUtil;
import com.example.lenovo.kuaikan.utils.dateutil.DateUtil;
import com.example.lenovo.kuaikan.widget.glide.GlideImageView;

import java.util.List;

/**
 * Created by Zhanglibin on 2017/4/11.
 */

public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<CommentBean.DataBean.CommentFloorsBean> mComments;
    Context mContext;
    private int mHeight;
    private String comicId;

    public CommentAdapter(List<CommentBean.DataBean.CommentFloorsBean> mComments, Context context) {
        this.mComments = mComments;
        mContext = context;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mComments.size()) {
            return -1;
        } else {
            return 1;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == -1) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.comment_item_buttom, parent, false);
            CommentButtomHV viewHolder = new CommentButtomHV(view);
            return viewHolder;
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_read_comment_item, parent, false);
        CommentViewHolder viewHolder = new CommentViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position < 10) {
            if (holder instanceof CommentViewHolder && ListUtil.isNotEmpty(mComments)) {
                ((CommentViewHolder) holder).tvUseName.setText(mComments.get(position).getRoot().getUser().getNickname());
                ((CommentViewHolder) holder).mImageView.setRounImage(mComments.get(position).getRoot().getUser().getAvatar_url());
                //转化时间格式 MM-dd HH:mm
                String date = DateUtil.formatLongToDates(mComments.get(position).getRoot().getCreated_at() * 1000);
                ((CommentViewHolder) holder).mTvCreatTime.setText(date);
                ((CommentViewHolder) holder).mTvCreatDetails.setText(mComments.get(position).getRoot().getContent());
                ((CommentViewHolder) holder).mTvLikeNum.setText(mComments.get(position).getRoot().getLikes_count() + "");
                mHeight += ((CommentViewHolder) holder).mConstraintLayout.getLayoutParams().height;
            } else if (holder instanceof CommentButtomHV) {
                ((CommentButtomHV) holder).mLayoutItemBottom.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CommentActivity.toComment(mContext, comicId, 1);
                    }
                });
            }
        }


    }

    @Override
    public int getItemCount() {
        return mComments.size() + 1;
    }

    class CommentViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvUseName;
        private final GlideImageView mImageView;
        private final TextView mTvCreatTime;
        private final TextView mTvCreatDetails;
        private final TextView mTvLikeNum;
        private final ConstraintLayout mConstraintLayout;

        public CommentViewHolder(View itemView) {
            super(itemView);
            tvUseName = (TextView) itemView.findViewById(R.id.tv_user_name);
            mImageView = (GlideImageView) itemView.findViewById(R.id.head_glideimageview);
            mTvCreatTime = (TextView) itemView.findViewById(R.id.tv_creat_time);
            mTvCreatDetails = (TextView) itemView.findViewById(R.id.tv_creat_details);
            mTvLikeNum = (TextView) itemView.findViewById(R.id.tv_like_num);
            mConstraintLayout = (ConstraintLayout) itemView.findViewById(R.id.constraintLayout);


        }
    }

    class CommentButtomHV extends RecyclerView.ViewHolder {

        private final ConstraintLayout mLayoutItemBottom;

        public CommentButtomHV(View itemView) {
            super(itemView);
            mLayoutItemBottom = (ConstraintLayout) itemView.findViewById(R.id.layout_item_bottom);
        }
    }

    public void setComicId(String comicId) {
        this.comicId = comicId;
    }

    public int getItemAllHight() {
        return mHeight;
    }
}
