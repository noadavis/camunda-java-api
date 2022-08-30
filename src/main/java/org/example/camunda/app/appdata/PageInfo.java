package org.example.camunda.app.appdata;

public class PageInfo {
    private String Title;
    private final String Icon;
    private final String BackPage;
    public PageInfo(String title, String icon, String backPage) {
        Title = title;
        Icon = icon;
        BackPage = backPage;
    }
    public String getTitle() {
        return Title;
    }
    public void setTitle(String value) {
        this.Title = value;
    }
    public String getIcon() {
        return Icon;
    }
    public String getBackPage() {
        return BackPage;
    }
}
