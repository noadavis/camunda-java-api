package org.example.camunda.app.appdata;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Calendar;

@Component
public class FooterData {
    @Value("${spring.application.version}")
    private String version;
    @Value("${spring.application.shortname}")
    private String shortName;
    @Value("${spring.application.company}")
    private String company;
    private String Right;
    private String Left;
    public FooterData() {
    }
    @PostConstruct
    private void init() {
        updateFooter();
        this.Right = String.format("%s, v:%s", this.shortName, this.version);
        System.out.println("FooterData init");
    }
    public void updateFooter() {
        this.Left = String.format("@%d %s", Calendar.getInstance().get(Calendar.YEAR), this.company);
    }
    public String getRight() {
        return Right;
    }
    public String getLeft() {
        return Left;
    }
}
