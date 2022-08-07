package omari.hamza.storyview;

import static omari.hamza.storyview.utils.Utils.getDurationBetweenDates;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.civitasv.ioslike.dialog.DialogBottom;
import com.civitasv.ioslike.dialog.DialogNormal;
import com.civitasv.ioslike.model.DialogText;
import com.civitasv.ioslike.model.DialogTextStyle;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import omari.hamza.storyview.callback.OnClickDeleteStoryListener;
import omari.hamza.storyview.callback.OnStoryChangedCallback;
import omari.hamza.storyview.callback.StoryCallbacks;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.callback.TouchCallbacks;
import omari.hamza.storyview.model.MyStory;
import omari.hamza.storyview.progress.StoriesProgressView;
import omari.hamza.storyview.utils.PullDismissLayout;
import omari.hamza.storyview.utils.StoryViewHeaderInfo;
import omari.hamza.storyview.utils.ViewPagerAdapter;

public class StoryView extends DialogFragment implements StoriesProgressView.StoriesListener,
        StoryCallbacks,
        PullDismissLayout.Listener,
        TouchCallbacks {

    private static final String TAG = StoryView.class.getSimpleName();

    private ArrayList<MyStory> storiesList = new ArrayList<>();

    private final static String IMAGES_KEY = "IMAGES";

    private long duration = 2000; //Default Duration

    private static final String DURATION_KEY = "DURATION";

    private static final String HEADER_INFO_KEY = "HEADER_INFO";

    private static final String STARTING_INDEX_TAG = "STARTING_INDEX";

    private static final String IS_RTL_TAG = "IS_RTL";

    private StoriesProgressView storiesProgressView;

    private ViewPager mViewPager;

    private int counter = 0;

    private int startingIndex = 0;

    private boolean isHeadlessLogoMode = false;

    //Heading
    private TextView titleTextView, subtitleTextView;
    private CardView titleCardView;
    private ImageView titleIconImageView;
    private ImageButton closeImageButton;
    private MaterialButton moreIV;

    //Touch Events
    private boolean isDownClick = false;
    private long elapsedTime = 0;
    private Thread timerThread;
    private boolean isPaused = false;
    private int width, height;
    private float xValue = 0, yValue = 0;

    private ViewPagerAdapter viewPagerAdapter;

    private StoryClickListeners storyClickListeners;
    private OnStoryChangedCallback onStoryChangedCallback;

    private boolean isRtl;

    private OnClickDeleteStoryListener onClickDeleteStoryListener;

    private DialogBottom moreMenuDialogBottom;
    private List<DialogText> dialogTextItemList;
    private boolean isShowDialogBottom = false;
    private boolean isAddDeleteItemToMoreMenu = false;
    private boolean isViewAudienceToMoreMenu = false;
    private boolean isAddedDialogTextItemList;
    private boolean isUserDismissMoreMenu;
//    private boolean isShowMoreMenu;

    private StoryView() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return inflater.inflate(R.layout.dialog_stories, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        width = displaymetrics.widthPixels;
        height = displaymetrics.heightPixels;
        isAddedDialogTextItemList = false;
        isUserDismissMoreMenu = false;
        // Get field from view
        readArguments();
        setupViews(view);
        setupStories();

    }

    private void setupStories() {
        storiesProgressView.setStoriesCount(storiesList.size());
        storiesProgressView.setStoryDuration(duration);
        updateHeading();
        viewPagerAdapter = new ViewPagerAdapter(storiesList, getContext(), this);
        mViewPager.setAdapter(viewPagerAdapter);
    }

    private void readArguments() {
        assert getArguments() != null;
//        storiesList = new ArrayList<>((ArrayList<MyStory>) getArguments().getSerializable(IMAGES_KEY));
        storiesList = getArguments().getParcelableArrayList(IMAGES_KEY);
        duration = getArguments().getLong(DURATION_KEY, 2000);
        startingIndex = getArguments().getInt(STARTING_INDEX_TAG, 0);
        isRtl = getArguments().getBoolean(IS_RTL_TAG, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupViews(View view) {
        PullDismissLayout rootLayout = view.findViewById(R.id.pull_dismiss_layout);
        rootLayout.setListener(this);
        rootLayout.setmTouchCallbacks(this);
        storiesProgressView = view.findViewById(R.id.storiesProgressView);
        mViewPager = view.findViewById(R.id.storiesViewPager);
        titleTextView = view.findViewById(R.id.title_textView);
        subtitleTextView = view.findViewById(R.id.subtitle_textView);
        titleIconImageView = view.findViewById(R.id.title_imageView);
        titleCardView = view.findViewById(R.id.titleCardView);
        closeImageButton = view.findViewById(R.id.imageButton);
        moreIV = view.findViewById(R.id.moreIV);
        storiesProgressView.setStoriesListener(this);
        mViewPager.setOnTouchListener((v, event) -> true);
        closeImageButton.setOnClickListener(v -> dismissAllowingStateLoss());
        if (storyClickListeners != null) {
            titleCardView.setOnClickListener(v -> storyClickListeners.onTitleIconClickListener(counter));
        }

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (onStoryChangedCallback != null) {
                    onStoryChangedCallback.storyChanged(position);
                }
                viewPagerAdapter.setCurrentPosition(position);
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        if (isRtl) {
            storiesProgressView.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            storiesProgressView.setRotation(180);
        }

        if (isShowDialogBottom) {
            moreIV.setVisibility(View.VISIBLE);

            moreIV.setOnClickListener(view1 -> {
                setupMoreMenu(view1);
            });

            moreIV.post(() -> {
                rootLayout.setDisableOnInterceptTouchEventX(moreIV.getX());
                rootLayout.setDisableOnInterceptTouchEventY(moreIV.getY());
            });
        } else {
            moreIV.setVisibility(View.GONE);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes(params);
    }

    @Override
    public void onNext() {
        mViewPager.setCurrentItem(++counter, false);
        updateHeading();
    }

    @Override
    public void onPrev() {
        if (counter <= 0) return;
        mViewPager.setCurrentItem(--counter, false);
        updateHeading();
    }

    @Override
    public void onComplete() {
        dismissAllowingStateLoss();
    }

    @Override
    public void startStories() {
        counter = startingIndex;
        storiesProgressView.startStories(startingIndex);
        mViewPager.setCurrentItem(startingIndex, false);
        updateHeading();
    }

    @Override
    public void pauseStories() {
        storiesProgressView.pause();
    }

    private void previousStory() {
        if (counter - 1 < 0) return;
        mViewPager.setCurrentItem(--counter, false);
        storiesProgressView.setStoriesCount(storiesList.size());
        storiesProgressView.setStoryDuration(duration);
        storiesProgressView.startStories(counter);
        updateHeading();
    }

    @Override
    public void nextStory() {
        if (counter + 1 >= storiesList.size()) {
            dismissAllowingStateLoss();
            return;
        }
        mViewPager.setCurrentItem(++counter, false);
        storiesProgressView.startStories(counter);
        updateHeading();
    }

    @Override
    public void onDescriptionClickListener(int position) {
        if (storyClickListeners == null) return;
        storyClickListeners.onDescriptionClickListener(position);
    }

    @Override
    public void onDestroy() {
        timerThread = null;
        storiesList = null;
        storiesProgressView.destroy();
        super.onDestroy();
    }

    private void setupMoreMenu(View view) {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        int currentItem = mViewPager.getCurrentItem();


//        isShowMoreMenu = true;
        if (moreMenuDialogBottom == null) {
            moreMenuDialogBottom = new DialogBottom(activity);
            moreMenuDialogBottom/*.setTitle(getString(R.string.options)
                            , v2 -> {
                            })*/
                    .setCancel(getString(R.string.cancel)
                            , v2 -> {
                                activity.runOnUiThread(() -> {
//                                    touchUp();
                                });
                            }
                            , true
                    )
                    .setOnDismissListener(dialog -> {
                        activity.runOnUiThread(() -> {
                            if (!isUserDismissMoreMenu) {
                                touchUp();
                            }
                            isUserDismissMoreMenu = false;
                        });
                    })
                    .setCancelClickListener(v2 -> {
                        activity.runOnUiThread(() -> {
//                            touchUp();
                        });
                    });

        }

        if (moreMenuDialogBottom != null && !isAddedDialogTextItemList) {
            isAddedDialogTextItemList = true;
            if (dialogTextItemList != null && !dialogTextItemList.isEmpty()) {
                moreMenuDialogBottom.setBottomList(dialogTextItemList);
            }
            if (isAddDeleteItemToMoreMenu) {
                moreMenuDialogBottom.addBottomItem(getString(R.string.delete)
                        , view1 -> {
                            isUserDismissMoreMenu = true;
                            activity.runOnUiThread(() -> {
                                new DialogNormal(activity)
                                        .setTitle(R.string.delete_this_story)
                                        .setContent(activity.getString(R.string.are_you_sure_you_want_to_delete_this_story))
                                        .setConfirm(getString(R.string.delete)
                                                , view2 -> {
                                                    isUserDismissMoreMenu = true;
                                                    if (onClickDeleteStoryListener != null) {
                                                        onClickDeleteStoryListener.OnClickDeleteStory(currentItem);
                                                    }
                                                    moreMenuDialogBottom.dismiss();
                                                    onComplete();
                                                }
                                                , true
                                                , (new DialogTextStyle.Builder(activity).color(R.color.ios_like_red)).build()
                                        )
                                        .setCancel(getString(R.string.cancel)
                                                , view2 -> {
//                                                    touchUp();
                                                }
                                                , true
                                                , (new DialogTextStyle.Builder(activity).color(R.color.black)).build()
                                        )
                                        .setCancelClickListener(view2 -> {
//                                            touchUp();
                                                }
                                        )
                                        .setOnDismissListener(dialog -> {
                                                    if (!isUserDismissMoreMenu) {
                                                        touchUp();
                                                    }
                                                    isUserDismissMoreMenu = false;
                                                }
                                        )
                                        .setCanceledOnTouchOutside(true)
                                        .show();
                                moreMenuDialogBottom.dismiss();
                            });
                        }
                        , (new DialogTextStyle.Builder(activity).color(R.color.ios_like_red)).build()
                );
            }

            if (isViewAudienceToMoreMenu) {
                moreMenuDialogBottom.addBottomItem(getString(R.string.view_audience)
                        , view1 -> {
                            isUserDismissMoreMenu = true;
                            activity.runOnUiThread(() -> {
                                moreMenuDialogBottom.dismiss();
                            });
                        }
                );
            }
        }
        touchDown(view.getX(), view.getY());
        moreMenuDialogBottom.show();

//        isShowMoreMenu = false;
    }

    private void deleteItemFromAdapter(int currentItem) {
        int storiesListSize = storiesList.size();
        if (currentItem > -1 && currentItem < storiesListSize) {
            MyStory myStory = storiesList.remove(currentItem);
            if (myStory != null) {
                if (currentItem + 1 < storiesListSize) {
                    setupStories();
                    /*storiesProgressView.setStoriesCount(storiesListSize - 1);
                    storiesProgressView.setCurrentItem(currentItem);
                    viewPagerAdapter.notifyDataSetChanged();
                    mViewPager.setCurrentItem(currentItem);*/
                    touchUp();
                } else {
                    onComplete();
                }
            }
        }
    }

    private void updateHeading() {

        Object object = getArguments().getSerializable(HEADER_INFO_KEY);

        StoryViewHeaderInfo storyHeaderInfo = null;

        if (object instanceof StoryViewHeaderInfo) {
            storyHeaderInfo = (StoryViewHeaderInfo) object;
        } else if (object instanceof ArrayList) {
            storyHeaderInfo = ((ArrayList<StoryViewHeaderInfo>) object).get(counter);
        }

        if (storyHeaderInfo == null) return;

        if (storyHeaderInfo.getTitleIconUrl() != null && !storyHeaderInfo.getTitleIconUrl().trim().isEmpty()) {
            titleCardView.setVisibility(View.VISIBLE);
            if (getContext() == null) return;
            Glide.with(getContext())
                    .load(storyHeaderInfo.getTitleIconUrl())
                    .into(titleIconImageView);
        } else {
            titleCardView.setVisibility(View.GONE);
            isHeadlessLogoMode = true;
        }

        if (storyHeaderInfo.getTitle() != null && !storyHeaderInfo.getTitle().trim().isEmpty()) {
            titleTextView.setVisibility(View.VISIBLE);
            titleTextView.setText(storyHeaderInfo.getTitle());
        } else {
            titleTextView.setVisibility(View.GONE);
        }

        if (storyHeaderInfo.getSubtitle() != null && !storyHeaderInfo.getSubtitle().trim().isEmpty()) {
            subtitleTextView.setVisibility(View.VISIBLE);
            subtitleTextView.setText(storyHeaderInfo.getSubtitle());

            if (storiesList.get(counter).getDate() != null) {
                titleTextView.setText(titleTextView.getText()
                        + " "
                        + getDurationBetweenDates(storiesList.get(counter).getDate(), Calendar.getInstance().getTime())
                );
            }
        } else {
            if (storiesList.get(counter).getDate() != null) {
                subtitleTextView.setVisibility(View.VISIBLE);
                subtitleTextView.setText(getDurationBetweenDates(storiesList.get(counter).getDate(), Calendar.getInstance().getTime()));

            } else {
                subtitleTextView.setVisibility(View.GONE);
            }
        }

        /*if (storyHeaderInfo.getSubtitle() != null) {
            subtitleTextView.setVisibility(View.VISIBLE);
            subtitleTextView.setText(storyHeaderInfo.getSubtitle());
        } else {
            subtitleTextView.setVisibility(View.GONE);
        }

        if (storiesList.get(counter).getDate() != null) {
            titleTextView.setText(titleTextView.getText()
                    + " "
                    + getDurationBetweenDates(storiesList.get(counter).getDate(), Calendar.getInstance().getTime())
            );
        }*/
    }

    private void setHeadingVisibility(int visibility) {
        if (isHeadlessLogoMode && visibility == View.VISIBLE) {
            titleTextView.setVisibility(View.GONE);
            titleCardView.setVisibility(View.GONE);
            subtitleTextView.setVisibility(View.GONE);
        } else {
            titleTextView.setVisibility(visibility);
            titleCardView.setVisibility(visibility);
            subtitleTextView.setVisibility(visibility);
        }

        closeImageButton.setVisibility(visibility);
        if (isShowDialogBottom) {
            moreIV.setVisibility(visibility);
        } else {
            moreIV.setVisibility(View.GONE);
        }
        storiesProgressView.setVisibility(visibility);
    }


    private void createTimer() {
        timerThread = new Thread(() -> {
            while (isDownClick) {
                try {
                    SystemClock.sleep(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                elapsedTime += 100;
                if (elapsedTime >= 500 && !isPaused) {
                    isPaused = true;
                    if (getActivity() == null) return;
                    getActivity().runOnUiThread(() -> {
                        storiesProgressView.pause();
                        setHeadingVisibility(View.GONE);
                    });
                }
            }
            isPaused = false;
            if (getActivity() == null) return;
            if (elapsedTime < 500) return;
            getActivity().runOnUiThread(() -> {
                setHeadingVisibility(View.VISIBLE);
                storiesProgressView.resume();
            });
        });
    }

    private void runTimer() {
        isDownClick = true;
        createTimer();
        timerThread.start();
    }

    private void stopTimer() {
        isDownClick = false;
    }

    @Override
    public void onDismissed() {
        dismissAllowingStateLoss();
    }

    @Override
    public boolean onShouldInterceptTouchEvent() {
        return false;
    }

    @Override
    public void touchPull() {
//        if (isShowMoreMenu) {
//            return;
//        }
        elapsedTime = 0;
        stopTimer();
        storiesProgressView.pause();
    }

    public void touchDown() {
        float xValue = 0f;
        float yValue = 0f;
        if (moreIV != null) {
            xValue = moreIV.getX();
            yValue = moreIV.getY();
        }

        touchDown(xValue, yValue);
    }

    public void touchDown(float xValue, float yValue) {
        this.xValue = xValue;
        this.yValue = yValue;
        if (!isDownClick) {
            runTimer();
        }
    }

    @Override
    public void touchUp() {
//        if (isShowMoreMenu) {
//            return;
//        }
        if (isDownClick && elapsedTime < 500) {
            stopTimer();
            if (((int) (height - yValue) <= 0.8 * height)) {
                if ((!TextUtils.isEmpty(storiesList.get(counter).getDescription())
                        && ((int) (height - yValue) >= 0.2 * height)
                        || TextUtils.isEmpty(storiesList.get(counter).getDescription()))) {
                    if ((int) xValue <= (width / 2)) {
                        //Left
                        if (isRtl) {
                            nextStory();
                        } else {
                            previousStory();
                        }
                    } else {
                        //Right
                        if (isRtl) {
                            previousStory();
                        } else {
                            nextStory();
                        }
                    }
                }
            }
        } else {
            stopTimer();
            setHeadingVisibility(View.VISIBLE);
            storiesProgressView.resume();
        }
        elapsedTime = 0;
    }

    public void setStoryClickListeners(StoryClickListeners storyClickListeners) {
        this.storyClickListeners = storyClickListeners;
    }

    public void setOnStoryChangedCallback(OnStoryChangedCallback onStoryChangedCallback) {
        this.onStoryChangedCallback = onStoryChangedCallback;
    }

    public void setOnClickDeleteStoryListener(OnClickDeleteStoryListener onClickDeleteStoryListener) {
        this.onClickDeleteStoryListener = onClickDeleteStoryListener;
    }

    public void setMoreMenuDialogBottom(DialogBottom moreMenuDialogBottom) {
        this.moreMenuDialogBottom = moreMenuDialogBottom;
    }

    public void setDialogTextItemList(List<DialogText> dialogTextItemList) {
        this.dialogTextItemList = dialogTextItemList;
    }

    public void setAddDeleteItemToMoreMenu(boolean addDeleteItemToMoreMenu) {
        isAddDeleteItemToMoreMenu = addDeleteItemToMoreMenu;
    }

    public void setViewAudienceToMoreMenu(boolean viewAudienceToMoreMenu) {
        isViewAudienceToMoreMenu = viewAudienceToMoreMenu;
    }

    public void setShowDialogBottom(boolean showDialogBottom) {
        isShowDialogBottom = showDialogBottom;
    }

    public static class Builder {

        private StoryView storyView;
        private final FragmentManager fragmentManager;
        private final Bundle bundle;
        private final StoryViewHeaderInfo storyViewHeaderInfo;
        private ArrayList<StoryViewHeaderInfo> headingInfoList;
        private StoryClickListeners storyClickListeners;
        private OnStoryChangedCallback onStoryChangedCallback;
        private OnClickDeleteStoryListener onClickDeleteStoryListener;
        private DialogBottom moreMenuDialogBottom;
        private List<DialogText> dialogTextItemList;
        private boolean isShowDialogBottom;
        private boolean isAddDeleteItemToMoreMenu;
        private boolean isViewAudienceToMoreMenu;

        public Builder(FragmentManager fragmentManager) {
            this.fragmentManager = fragmentManager;
            this.bundle = new Bundle();
            this.storyViewHeaderInfo = new StoryViewHeaderInfo();
        }

        public Builder setStoriesList(ArrayList<MyStory> storiesList) {
//            bundle.putSerializable(IMAGES_KEY, storiesList);
            bundle.putParcelableArrayList(IMAGES_KEY, storiesList);
            return this;
        }

        public Builder setTitleText(String title) {
            storyViewHeaderInfo.setTitle(title);
            return this;
        }

        public Builder setSubtitleText(String subtitle) {
            storyViewHeaderInfo.setSubtitle(subtitle);
            return this;
        }

        public Builder setTitleLogoUrl(String url) {
            storyViewHeaderInfo.setTitleIconUrl(url);
            return this;
        }

        public Builder setStoryDuration(long duration) {
            bundle.putLong(DURATION_KEY, duration);
            return this;
        }

        public Builder setStartingIndex(int index) {
            bundle.putInt(STARTING_INDEX_TAG, index);
            return this;
        }

        public Builder build() {
            if (storyView != null) {
                Log.e(TAG, "The StoryView has already been built!");
                return this;
            }
            storyView = new StoryView();
            bundle.putSerializable(HEADER_INFO_KEY, headingInfoList != null ? headingInfoList : storyViewHeaderInfo);
            storyView.setArguments(bundle);
            if (storyClickListeners != null) {
                storyView.setStoryClickListeners(storyClickListeners);
            }
            if (onStoryChangedCallback != null) {
                storyView.setOnStoryChangedCallback(onStoryChangedCallback);
            }
            if (onClickDeleteStoryListener != null) {
                storyView.setOnClickDeleteStoryListener(onClickDeleteStoryListener);
            }
            if (moreMenuDialogBottom != null) {
                storyView.setMoreMenuDialogBottom(moreMenuDialogBottom);
            }
            if (dialogTextItemList != null) {
                storyView.setDialogTextItemList(dialogTextItemList);
            }
            storyView.setAddDeleteItemToMoreMenu(isAddDeleteItemToMoreMenu);
            storyView.setViewAudienceToMoreMenu(isViewAudienceToMoreMenu);
            storyView.setShowDialogBottom(isShowDialogBottom);
            return this;
        }

        public Builder setOnStoryChangedCallback(OnStoryChangedCallback onStoryChangedCallback) {
            this.onStoryChangedCallback = onStoryChangedCallback;
            return this;
        }

        public Builder setRtl(boolean isRtl) {
            this.bundle.putBoolean(IS_RTL_TAG, isRtl);
            return this;
        }

        public Builder setHeadingInfoList(ArrayList<StoryViewHeaderInfo> headingInfoList) {
            this.headingInfoList = headingInfoList;
            return this;
        }

        public Builder setStoryClickListeners(StoryClickListeners storyClickListeners) {
            this.storyClickListeners = storyClickListeners;
            return this;
        }

        public Builder setOnClickDeleteStoryListener(OnClickDeleteStoryListener onClickDeleteStoryListener) {
            this.onClickDeleteStoryListener = onClickDeleteStoryListener;
            return this;
        }

        public Builder setMoreMenuDialogBottom(DialogBottom moreMenuDialogBottom) {
            this.moreMenuDialogBottom = moreMenuDialogBottom;
            return this;
        }

        public Builder setAddDeleteItemToMoreMenu(boolean addDeleteItemToMoreMenu) {
            isAddDeleteItemToMoreMenu = addDeleteItemToMoreMenu;
            return this;
        }

        public Builder setViewAudienceToMoreMenu(boolean viewAudienceToMoreMenu) {
            isViewAudienceToMoreMenu = viewAudienceToMoreMenu;
            return this;
        }

        public Builder setDialogTextItemList(List<DialogText> dialogTextItemList) {
            this.dialogTextItemList = dialogTextItemList;
            return this;
        }

        public Builder setShowDialogBottom(boolean showDialogBottom) {
            isShowDialogBottom = showDialogBottom;
            return this;
        }

        public void startStories() {
            storyView.touchUp();
        }

        public void pauseStories() {
            storyView.touchDown();
        }

        public void show() {
            storyView.show(fragmentManager, TAG);
        }

        public void dismiss() {
            storyView.dismiss();
        }

        public Fragment getFragment() {
            return storyView;
        }

    }
}
