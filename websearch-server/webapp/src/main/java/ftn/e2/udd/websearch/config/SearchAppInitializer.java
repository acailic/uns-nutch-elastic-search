package ftn.e2.udd.websearch.config;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.*;
import java.util.EnumSet;

/**
 * Web application initializer class
 */
public class SearchAppInitializer implements WebApplicationInitializer {
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();
        ctx.register(SearchAppContext.class);
        ctx.setServletContext(servletContext);
        servletContext.setInitParameter("resteasy.resources", "ftn.e2.udd.websearch.api.impl.SearchServiceImpl");
        ServletRegistration.Dynamic dynamic = servletContext.addServlet("dispatcher", new DispatcherServlet(ctx));
        dynamic.addMapping("/");
        dynamic.setLoadOnStartup(1);
        FilterRegistration.Dynamic filter = servletContext.addFilter("corsFilter", new CorsFilter());
        filter.addMappingForServletNames(EnumSet.of(DispatcherType.REQUEST), true, "dispatcher");
    }
}
