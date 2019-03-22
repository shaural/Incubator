package cs408.incubator;

import com.google.firebase.database.Exclude;

public class uploadImage {
    String mName;
    String mImageUrl;
    String mKey;

    public uploadImage() {
        //empty constructor needed
    }

    public uploadImage(String name, String imageUrl) {

            name = "No Name !";
        mName = name;
        mImageUrl = imageUrl;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

    @Exclude
    public String getKey(){
        return mKey;
    }

    @Exclude
    public void setKey(String key){
        mKey = key;
    }
}
