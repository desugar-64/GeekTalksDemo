package com.android.sergeyfitis.geektalksdemo.ui.adapters;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.util.Pair;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.sergeyfitis.geektalksdemo.R;
import com.android.sergeyfitis.geektalksdemo.helpers.RxUtils;
import com.android.sergeyfitis.geektalksdemo.helpers.Utils;
import com.android.sergeyfitis.geektalksdemo.models.Group;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Serhii Yaremych on 31.10.2015.
 */
public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.GroupsViewHolder> {
    private List<Group> groups;

    @Nullable
    private OnItemClickListener onItemClickListener;

    public GroupsAdapter(List<Group> groups) {
        this.groups = groups;
    }

    @Override
    public GroupsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GroupsViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.group_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(GroupsViewHolder holder, int position) {
        holder.setGroup(groups.get(position));
    }

    @Override
    public int getItemCount() {
        return groups == null ? 0 : groups.size();
    }

    public void setOnItemClickListener(@Nullable OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    class GroupsViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_group_cover)
        ImageView ivGroupCover;
        @Bind(R.id.tv_group_item_title)
        TextView tvGroupItemTitle;
        @Bind(R.id.tv_group_item_likes)
        TextView tvGroupItemLikes;
        @Bind(R.id.ll_group_item_bg)
        LinearLayout llGroupItemBg;

        private Group group;
        private int[] uiColors;

        public GroupsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClicked(group, uiColors,
                            new Pair<>(ivGroupCover, itemView.getResources().getString(R.string.transition_group_cover)));
                }
            });
        }


        public void setGroup(Group group) {
            this.group = group;
            tvGroupItemTitle.setText(group.getName());
            tvGroupItemLikes.setText(String.valueOf(group.getLikes()));
            if (!TextUtils.isEmpty(group.getPicture().getImageUrl())) {
                RxUtils.generatePalette(group.getPicture().getImageUrl(), ivGroupCover.getContext(), false)
                        .subscribe(this::styleViewAccordingToSwatch);
            }
        }

        private void styleViewAccordingToSwatch(Pair<Palette, Bitmap> paletteBitmapPair) {
            Palette.Swatch swatch = Utils.findSwatchByMostUsedColor(paletteBitmapPair.first
                    .getSwatches());
            uiColors = new int[]{swatch.getRgb(), swatch.getTitleTextColor(), swatch.getBodyTextColor()};
            llGroupItemBg.setBackgroundColor(swatch.getRgb());
            tvGroupItemTitle.setTextColor(swatch.getBodyTextColor());
            tvGroupItemLikes.setTextColor(swatch.getTitleTextColor());
            ivGroupCover.setImageBitmap(paletteBitmapPair.second);
            Drawable[] compoundDrawables = tvGroupItemLikes.getCompoundDrawables();
            Drawable drawable = compoundDrawables[0];
            DrawableCompat.setTint(drawable, swatch.getTitleTextColor());
        }
    }

    public interface OnItemClickListener {
        void onItemClicked(@NonNull Group group, int[] uiColors, @NonNull Pair<View, String>... args);
    }

}
