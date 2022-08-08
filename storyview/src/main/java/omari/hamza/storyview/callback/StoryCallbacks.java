package omari.hamza.storyview.callback;

public interface StoryCallbacks {

    void startStories();

    void resumeStories();

    void pauseStories();

    void nextStory();

    void onDescriptionClickListener(int position);

    void changeOrientation(long duration);

}
