package salomax.glassdoor.crawler.view;

import salomax.glassdoor.crawler.model.JobPosting;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by marcos.salomao.
 */
public class JobPostingList extends ArrayList<JobPosting> {

    private Integer capacity;
    public JobPostingList(int capacity) {
        super(capacity);
        this.capacity = capacity;
    }

    public boolean add(JobPosting jobPosting) {

        if (jobPosting.getEmpolyerScore() == null) {
            return false;
        }

        super.add(jobPosting);

        Collections.sort(this, (o1, o2) ->
                o1.getEmpolyerScore() != null && o2 .getEmpolyerScore() != null &&
                        o1.getEmpolyerScore() < o2.getEmpolyerScore() ? 1 : -1);

        if (this.size() > this.capacity) {
            this.subList(this.capacity, this.size()).clear();
        }

        return true;
    }

}
