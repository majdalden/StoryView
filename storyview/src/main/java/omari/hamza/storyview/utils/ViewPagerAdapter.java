package omari.hamza.storyview.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;

import omari.hamza.storyview.R;
import omari.hamza.storyview.callback.StoryCallbacks;
import omari.hamza.storyview.model.MyStory;
import omari.hamza.storyview.model.StoryTextFont;
import omari.hamza.storyview.model.StoryType;

public class ViewPagerAdapter extends PagerAdapter {

    private final ArrayList<MyStory> images;
    private final Context context;
    private final StoryCallbacks storyCallbacks;
    private boolean storiesStarted = false;
    private int currentPosition = 0;

    private int maxStoryTextLength = 300;
    private int maxStoryTextLine = 10;

    public ViewPagerAdapter(ArrayList<MyStory> images, Context context, StoryCallbacks storyCallbacks) {
        this.images = images;
        this.context = context;
        this.storyCallbacks = storyCallbacks;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup collection, final int position) {

        LayoutInflater inflater = LayoutInflater.from(context);

        MyStory currentStory = images.get(position);

        final View view = inflater.inflate(R.layout.layout_story_item, collection, false);

        final ImageView mImageView = view.findViewById(R.id.mImageView);
        final VideoView mVideoView = view.findViewById(R.id.mVideoView);
        final TextView mTextView = view.findViewById(R.id.mTextView);

        if (!TextUtils.isEmpty(currentStory.getDescription())) {
            TextView textView = view.findViewById(R.id.descriptionTextView);
            textView.setVisibility(View.VISIBLE);
            textView.setText(currentStory.getDescription());
            textView.setOnClickListener(v -> {
                storyCallbacks.onDescriptionClickListener(position);
            });
        }

        if (currentStory.getStoryType() == StoryType.TEXT) {
            mTextView.setVisibility(View.VISIBLE);
            mImageView.setVisibility(View.GONE);
            mVideoView.setVisibility(View.GONE);


            InputFilter[] fArray = new InputFilter[1];
            fArray[0] = new InputFilter.LengthFilter(maxStoryTextLength);
            mTextView.setFilters(fArray);
            mTextView.setMaxLines(maxStoryTextLine);


            mTextView.setText(currentStory.getText());

            checkSizeText(context, mTextView);

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

            startStory(position);

        } else {
            String fileUrl = currentStory.getUrl();
            fileUrl = fileUrl == null ? "" : fileUrl.trim();

            mTextView.setVisibility(View.GONE);

            pauseStories(position);

            if (currentStory.getStoryType() == StoryType.VIDEO) {
                mVideoView.setVisibility(View.VISIBLE);
                mImageView.setVisibility(View.GONE);

                mVideoView.setVideoURI(Uri.parse(fileUrl));

                mVideoView.setOnPreparedListener(mp -> {
                    mp.start();
                    startStory(position);
                });

                mVideoView.setOnErrorListener((mp, what, extra) -> {
                    storyCallbacks.nextStory();
                    return false;
                });
            } else {
                mVideoView.setVisibility(View.GONE);
                mImageView.setVisibility(View.VISIBLE);
            }

            Glide.with(context)
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
                                    PaletteExtraction pe = new PaletteExtraction(view.findViewById(R.id.relativeLayout)
                                            , ((BitmapDrawable) resource).getBitmap());
                                    pe.execute();
                                }
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }

                            if (currentStory.getStoryType() != StoryType.VIDEO) {
                                startStory(position);
                            }
                            return false;
                        }
                    })
                    .into(mImageView);
        }

        collection.addView(view);

        return view;
    }

    private void startStory(int position) {
        if (!storiesStarted && position == currentPosition) {
            storiesStarted = true;
            storyCallbacks.startStories();
        }
    }

    private void pauseStories(int position) {
        if (storiesStarted && position == currentPosition) {
            storiesStarted = false;
            storyCallbacks.pauseStories();
        }
    }

    private Typeface getFontTypeFace(StoryTextFont textFont) {
        switch (textFont) {
            case CAIRO_BOLD:
                return ResourcesCompat.getFont(context, R.font.cairo_bold);
            case POPPINS_BOLD:
                return ResourcesCompat.getFont(context, R.font.poppins_bold);
            case POPPINS_LIGHT:
                return ResourcesCompat.getFont(context, R.font.poppins_light);
            case POPPINS_REGULAR:
                return ResourcesCompat.getFont(context, R.font.poppins_regular);
            case POPPINS_SEMI_BOLD:
                return ResourcesCompat.getFont(context, R.font.poppins_semi_bold);
            case ROBOTO_MEDIUM:
                return ResourcesCompat.getFont(context, R.font.roboto_medium);
            case ROBOTO_REGULAR:
                return ResourcesCompat.getFont(context, R.font.roboto_regular);
            case SF_PRO_DISPLAY_MEDIUM:
                return ResourcesCompat.getFont(context, R.font.sf_pro_display_medium);
            case SOURCE_SAN_PRO_SEMIBOLD:
                return ResourcesCompat.getFont(context, R.font.source_san_pro_semibold);
            case SOURCE_SAN_PROBOLD:
                return ResourcesCompat.getFont(context, R.font.source_san_probold);
            default:
                return ResourcesCompat.getFont(context, R.font.app_roboto_bold);
        }
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        (container).removeView((View) object);
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

    public void setMaxStoryTextLine(int maxStoryTextLine) {
        this.maxStoryTextLine = maxStoryTextLine;
    }
}
