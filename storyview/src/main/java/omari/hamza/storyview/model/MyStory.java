package omari.hamza.storyview.model;

import android.graphics.Typeface;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.google.gson.Gson;

import java.util.Date;

public class MyStory implements Parcelable {

    public static final Creator<MyStory> CREATOR = new Creator<MyStory>() {
        @Override
        public MyStory createFromParcel(Parcel in) {
            return new MyStory(in);
        }

        @Override
        public MyStory[] newArray(int size) {
            return new MyStory[size];
        }
    };
    private String url;
    private StoryType storyType;
    private String text;
    private StoryTextFont textFont;
    private String backgroundColor;
    private Date date;
    private String description;
    private String textColor;
    private Typeface typeface;

    public MyStory(StoryType storyType, String url, Date date, String description) {
        this.storyType = storyType;
        this.url = url;
        this.date = date;
        this.description = description;
    }

    public MyStory(StoryType storyType, String url, Date date) {
        this.storyType = storyType;
        this.url = url;
        this.date = date;
    }

    public MyStory(StoryType storyType, String url) {
        this.storyType = storyType;
        this.url = url;
    }

    public MyStory(StoryType storyType, String text, StoryTextFont textFont, String backgroundColor, String textColor, Date date, String description) {
        this.storyType = storyType;
        this.text = text;
        this.textFont = textFont;
        this.backgroundColor = backgroundColor;
        this.textColor = textColor;
        this.date = date;
        this.description = description;
    }

    public MyStory(StoryType storyType, String text, StoryTextFont textFont, String backgroundColor, String textColor, Date date) {
        this.storyType = storyType;
        this.text = text;
        this.textFont = textFont;
        this.backgroundColor = backgroundColor;
        this.textColor = textColor;
        this.date = date;
    }

    public MyStory(StoryType storyType, String text, StoryTextFont textFont, String backgroundColor, String textColor) {
        this.storyType = storyType;
        this.text = text;
        this.textFont = textFont;
        this.backgroundColor = backgroundColor;
        this.textColor = textColor;
    }

    public MyStory(StoryType storyType, String text, Typeface typeface, String backgroundColor, String textColor, Date date, String description) {
        this.storyType = storyType;
        this.text = text;
        this.typeface = typeface;
        this.textFont = null;
        this.backgroundColor = backgroundColor;
        this.textColor = textColor;
        this.date = date;
        this.description = description;
    }

    public MyStory(StoryType storyType, String text, Typeface typeface, String backgroundColor, String textColor, Date date) {
        this.storyType = storyType;
        this.text = text;
        this.typeface = typeface;
        this.textFont = null;
        this.backgroundColor = backgroundColor;
        this.textColor = textColor;
        this.date = date;
    }

    public MyStory(StoryType storyType, String text, Typeface typeface, String backgroundColor, String textColor) {
        this.storyType = storyType;
        this.text = text;
        this.typeface = typeface;
        this.textFont = null;
        this.backgroundColor = backgroundColor;
        this.textColor = textColor;
    }

    protected MyStory(Parcel in) {
        storyType = StoryType.valueOf(in.readString());

        if (storyType == StoryType.TEXT) {
            text = in.readString();
            String typefaceName = in.readString();
            if (typefaceName != null && !typefaceName.trim().isEmpty()) {
                typeface = new Gson().fromJson(typefaceName, Typeface.class);
            }
            textFont = StoryTextFont.valueOf(in.readString());
            backgroundColor = in.readString();
            textColor = in.readString();
        } else {
            url = in.readString();
        }

        long time = in.readLong();
        if (time > 0) {
            date = new Date(time);
        }
        description = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(storyType.toString());

        if (storyType == StoryType.TEXT) {
            dest.writeString(text);
            if (typeface != null) {
                dest.writeString(new Gson().toJson(typeface));
            }
            dest.writeString(textFont.toString());
            dest.writeString(backgroundColor);
            dest.writeString(textColor);
        } else {
            dest.writeString(url);
        }
        if (date != null) {
            dest.writeLong(date.getTime());
        } else {
            dest.writeLong(0);
        }

        dest.writeString(description);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public StoryType getStoryType() {
        return storyType;
    }

    public void setStoryType(StoryType storyType) {
        this.storyType = storyType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public StoryTextFont getTextFont() {
        return textFont;
    }

    public void setTextFont(StoryTextFont textFont) {
        this.textFont = textFont;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Nullable
    public Typeface getTypeface() {
        return typeface;
    }

    public void setTypeface(Typeface typeface) {
        this.typeface = typeface;
    }

    @Override
    public String toString() {
        return "MyStory{" +
                "storyType=" + storyType +
                ", url='" + url + '\'' +
                ", text='" + text + '\'' +
                ", textFont=" + textFont +
                ", backgroundColor='" + backgroundColor + '\'' +
                ", textColor='" + textColor + '\'' +
                ", date=" + date +
                ", description='" + description + '\'' +
                ", typeface=" + typeface +
                '}';
    }
}
