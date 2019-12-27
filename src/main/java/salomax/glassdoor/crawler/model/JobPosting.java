package salomax.glassdoor.crawler.model;

import lombok.Data;

/**
 * Created by marcos.salomao.
 */
@Data
public class JobPosting {

    private String jobTitle;
    private String empolyerName;
    private Float empolyerScore;
    private String jobLink;

}
