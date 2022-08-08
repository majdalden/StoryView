package omari.hamza.storyviewdemo;

import android.graphics.Typeface;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import omari.hamza.storyview.StoryView;
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
            myStories.add(new MyStory(
                    StoryType.IMAGE,
                    "https://app.hony.us/uploads/picture/b8e5deec0a8d41b49b3ed788a577dc27.jpg",
                    simpleDateFormat.parse("20-10-2021 00:00:00")
            ));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            MyStory story1 = new MyStory(
                    StoryType.IMAGE,
                    "https://i.picsum.photos/id/370/1920/1080.jpg?hmac=BX7F76Chb5YLvSLJJTwZUSAIIFgcJsJTvJ55QkUH40E",
                    simpleDateFormat.parse("20-10-2019 10:00:00")
            );
            myStories.add(story1);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            MyStory story2 = new MyStory(
                    StoryType.IMAGE,
//                    "https://i.picsum.photos/id/164/3840/2160.jpg?hmac=CWM12wwPFuBimhVAl7RT-A8PoZxPNkGsnzQQcI0mIJU",
                    "https://app.hony.us/story/file/299ed998-fc97-4fe3-8862-274c78d8cc82.png",
                    simpleDateFormat.parse("26-10-2019 15:00:00"),
                    "#TEAM_STANNIS"
            );
            myStories.add(story2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        MyStory story3 = new MyStory(
                StoryType.IMAGE,
                "https://i.picsum.photos/id/674/1920/1080.jpg?hmac=-Di2Z5ARQrYUoegBDpVrMOaU5eT5_bvjllHiea1lqEs"
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
                "This is a text story1",
                StoryTextFont.APP_ROBOTO_BOLD,
                "#FF000000",
                "#FFFFFFFF"
        );
        myStories.add(story5);

        MyStory story6 = new MyStory(
                StoryType.TEXT,
                "This is a text story2",
                Typeface.create("sans-serif-medium", Typeface.NORMAL),
                "#FF000000",
                "#FFFFFFFF"
        );
        myStories.add(story6);

        MyStory story7 = new MyStory(
                StoryType.TEXT,
                "This is a text story3",
                ResourcesCompat.getFont(this, R.font.cairo_bold),
                "#FFD15C5C",
                "#FFFFFFFF"
        );
        myStories.add(story7);

        try {
            MyStory story8 = new MyStory(
                    StoryType.VIDEO,
//                    "https://hony.us:5443/upload/6df407955e48d5e4709d49621f99ace4532ba6ce/Sd4GIoOAUGZre9iSuvwpO1ExUQ2LsNHt15Q2mpFB/VIDEO_20220805_062921.mp4",
//                    "https://drive.google.com/file/d/1dug8adH3JUzUQOjDkgiGiK_Nvrfp8-AQ/view?usp=sharing",
//                    "https://drive.google.com/file/d/1AQOB6and5XdEGWd-DYYYZqubDVJQIYZ-/view?usp=sharing",
//                    "https://miran-media.s3.amazonaws.com/resources/2020/03/23/built-in_method_split_of_str_object_at_0x7fa042111d30.jpeg",
                    "https://miran-media.s3.amazonaws.com/resources/2020/03/23/CA_-_25.mp4",
                    simpleDateFormat.parse("03-10-2020 20:00:00")
            );
            myStories.add(story8);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        MyStory story9 = new MyStory(
                StoryType.TEXT,
                "1\n2\n3\n4",
                Typeface.create("sans-serif-medium", Typeface.NORMAL),
                "#FF000000",
                "#FFFFFFFF"
        );
        myStories.add(story9);


        createStoryView(myStories);

    }

    private void createStoryView(ArrayList<MyStory> myStories) {

        if (myStories == null || myStories.isEmpty()) {
            return;
        }

        new StoryView.Builder(getSupportFragmentManager())
                .setStoriesList(myStories) // Required
                .setStoryDuration(5000) // Default is 2000 Millis (2 Seconds)
                .setTitleText("Hamza Al-Omari")
                .setSubtitleText("Damascus")
                .setTitleLogoUrl("https://picsum.photos/1920/1080") // Default is Hidden
                .setShowDialogBottom(true)
                .setAddDeleteItemToMoreMenu(true)
                .setViewAudienceToMoreMenu(true)
                .setMaxStoryTextLines(300)
                .setMaxStoryTextLength(10)
                .setStoryClickListeners(new StoryClickListeners() {
                    @Override
                    public void onDescriptionClickListener(int position) {
                        //your action
                    }

                    @Override
                    public void onTitleIconClickListener(int position) {
                        //your action
                    }
                }) // Optional Listeners
                .setOnClickDeleteStoryListener(position -> {
                    myStories.remove(position);
                    createStoryView(myStories);
                    //your action
                })
                .build() // Must be called before calling show method
                .show();
    }

    /*private void createStoryView(ArrayList<MyStory> myStories) {
    if (myStories == null || myStories.isEmpty()) {
            return;
        }
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
                .setOnStoryChangedCallback(position -> {
//                    Toast.makeText(MainActivity.this, position + "", Toast.LENGTH_SHORT).show();
                })
                .setOnClickDeleteStoryListener((currentItem) -> {
                    myStories.remove(currentItem);
                    createStoryView(myStories);
                })
                .setStartingIndex(0)
                .setRtl(true)
                .build()
                .show();
    }*/
}
