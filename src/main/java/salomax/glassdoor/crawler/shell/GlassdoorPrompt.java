package salomax.glassdoor.crawler.shell;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.stereotype.Component;

/**
 * Created by marcos.salomao.
 */
@Component
public class GlassdoorPrompt implements PromptProvider {

    @Override
    public AttributedString getPrompt() {
        return new AttributedString("glassdoor $ ",
                AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN));
    }

}
