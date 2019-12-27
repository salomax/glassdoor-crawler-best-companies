package salomax.glassdoor.crawler.service;

import salomax.glassdoor.crawler.model.JobPosting;

import java.net.URL;
import java.util.List;

/**
 * Created by marcos.salomao.
 */
public interface GlassdoorService {

    List<JobPosting> search(URL url, Integer limit) throws Exception;

}
