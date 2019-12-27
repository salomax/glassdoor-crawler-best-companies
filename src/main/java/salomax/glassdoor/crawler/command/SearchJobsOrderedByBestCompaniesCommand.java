package salomax.glassdoor.crawler.command;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import salomax.glassdoor.crawler.service.GlassdoorService;
import salomax.glassdoor.crawler.view.JobPostingView;

import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * Created by marcos.salomao.
 */
@ShellComponent
@Log
public class SearchJobsOrderedByBestCompaniesCommand {

    @Autowired
    private GlassdoorService glassdoorService;
    @Autowired
    private JobPostingView jobPostingView;

    @ShellMethod("Search top jobs based on company reviews")
    public String search(
            @ShellOption(help= "URL search") URL search,
            @ShellOption(help="Limit") Integer limit) {

        log.info("Searching...");

        final AtomicBoolean loading = this.startLoading();

        try {

            long time = System.currentTimeMillis();

            return this.glassdoorService.search(search, limit).stream()
                    .map(this.jobPostingView::format)
                    .collect(Collectors.joining("\n")) +
                    String.format("\nTotal search time %d ms", System.currentTimeMillis() - time);

        } catch (Exception e) {
            return e.getMessage();
        } finally {
            this.finishLoading(loading);
        }

    }

    private synchronized void finishLoading(AtomicBoolean loading) {
        try {
            loading.set(false);
            wait();
        } catch (InterruptedException e) {
            // do nothing
        }
    }

    private synchronized AtomicBoolean startLoading() {
        final AtomicBoolean loading = new AtomicBoolean(true);
        Thread thread = new Thread(() -> {
            try {
                float i = 0.0f;
                while(loading.get()) {
                    System.out.print(String.format("\rSearching %.1fs", i));
                    Thread.currentThread().sleep(100L);
                    i += 0.1;
                }
                System.out.print("\rDone!              \n");
            } catch (InterruptedException e) {
                // do nothing
            }
            synchronized(this) {
                this.notify();
            }
        });
        thread.start();
        return loading;
    }

}
