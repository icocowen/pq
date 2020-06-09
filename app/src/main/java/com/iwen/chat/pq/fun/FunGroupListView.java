package com.iwen.chat.pq.fun;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.iwen.chat.pq.R;
import com.qmuiteam.qmui.skin.QMUISkinHelper;
import com.qmuiteam.qmui.skin.QMUISkinValueBuilder;
import com.qmuiteam.qmui.util.QMUIResHelper;
import com.qmuiteam.qmui.util.QMUIViewHelper;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListSectionHeaderFooterView;

import java.util.ArrayList;
import java.util.List;

/*
* 自定义的GroupListView
* 实现能够动态的插入指定位置
* */
public class FunGroupListView extends LinearLayout {


    private SparseArray<FunGroupListView.Section> mSections;

    public FunGroupListView(Context context) {
        this(context, null);
    }

    public FunGroupListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FunGroupListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mSections = new SparseArray<>();
        setOrientation(LinearLayout.VERTICAL);
    }

    /**
     * 创建一个 Section。
     *
     * @return 返回新创建的 Section。
     */
    public static FunGroupListView.Section newSection(Context context) {
        return new FunGroupListView.Section(context);
    }


    public int getSectionCount() {
        return mSections.size();
    }

    public QMUICommonListItemView createItemView(@Nullable Drawable imageDrawable, CharSequence titleText, String detailText, int orientation, int accessoryType, int height) {
        QMUICommonListItemView itemView = new QMUICommonListItemView(getContext());
        itemView.setOrientation(orientation);
        itemView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, height));
        itemView.setImageDrawable(imageDrawable);
        itemView.setText(titleText);
        itemView.setDetailText(detailText);
        itemView.setAccessoryType(accessoryType);
        return itemView;
    }

    public QMUICommonListItemView createItemView(@Nullable Drawable imageDrawable, CharSequence titleText, String detailText, int orientation, int accessoryType) {
        int height;
        if (orientation == QMUICommonListItemView.VERTICAL) {
            height = QMUIResHelper.getAttrDimen(getContext(), R.attr.qmui_list_item_height_higher);
            return createItemView(imageDrawable, titleText, detailText, orientation, accessoryType, height);
        } else {
            height = QMUIResHelper.getAttrDimen(getContext(), R.attr.qmui_list_item_height);
            return createItemView(imageDrawable, titleText, detailText, orientation, accessoryType, height);
        }
    }

    public QMUICommonListItemView createItemView(CharSequence titleText) {
        return createItemView(null, titleText, null, QMUICommonListItemView.HORIZONTAL, QMUICommonListItemView.ACCESSORY_TYPE_NONE);
    }

    public QMUICommonListItemView createItemView(int orientation) {
        return createItemView(null, null, null, orientation, QMUICommonListItemView.ACCESSORY_TYPE_NONE);
    }

    /**
     * private, use {@link FunGroupListView.Section#addTo(FunGroupListView)}
     * <p>这里只是把section记录到数组里面而已</p>
     */
    private void addSection(FunGroupListView.Section section) {
        mSections.append(mSections.size(), section);
    }

    /**
     * private，use {@link FunGroupListView.Section#removeFrom(FunGroupListView)}
     * <p>这里只是把section从记录的数组里移除而已</p>
     */
    private void removeSection(FunGroupListView.Section section) {
        for (int i = 0; i < mSections.size(); i++) {
            FunGroupListView.Section each = mSections.valueAt(i);
            if (each == section) {
                mSections.remove(i);
            }
        }
    }

    public FunGroupListView.Section getSection(int index) {
        return mSections.get(index);
    }


    /**
     * Section 是组成 {@link FunGroupListView} 的部分。
     * <ul>
     * <li>每个 Section 可以有多个 item, 通过 {@link #(QMUICommonListItemView, OnClickListener)} 添加。</li>
     * <li>Section 还可以有自己的一个顶部 title 和一个底部 description, 通过 {@link #setTitle(CharSequence)} 和 {@link #setDescription(CharSequence)} 设置。</li>
     * </ul>
     */
    public static class Section {
        private Context mContext;
        private QMUIGroupListSectionHeaderFooterView mTitleView;
        private QMUIGroupListSectionHeaderFooterView mDescriptionView;
        private List<QMUICommonListItemView> mItemViews;
        private boolean mUseDefaultTitleIfNone;
        private boolean mUseTitleViewForSectionSpace = true;
        private int mSeparatorColorAttr = R.attr.qmui_skin_support_common_list_separator_color;
        private boolean mHandleSeparatorCustom = false;
        private boolean mShowSeparator = true;
        private boolean mOnlyShowStartEndSeparator = false;
        private boolean mOnlyShowMiddleSeparator = false;
        private int mMiddleSeparatorInsetLeft = 0;
        private int mMiddleSeparatorInsetRight = 0;
        private int mBgAttr = R.attr.qmui_skin_support_s_common_list_bg;

        private int mLeftIconWidth = ViewGroup.LayoutParams.WRAP_CONTENT;
        private int mLeftIconHeight = ViewGroup.LayoutParams.WRAP_CONTENT;

        public Section(Context context) {
            mContext = context;
            mItemViews = new ArrayList<>();
        }

        /**
         * 对 Section 添加一个 {@link QMUICommonListItemView}
         *
         * @param itemView        要添加的 ItemView
         * @param onClickListener ItemView 的点击事件
         * @return Section 本身,支持链式调用
         */
        public FunGroupListView.Section addItemViewToHead(QMUICommonListItemView itemView, OnClickListener onClickListener) {
            return addItemView(itemView, onClickListener, null, 0);
        }

        public FunGroupListView.Section addItemViewToTail(QMUICommonListItemView itemView, OnClickListener onClickListener) {

            return addItemView(itemView, onClickListener, null,mItemViews.size());
        }

        public FunGroupListView.Section addItemViewForIndx(QMUICommonListItemView itemView, OnClickListener onClickListener, int idx) {
            return addItemView(itemView, onClickListener, null,idx);
        }

        /**
         * 对 Section 添加一个 {@link QMUICommonListItemView}
         *
         * @param itemView            要添加的 ItemView
         * @param onClickListener     ItemView 的点击事件
         * @param onLongClickListener ItemView 的长按事件
         * @return Section 本身, 支持链式调用
         */
        public FunGroupListView.Section addItemView(final QMUICommonListItemView itemView, OnClickListener onClickListener, OnLongClickListener onLongClickListener, int idx) {
            if (onClickListener != null) {
                itemView.setOnClickListener(onClickListener);
            }

            if (onLongClickListener != null) {
                itemView.setOnLongClickListener(onLongClickListener);
            }



//            mItemViews.indexOfValue(idx, itemView)
//            mItemViews.append(idx, itemView);
//            mItemViews.put(idx, itemView);
            //idx 0 .... size() - 1
            for (int i = 0; i < mItemViews.size(); i++) {
                if (itemView == mItemViews.get(i)) {
                    mItemViews.remove(i);
                    idx--;
                }
            }
            mItemViews.add(idx, itemView);
//            mItemViews.setValueAt(idx, itemView);

            return this;
        }

        /**
         * 设置 Section 的 title
         *
         * @return Section 本身, 支持链式调用
         */
        public FunGroupListView.Section setTitle(CharSequence title) {
            mTitleView = createSectionHeader(title);
            return this;
        }

        /**
         * 设置 Section 的 description
         *
         * @return Section 本身, 支持链式调用
         */
        public FunGroupListView.Section setDescription(CharSequence description) {
            mDescriptionView = createSectionFooter(description);
            return this;
        }

        public FunGroupListView.Section setUseDefaultTitleIfNone(boolean useDefaultTitleIfNone) {
            mUseDefaultTitleIfNone = useDefaultTitleIfNone;
            return this;
        }

        public FunGroupListView.Section setUseTitleViewForSectionSpace(boolean useTitleViewForSectionSpace) {
            mUseTitleViewForSectionSpace = useTitleViewForSectionSpace;
            return this;
        }

        public FunGroupListView.Section setLeftIconSize(int width, int height) {
            mLeftIconHeight = height;
            mLeftIconWidth = width;
            return this;
        }

        public FunGroupListView.Section setSeparatorColorAttr(int attr) {
            mSeparatorColorAttr = attr;
            return this;
        }

        public FunGroupListView.Section setHandleSeparatorCustom(boolean handleSeparatorCustom) {
            mHandleSeparatorCustom = handleSeparatorCustom;
            return this;
        }

        public FunGroupListView.Section setShowSeparator(boolean showSeparator) {
            mShowSeparator = showSeparator;
            return this;
        }

        public FunGroupListView.Section setOnlyShowStartEndSeparator(boolean onlyShowStartEndSeparator) {
            mOnlyShowStartEndSeparator = onlyShowStartEndSeparator;
            return this;
        }

        public FunGroupListView.Section setOnlyShowMiddleSeparator(boolean onlyShowMiddleSeparator) {
            mOnlyShowMiddleSeparator = onlyShowMiddleSeparator;
            return this;
        }

        public FunGroupListView.Section setMiddleSeparatorInset(int insetLeft, int insetRight) {
            mMiddleSeparatorInsetLeft = insetLeft;
            mMiddleSeparatorInsetRight = insetRight;
            return this;
        }

        public FunGroupListView.Section setBgAttr(int bgAttr) {
            mBgAttr = bgAttr;
            return this;
        }


        /**
         * 将 Section 添加到 {@link FunGroupListView} 上
         */
        public void addTo(FunGroupListView groupListView) {
            if (mTitleView == null) {
                if (mUseDefaultTitleIfNone) {
                    setTitle("Section " + groupListView.getSectionCount());
                } else if (mUseTitleViewForSectionSpace) {
                    setTitle("");
                }
            }
            if (mTitleView != null) {
                groupListView.addView(mTitleView);
            }


            final int itemViewCount = mItemViews.size();
            QMUICommonListItemView.LayoutParamConfig leftIconLpConfig = new QMUICommonListItemView.LayoutParamConfig() {
                @Override
                public ConstraintLayout.LayoutParams onConfig(ConstraintLayout.LayoutParams lp) {
                    lp.width = mLeftIconWidth;
                    lp.height = mLeftIconHeight;
                    return lp;
                }
            };
            QMUISkinValueBuilder builder = QMUISkinValueBuilder.acquire();
            String skin = builder.background(mBgAttr)
                    .topSeparator(mSeparatorColorAttr)
                    .bottomSeparator(mSeparatorColorAttr)
                    .build();
            QMUISkinValueBuilder.release(builder);
            int separatorColor = QMUIResHelper.getAttrColor(groupListView.getContext(), mSeparatorColorAttr);
            for (int i = 0; i < itemViewCount; i++) {
                QMUICommonListItemView itemView = mItemViews.get(i);
                Drawable bg = QMUISkinHelper.getSkinDrawable(groupListView, mBgAttr);
                QMUIViewHelper.setBackgroundKeepingPadding(itemView, bg == null ? null : bg.mutate());
                QMUISkinHelper.setSkinValue(itemView, skin);
                if (!mHandleSeparatorCustom && mShowSeparator) {
                    if (itemViewCount == 1) {
                        itemView.updateTopDivider(0, 0, 1, separatorColor);
                        itemView.updateBottomDivider(0, 0, 1, separatorColor);
                    } else if (i == 0) {
                        if(!mOnlyShowMiddleSeparator){
                            itemView.updateTopDivider(0, 0, 1, separatorColor);
                        }
                        if (!mOnlyShowStartEndSeparator) {
                            itemView.updateBottomDivider(
                                    mMiddleSeparatorInsetLeft, mMiddleSeparatorInsetRight, 1, separatorColor);
                        }
                    } else if (i == itemViewCount - 1) {
                        if(!mOnlyShowMiddleSeparator){
                            itemView.updateBottomDivider(0, 0, 1, separatorColor);
                        }
                    } else if (!mOnlyShowStartEndSeparator) {
                        itemView.updateBottomDivider(mMiddleSeparatorInsetLeft, mMiddleSeparatorInsetRight, 1, separatorColor);
                    }
                }
                itemView.updateImageViewLp(leftIconLpConfig);
                groupListView.addView(itemView);
            }

            if (mDescriptionView != null) {
                groupListView.addView(mDescriptionView);
            }
            groupListView.addSection(this);
        }

        public void removeFrom(FunGroupListView parent) {
            if (mTitleView != null && mTitleView.getParent() == parent) {
                parent.removeView(mTitleView);
            }
            for (int i = 0; i < mItemViews.size(); i++) {
                QMUICommonListItemView itemView = mItemViews.get(i);
                parent.removeView(itemView);
            }
            if (mDescriptionView != null && mDescriptionView.getParent() == parent) {
                parent.removeView(mDescriptionView);
            }
            parent.removeSection(this);
        }

        /**
         * 创建 Section Header，每个 Section 都会被创建一个 Header，有 title 时会显示 title，没有 title 时会利用 header 的上下 padding 充当 Section 分隔条
         */
        public QMUIGroupListSectionHeaderFooterView createSectionHeader(CharSequence titleText) {
            return new QMUIGroupListSectionHeaderFooterView(mContext, titleText);
        }

        /**
         * Section 的 Footer，形式与 Header 相似，都是显示一段文字
         */
        public QMUIGroupListSectionHeaderFooterView createSectionFooter(CharSequence text) {
            return new QMUIGroupListSectionHeaderFooterView(mContext, text, true);
        }
    }

}
