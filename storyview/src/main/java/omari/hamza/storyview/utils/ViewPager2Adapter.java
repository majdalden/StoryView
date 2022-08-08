package omari.hamza.storyview.utils;

import static omari.hamza.storyview.StoryView.MAX_STORY_TEXT_LENGTH;
import static omari.hamza.storyview.StoryView.MAX_STORY_TEXT_LINES;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.List;

import omari.hamza.storyview.R;
import omari.hamza.storyview.callback.StoryCallbacks;
import omari.hamza.storyview.model.MyStory;
import omari.hamza.storyview.model.StoryTextFont;
import omari.hamza.storyview.model.StoryType;

public class ViewPager2Adapter extends RecyclerView.Adapter<ViewPager2Adapter.ViewHolder> {

    private final Activity activity;
    private final List<MyStory> items;
    private final StoryCallbacks storyCallbacks;

    private boolean isFirstTime = true;
    private boolean storiesStarted = false;
    private int currentPosition = 0;

    private int maxStoryTextLength = MAX_STORY_TEXT_LENGTH;
    private int maxStoryTextLines = MAX_STORY_TEXT_LINES;

    public ViewPager2Adapter(Activity activity, List<MyStory> items, StoryCallbacks storyCallbacks) {
        this.activity = activity;
        this.items = items;
        this.storyCallbacks = storyCallbacks;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.layout_story_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.e("ViewPager2Adapter", "onBindViewHolder currentPosition: " + currentPosition + ", position: " + position);

        MyStory currentStory = items.get(holder.getAbsoluteAdapterPosition());

        if (!isFirstTime) {
            pauseStories(holder.getAbsoluteAdapterPosition());
        } else {
            isFirstTime = false;
        }

        final ConstraintLayout rootLayout = holder.rootLayout;
        final ImageView mImageView = holder.mImageView;
        final VideoView mVideoView = holder.mVideoView;
        final TextView mTextView = holder.mTextView;
        final ProgressBar progressBar = holder.progressBar;

        rootLayout.setBackgroundColor(Color.BLACK);

        if (!TextUtils.isEmpty(currentStory.getDescription())) {
            TextView textView = holder.descriptionTextView;
            textView.setVisibility(View.VISIBLE);
            textView.setText(currentStory.getDescription());
            textView.setOnClickListener(v -> {
                storyCallbacks.onDescriptionClickListener(holder.getAbsoluteAdapterPosition());
            });
        }

        if (currentStory.getStoryType() == StoryType.TEXT) {
            mTextView.setVisibility(View.VISIBLE);
            mImageView.setVisibility(View.GONE);
            mVideoView.setVisibility(View.GONE);

            storyCallbacks.changeOrientation(-1);

            if (maxStoryTextLength > 0) {
                InputFilter[] fArray = new InputFilter[1];
                fArray[0] = new InputFilter.LengthFilter(maxStoryTextLength);
                mTextView.setFilters(fArray);
            }

            if (maxStoryTextLines > 0) {
                mTextView.setMaxLines(maxStoryTextLines);
            }

            mTextView.setText(currentStory.getText());

            checkSizeText(activity, mTextView);

            try {
                mTextView.setBackgroundColor(Color.parseColor(currentStory.getBackgroundColor()));
            } catch (Exception e) {
                mTextView.setBackgroundColor(Color.BLACK);
//                e.printStackTrace();
            }

            try {
                mTextView.setTextColor(Color.parseColor(currentStory.getTextColor()));
            } catch (Exception e) {
                mTextView.setTextColor(Color.WHITE);
//                e.printStackTrace();
            }

            try {
                Typeface typeface = currentStory.getTypeface();

                if (typeface == null) {
                    typeface = getFontTypeFace(currentStory.getTextFont());
                }

                if (typeface != null) {
                    mTextView.setTypeface(typeface);
                }
            } catch (Exception e) {
//                e.printStackTrace();
            }

            startStory(holder.getAbsoluteAdapterPosition());

        } else {
            String fileUrl = currentStory.getUrl();
            fileUrl = fileUrl == null ? "" : fileUrl.trim();

            mTextView.setVisibility(View.GONE);
            mVideoView.setVisibility(View.GONE);
            mImageView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);

            if (currentStory.getStoryType() == StoryType.VIDEO) {
                mVideoView.setVisibility(View.VISIBLE);

                mVideoView.setVideoURI(Uri.parse(fileUrl));

                mVideoView.setOnPreparedListener(mp -> {
                    mp.seekTo(0);
                    mp.start();

                    storyCallbacks.changeOrientation(mp.getDuration());

                    mVideoView.setVisibility(View.VISIBLE);
                    mImageView.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);

                    startStory(holder.getAbsoluteAdapterPosition());
                });

                mVideoView.setOnErrorListener((mp, what, extra) -> {
                    storyCallbacks.nextStory();
                    return false;
                });
            } else {
                storyCallbacks.changeOrientation(-1);
            }

            Glide.with(activity)
                    .load(fileUrl)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e
                                , Object model
                                , Target<Drawable> target
                                , boolean isFirstResource) {
                            if (currentStory.getStoryType() != StoryType.VIDEO) {
                                storyCallbacks.nextStory();
                            }
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource
                                , Object model, Target<Drawable> target
                                , DataSource dataSource
                                , boolean isFirstResource) {

                            try {
                                if (resource != null) {
                                    PaletteExtraction pe = new PaletteExtraction(holder.rootLayout, ((BitmapDrawable) resource).getBitmap());
                                    pe.execute();
                                }
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                            if (currentStory.getStoryType() != StoryType.VIDEO) {
                                startStory(holder.getAbsoluteAdapterPosition());
                                progressBar.setVisibility(View.GONE);
                            }
                            return false;
                        }
                    })
                    .into(mImageView);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private void startStory(int position) {
        if (position == currentPosition) {
            if (storiesStarted) {
                storyCallbacks.resumeStories();
            } else {
                storiesStarted = true;
                storyCallbacks.startStories();
            }
        }
    }

    private void pauseStories(int position) {
        if (position == currentPosition) {
            storyCallbacks.pauseStories();
        }
    }

    private Typeface getFontTypeFace(StoryTextFont textFont) {
        switch (textFont) {
            case CAIRO_BOLD:
                return ResourcesCompat.getFont(activity, R.font.cairo_bold);
            case POPPINS_BOLD:
                return ResourcesCompat.getFont(activity, R.font.poppins_bold);
            case POPPINS_LIGHT:
                return ResourcesCompat.getFont(activity, R.font.poppins_light);
            case POPPINS_REGULAR:
                return ResourcesCompat.getFont(activity, R.font.poppins_regular);
            case POPPINS_SEMI_BOLD:
                return ResourcesCompat.getFont(activity, R.font.poppins_semi_bold);
            case ROBOTO_MEDIUM:
                return ResourcesCompat.getFont(activity, R.font.roboto_medium);
            case ROBOTO_REGULAR:
                return ResourcesCompat.getFont(activity, R.font.roboto_regular);
            case SF_PRO_DISPLAY_MEDIUM:
                return ResourcesCompat.getFont(activity, R.font.sf_pro_display_medium);
            case SOURCE_SAN_PRO_SEMIBOLD:
                return ResourcesCompat.getFont(activity, R.font.source_san_pro_semibold);
            case SOURCE_SAN_PROBOLD:
                return ResourcesCompat.getFont(activity, R.font.source_san_probold);
            default:
                return ResourcesCompat.getFont(activity, R.font.app_roboto_bold);
        }
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }

    private void checkSizeText(Context context, TextView textView) {
        if (context == null
                || textView == null
                || textView.getText() == null
                || textView.getText().toString().trim().isEmpty()) {
            return;
        }

        String textStory = textView.getText().toString().trim();
        int textStoryLength = textStory.length();
        float textStorySizeLarge = context.getResources().getDimension(R.dimen._36sdp);
        float textStorySizeMedium = context.getResources().getDimension(R.dimen._28sdp);
        float textStorySizeSmall = context.getResources().getDimension(R.dimen._20sdp);

        float textStoryToSizeLarge = ((1.0F / 3.0F) * maxStoryTextLength);
        float textStoryToSizeMedium = ((2.0F / 3.0F) * maxStoryTextLength);

        if (textStoryToSizeLarge > textStoryLength) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textStorySizeLarge);
        } else if (textStoryToSizeMedium > textStoryLength) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textStorySizeMedium);
        } else {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textStorySizeSmall);
        }
    }

    public void setMaxStoryTextLength(int maxStoryTextLength) {
        this.maxStoryTextLength = maxStoryTextLength;
    }

    public void setMaxStoryTextLines(int maxStoryTextLines) {
        this.maxStoryTextLines = maxStoryTextLines;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ConstraintLayout rootLayout;
        final ImageView mImageView;
        final VideoView mVideoView;
        final TextView mTextView;
        final TextView descriptionTextView;
        final ProgressBar progressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            rootLayout = itemView.findViewById(R.id.rootLayout);
            mImageView = itemView.findViewById(R.id.mImageView);
            mVideoView = itemView.findViewById(R.id.mVideoView);
            mTextView = itemView.findViewById(R.id.mTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }
}