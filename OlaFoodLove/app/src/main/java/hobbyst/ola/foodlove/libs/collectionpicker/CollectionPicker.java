/**
 * 
 * @author Arshad Parwez
 * 
 * @Company Freelancing
 *
 */

package hobbyst.ola.foodlove.libs.collectionpicker;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import hobbyst.ola.foodlove.R;

@SuppressLint("NewApi")
public class CollectionPicker extends LinearLayout {

    public static final int LAYOUT_WIDTH_OFFSET = 3;

    private ViewTreeObserver mViewTreeObserver;
    private LayoutInflater mInflater;

    private List<CollectionItem> mItems = new ArrayList<>();
    private LinearLayout mRow;
    private HashMap<String, Object> mCheckedItems;
    private CollectionOnItemClickListener mClickListener;
    private int mWidth;
    private int mItemMargin = 10;
    private int textPaddingLeft = 8;
    private int textPaddingRight = 8;
    private int textPaddingTop = 5;
    private int textPaddingBottom = 5;
    private int mAddIcon = R.drawable.collection_add;
    private int mCancelIcon = R.drawable.collection_selected;
    private int mLayoutBackgroundColorNormal = android.R.color.white;
    private int mLayoutBackgroundColorPressed = R.color.Accent400;
    private int mTextColorNormal = R.color.Accent400;
    private int mTextColorPressed = android.R.color.white;
    private int mRadius = 10;
    private boolean mInitialized;

    private Typeface proxima_nova_l, proxima_nova_sb;
    
    public CollectionPicker(Context context) {
        this(context, null);
    }

    public CollectionPicker(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CollectionPicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.proxima_nova_sb = Typeface.createFromAsset(context.getAssets(), "fonts/ProximaNova-Semibold.otf");
        this.proxima_nova_l = Typeface.createFromAsset(context.getAssets(), "fonts/ProximaNova-Light.otf");
		
        TypedArray typeArray = context
                .obtainStyledAttributes(attrs, R.styleable.CollectionPicker);
        this.mItemMargin = (int) typeArray
                .getDimension(R.styleable.CollectionPicker_cp_itemMargin,
                		CollectionUtils.dpToPx(this.getContext(), mItemMargin));
        this.textPaddingLeft = (int) typeArray
                .getDimension(R.styleable.CollectionPicker_cp_textPaddingLeft,
                		CollectionUtils.dpToPx(this.getContext(), textPaddingLeft));
        this.textPaddingRight = (int) typeArray
                .getDimension(R.styleable.CollectionPicker_cp_textPaddingRight,
                		CollectionUtils.dpToPx(this.getContext(),
                                textPaddingRight));
        this.textPaddingTop = (int) typeArray
                .getDimension(R.styleable.CollectionPicker_cp_textPaddingTop,
                        CollectionUtils.dpToPx(this.getContext(), textPaddingTop));
        this.textPaddingBottom = (int) typeArray
                .getDimension(R.styleable.CollectionPicker_cp_textPaddingBottom,
                		CollectionUtils.dpToPx(this.getContext(),
                                textPaddingBottom));
        this.mAddIcon = typeArray.getResourceId(R.styleable.CollectionPicker_cp_addIcon, mAddIcon);
        this.mCancelIcon = typeArray.getResourceId(R.styleable.CollectionPicker_cp_cancelIcon,
                mCancelIcon);
        this.mLayoutBackgroundColorNormal = typeArray.getColor(
                R.styleable.CollectionPicker_cp_itemBackgroundNormal,
                mLayoutBackgroundColorNormal);
        this.mLayoutBackgroundColorPressed = typeArray.getColor(
                R.styleable.CollectionPicker_cp_itemBackgroundPressed,
                mLayoutBackgroundColorPressed);
        this.mRadius = (int) typeArray
                .getDimension(R.styleable.CollectionPicker_cp_itemRadius, mRadius);
        this.mTextColorNormal = typeArray
                .getColor(R.styleable.CollectionPicker_cp_itemTextColorNormal, mTextColorNormal);
        this.mTextColorPressed = typeArray
                .getColor(R.styleable.CollectionPicker_cp_itemTextColorPressed, mTextColorPressed);
        typeArray.recycle();

        mCheckedItems = new HashMap<String, Object>();
        
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);

        mViewTreeObserver = getViewTreeObserver();
        mViewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!mInitialized) {
                    mInitialized = true;
                    drawItemView();
                }
            }
        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mWidth = w;
    }

    /**
     * Selected flags
     */
    public void setCheckedItems(HashMap<String, Object> checkedItems) {
        mCheckedItems = checkedItems;
    }

    public HashMap<String, Object> getCheckedItems() {
        return mCheckedItems;
    }

    public void drawItemView() {
        if (!mInitialized) {
            return;
        }

        clearUi();

        float totalPadding = getPaddingLeft() + getPaddingRight();
        int indexFrontView = 0;

        LayoutParams itemParams = getItemLayoutParams();

        for (int i = 0; i < mItems.size(); i++) {
            final CollectionItem item = mItems.get(i);
            if (mCheckedItems != null && mCheckedItems.containsKey(item.id)) {
                item.isSelected = true;
            }

            final int position = i;
            final View itemLayout = createItemView(item);
            itemLayout.setOnClickListener(new OnClickListener() {
                @SuppressWarnings("deprecation")
				@Override
                public void onClick(View v) {
                    animateView(v);
                    item.isSelected = !item.isSelected;
                    if (item.isSelected) {
                        mCheckedItems.put(item.id, item);
                    } else {
                        mCheckedItems.remove(item.id);
                    }

                    if (isJellyBeanAndAbove()) {
                        itemLayout.setBackground(getSelector(item));
                    } else {
                        itemLayout.setBackgroundDrawable(getSelector(item));
                    }
                    
                    TextView itemTextView = (TextView) itemLayout.findViewById(R.id.item_name);
                    itemTextView.setTypeface(getItemTextTypeface(item.isSelected));
                    itemTextView.setTextColor(getResources().getColor(getItemTextColor(item.isSelected)));
                    
                    ImageView iconView = (ImageView) itemLayout.findViewById(R.id.item_icon);
                    iconView.setImageResource(getItemIcon(item.isSelected));
                    if (mClickListener != null) {
                        mClickListener.onClick(item, position);
                    }
                }
            });

            TextView itemTextView = (TextView) itemLayout.findViewById(R.id.item_name);
            itemTextView.setText(item.text);
            itemTextView.setPadding(textPaddingLeft, textPaddingTop, textPaddingRight, textPaddingBottom);
            itemTextView.setTypeface(getItemTextTypeface(item.isSelected));
            itemTextView.setTextColor(getResources().getColor(getItemTextColor(item.isSelected)));

            float itemWidth = itemTextView.getPaint().measureText(item.text) + textPaddingLeft
                    + textPaddingRight;

            ImageView indicatorView = (ImageView) itemLayout.findViewById(R.id.item_icon);
            indicatorView.setImageResource(getItemIcon(item.isSelected));
            indicatorView.setPadding(0, textPaddingTop, textPaddingRight, textPaddingBottom);

            itemWidth += CollectionUtils.dpToPx(getContext(), 30) + textPaddingLeft
                    + textPaddingRight;

            if (mWidth <= totalPadding + itemWidth + CollectionUtils
                    .dpToPx(this.getContext(), LAYOUT_WIDTH_OFFSET)) {
                totalPadding = getPaddingLeft() + getPaddingRight();
                indexFrontView = i;
                addItemView(itemLayout, itemParams, true, i);
            } else {
                if (i != indexFrontView) {
                    itemParams.leftMargin = mItemMargin;
                    totalPadding += mItemMargin;
                }
                addItemView(itemLayout, itemParams, false, i);
            }
            totalPadding += itemWidth;
        }
    }

    @SuppressWarnings("deprecation")
	private View createItemView(CollectionItem item) {
        View view = mInflater.inflate(R.layout.collection_item_layout, this, false);
        if (isJellyBeanAndAbove()) {
            view.setBackground(getSelector(item));
        } else {
            view.setBackgroundDrawable(getSelector(item));
        }

        return view;
    }

    private LayoutParams getItemLayoutParams() {
        LayoutParams itemParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        itemParams.bottomMargin = mItemMargin / 2;
        itemParams.topMargin = mItemMargin / 2;

        return itemParams;
    }

    private int getItemIcon(Boolean isSelected) {
        return isSelected ? mCancelIcon : mAddIcon;
    }

    private int getItemTextColor(Boolean isSelected) {
        return isSelected ? mTextColorPressed : mTextColorNormal;
    }
    
    private Typeface getItemTextTypeface(Boolean isSelected) {
        return isSelected ? proxima_nova_sb : proxima_nova_l;
    }
    
    private void clearUi() {
        removeAllViews();
        mRow = null;
    }

    private void addItemView(View itemView, ViewGroup.LayoutParams chipParams, boolean newLine,
            int position) {
        if (mRow == null || newLine) {
            mRow = new LinearLayout(getContext());
            mRow.setGravity(Gravity.CENTER);
            mRow.setOrientation(HORIZONTAL);

            LayoutParams params = new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            mRow.setLayoutParams(params);

            addView(mRow);
        }

        mRow.addView(itemView, chipParams);
        animateItemView(itemView, position);
    }

    private StateListDrawable getSelector(CollectionItem item) {
        return item.isSelected ? getSelectorSelected() : getSelectorNormal();
    }

    private StateListDrawable getSelectorNormal() {
        StateListDrawable states = new StateListDrawable();

        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(mLayoutBackgroundColorPressed);
        gradientDrawable.setCornerRadius(mRadius);

        states.addState(new int[]{android.R.attr.state_pressed}, gradientDrawable);

        gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(mLayoutBackgroundColorNormal);
        gradientDrawable.setCornerRadius(mRadius);

        states.addState(new int[]{}, gradientDrawable);

        return states;
    }

    private StateListDrawable getSelectorSelected() {
        StateListDrawable states = new StateListDrawable();
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(mLayoutBackgroundColorNormal);
        gradientDrawable.setCornerRadius(mRadius);

        states.addState(new int[]{android.R.attr.state_pressed}, gradientDrawable);

        gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(mLayoutBackgroundColorPressed);
        gradientDrawable.setCornerRadius(mRadius);

        states.addState(new int[]{}, gradientDrawable);

        return states;
    }

    public List<CollectionItem> getItems() {
        return mItems;
    }

    public void setItems(List<CollectionItem> items) {
        mItems = items;
    }

    public void clearItems() {
        mItems.clear();
    }

    public void setOnItemClickListener(CollectionOnItemClickListener clickListener) {
        mClickListener = clickListener;
    }

    private boolean isJellyBeanAndAbove() {
        return android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN;
    }

    private void animateView(final View view) {
        view.setScaleY(1f);
        view.setScaleX(1f);

        view.animate()
                .scaleX(1.2f)
                .scaleY(1.2f)
                .setDuration(100)
                .setStartDelay(0)
                .setInterpolator(new DecelerateInterpolator())
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        reverseAnimation(view);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                })
                .start();
    }

    private void reverseAnimation(View view) {
        view.setScaleY(1.2f);
        view.setScaleX(1.2f);

        view.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(100)
                .setListener(null)
                .start();
    }

    private void animateItemView(View view, int position) {
        long animationDelay = 600;

        animationDelay += position * 30;

        view.setScaleY(0);
        view.setScaleX(0);
        view.animate()
                .scaleY(1)
                .scaleX(1)
                .setDuration(200)
                .setInterpolator(new DecelerateInterpolator())
                .setListener(null)
                .setStartDelay(animationDelay)
                .start();
    }
}