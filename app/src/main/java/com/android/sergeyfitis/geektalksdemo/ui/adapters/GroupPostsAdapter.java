package com.android.sergeyfitis.geektalksdemo.ui.adapters;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.sergeyfitis.geektalksdemo.R;
import com.android.sergeyfitis.geektalksdemo.helpers.Utils;
import com.android.sergeyfitis.geektalksdemo.models.GroupPostData;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Serhii Yaremych on 02.11.2015.
 */
public class GroupPostsAdapter extends RecyclerView.Adapter<GroupPostsAdapter.PostViewHolder> {
    private List<GroupPostData> posts;
    @Nullable
    private OnPostItemClick onPostItemClick;

    public GroupPostsAdapter(List<GroupPostData> posts) {
        this.posts = posts;
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PostViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.group_post_item_layout, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(PostViewHolder holder, int position) {
        holder.setPost(posts.get(position));
    }

    @Override
    public int getItemCount() {
        return posts == null ? 0 : posts.size();
    }

    public void setOnPostItemClick(OnPostItemClick onPostItemClick) {
        this.onPostItemClick = onPostItemClick;
    }


    class PostViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_post_creator)
        ImageView ivPostCreator;
        @Bind(R.id.tv_post_title)
        TextView tvPostTitle;
        @Bind(R.id.tv_post_date_created)
        TextView tvPostDateCreated;
        @Bind(R.id.iv_post_cover)
        ImageView ivPostCover;
        @Bind(R.id.tv_post_description)
        TextView tvPostDescription;

        private GroupPostData post;

        public PostViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(v -> {
                if (onPostItemClick != null) {

                    onPostItemClick.onPostItemClick(post,
                            new Pair<>(ivPostCover, itemView.getResources().getString(R.string.transition_group_cover))/*,
                            new Pair<>(tvPostDescription, itemView.getResources().getString(R.string.transition_group_title))*/);
                }
            });
        }

        public void setPost(GroupPostData post) {
            this.post = post;
            tvPostTitle.setText(post.getCaption());
            tvPostDateCreated.setText(DateUtils.getRelativeDateTimeString(
                    itemView.getContext(),
                    post.getCreatedTime().getTime(),
                    DateUtils.MINUTE_IN_MILLIS,
                    DateUtils.WEEK_IN_MILLIS,
                    DateUtils.FORMAT_ABBREV_ALL
            ));
            Utils.loadImage(post.getFullPicture(), ivPostCover);
            tvPostDescription.setText(post.getDescription());


        }
    }

    public interface OnPostItemClick {
        void onPostItemClick(@NonNull GroupPostData postData, Pair<View, String>... args);
    }

}
