package omari.hamza.storyviewdemo;

import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.OnStoryChangedCallback;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;
import omari.hamza.storyview.model.StoryTextFont;
import omari.hamza.storyview.model.StoryType;
import omari.hamza.storyviewdemo.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mBinding.setActivity(this);
    }

    public void showStories() {

        final ArrayList<MyStory> myStories = new ArrayList<>();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");

        try {
            MyStory story1 = new MyStory(
                    StoryType.IMAGE,
                    "https://media.pri.org/s3fs-public/styles/story_main/public/images/2019/09/092419-germany-climate.jpg?itok=P3FbPOp-",
                    simpleDateFormat.parse("20-10-2019 10:00:00")
            );
            myStories.add(story1);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            MyStory story2 = new MyStory(
                    StoryType.IMAGE,
                    "http://i.imgur.com/0BfsmUd.jpg",
                    simpleDateFormat.parse("26-10-2019 15:00:00"),
                    "#TEAM_STANNIS"
            );
            myStories.add(story2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        MyStory story3 = new MyStory(
                StoryType.IMAGE,
                "https://mfiles.alphacoders.com/681/681242.jpg"
        );
        myStories.add(story3);

        try {
            MyStory story4 = new MyStory(
                    StoryType.TEXT,
                    "This is a text story",
                    StoryTextFont.APP_ROBOTO_BOLD,
                    "#FF000000",
                    "#FFFFFFFF",
                    simpleDateFormat.parse("26-10-2019 15:00:00"),
                    "#TEAM_STANNIS"
            );
            myStories.add(story4);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        MyStory story5 = new MyStory(
                StoryType.TEXT,
                "This is a text story",
                StoryTextFont.APP_ROBOTO_BOLD,
                "#FF000000",
                "#FFFFFFFF"
        );
        myStories.add(story5);

        MyStory story6 = new MyStory(
                StoryType.TEXT,
                "This is a text story",
                Typeface.create("sans-serif-medium", Typeface.NORMAL),
                "#FF000000",
                "#FFFFFFFF"
        );
        myStories.add(story6);

        MyStory story7 = new MyStory(
                StoryType.TEXT,
                "This is a text story",
                ResourcesCompat.getFont(this, R.font.cairo_bold),
                "#FFD15C5C",
                "#FFFFFFFF"
        );
        myStories.add(story7);


        new StoryView.Builder(getSupportFragmentManager())
                .setStoriesList(myStories)
                .setStoryDuration(5000)
                .setTitleText("Hamza Al-Omari")
                .setSubtitleText("Damascus")
                .setStoryClickListeners(new StoryClickListeners() {
                    @Override
                    public void onDescriptionClickListener(int position) {
                        Toast.makeText(MainActivity.this, "Clicked: " + myStories.get(position).getDescription(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onTitleIconClickListener(int position) {
                    }
                })
                .setOnStoryChangedCallback(new OnStoryChangedCallback() {
                    @Override
                    public void storyChanged(int position) {
                        Toast.makeText(MainActivity.this, position + "", Toast.LENGTH_SHORT).show();
                    }
                })
                .setStartingIndex(0)
                .setRtl(true)
                .build()
                .show();

    }
}
