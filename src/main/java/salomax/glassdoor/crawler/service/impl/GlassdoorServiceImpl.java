package salomax.glassdoor.crawler.service.impl;

import lombok.extern.java.Log;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import salomax.glassdoor.crawler.model.JobPosting;
import salomax.glassdoor.crawler.service.GlassdoorService;
import salomax.glassdoor.crawler.view.JobPostingList;

import java.io.IOException;
import java.net.URL;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by marcos.salomao.
 */
@Service
@Log
public class GlassdoorServiceImpl implements GlassdoorService {

    @Value("${reddit.executor.thread-pool:5}")
    private Integer threadPool;
    @Value("${reddit.executor.timeout:120}")
    private Integer timeout;
    private ExecutorService executorService;

    @Override
    public List<JobPosting> search(URL url, Integer limit) throws Exception {

        log.fine("Start searching threads at " + url);

        this.executorService = Executors.newFixedThreadPool(this.threadPool);

        List<JobPosting> jobPostings = Collections.synchronizedList(new JobPostingList(limit));

        this.search(url, jobPostings);

        try {
            this.executorService.awaitTermination(this.timeout, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new Exception("Thread can not be finished", e);
        }

        return jobPostings;
    }

    protected void search(URL url, List<JobPosting> jobPostings) {

        this.executorService.submit(() -> {

            try {

                Document document = this.openPage(url);

                Optional<URL> nextPage = this.findNextPage(url, document);
                if (!nextPage.isPresent()) {
                    log.fine("No pages found");
                    this.executorService.shutdown();
                    return;
                }

                this.search(nextPage.get(), jobPostings);

                Elements elements = this.findJobPosting(document);
                for (Element element : elements) {
                    jobPostings.add(this.parse(url, element));
                }

            } catch (IOException e) {
                throw new RuntimeException("URL can not be connected: " + url, e);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        });

    }

    protected Document openPage(URL url) throws IOException {
        System.out.println(String.format("\rOpening %s", url));
        return Jsoup.connect(url.toString()).userAgent("bot").get();
    }

    protected Optional<URL> findNextPage(URL url, Document document) throws Exception {
        Element nextPage = document.selectFirst("li.next a");
        if (nextPage != null && StringUtils.hasText(nextPage.attr("href"))) {
            return Optional.of(new URL(url.getProtocol() + "://" + url.getHost() + nextPage.attr("href")));
        }
        return Optional.empty();
    }

    protected JobPosting parse(URL url, Element element) {

        JobPosting jobPosting = new JobPosting();

        jobPosting.setJobTitle(element.select("div.jobContainer a.jobTitle").text());
        jobPosting.setEmpolyerName(element.select("div.jobContainer div.jobEmpolyerName").text());
        jobPosting.setJobLink(url.getProtocol() + "://" + url.getHost() + element.select("div.jobContainer a.jobTitle").attr("href"));

        if (element.select("div.logoWrap span.compactStars").hasText()) {
            jobPosting.setEmpolyerScore(new Float(element.select("span.compactStars").text().replace(',', '.')));
        }

        return jobPosting;
    }

    protected Elements findJobPosting(Document document) {
        return document.select("li.jl");
    }

}
