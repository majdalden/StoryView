package omari.hamza.storyview.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

            mTextView.setText(currentStory.getText());

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

        } else {
            mTextView.setVisibility(View.GONE);
            mImageView.setVisibility(View.VISIBLE);

            Glide.with(context)
                    .load(currentStory.getUrl())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            storyCallbacks.nextStory();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            if (resource != null) {
                                PaletteExtraction pe = new PaletteExtraction(view.findViewById(R.id.relativeLayout),
                                        ((BitmapDrawable) resource).getBitmap());
                                pe.execute();
                            }
                            if (!storiesStarted) {
                                storiesStarted = true;
                                storyCallbacks.startStories();
                            }
                            return false;
                        }
                    })
                    .into(mImageView);
        }

        collection.addView(view);

        return view;
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
}
