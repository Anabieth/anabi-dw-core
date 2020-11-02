package lv.llu.science.utils.time;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.logging.Logger;

@Component
@Profile("time")
public class TimeFilter implements Filter {

    public final static String TIME_HEADER = "_TIME_";

    private final TimeMachine timeMachine;

    @Autowired
    public TimeFilter(RequestTimeMachine timeMachine) {
        this.timeMachine = timeMachine;
    }


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String timeString = ((HttpServletRequest) request).getHeader(TIME_HEADER);
        if (timeString != null) {
            timeMachine.fixedAt(LocalDateTime.parse(timeString));
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Logger.getLogger(this.getClass().getCanonicalName()).warning("==== Mock time filter initialized! ===");
    }
}
