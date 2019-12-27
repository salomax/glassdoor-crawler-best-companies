package salomax.glassdoor.crawler.view;

import org.springframework.stereotype.Component;
import salomax.glassdoor.crawler.model.JobPosting;

/**
 * Created by marcos.salomao.
 */
@Component
public class JobPostingView {

    public String format(JobPosting jobPosting) {
        return String.format("%.2f\t%s\n\t%s\n\t%s\n\n",
                jobPosting.getEmpolyerScore(),
                jobPosting.getEmpolyerName(),
                jobPosting.getJobTitle(),
                jobPosting.getJobLink());
    }

}
