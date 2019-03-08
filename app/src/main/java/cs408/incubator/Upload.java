package cs408.incubator;

public class Upload {
    public String docsID;
    public String name;
    public String url;

    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public Upload() {
    }

    public Upload(String docsID, String name, String url) {
        {
            this.docsID = docsID;
            this.name = name;
            this.url = url;
        }
    }

    public String getID() {
        return docsID;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }
}