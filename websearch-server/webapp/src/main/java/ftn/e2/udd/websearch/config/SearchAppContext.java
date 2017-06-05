package ftn.e2.udd.websearch.config;

import ftn.e2.udd.websearch.util.ApplicationContextProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.mvc.SimpleControllerHandlerAdapter;

/**
 * Code based application configuration
 */
@Configuration
@EnableWebMvc
@ComponentScan({"ftn.e2.udd.websearch.api", "ftn.e2.udd.websearch.api.impl", "ftn.e2.udd.websearch.elastic", "ftn.e2.udd.websearch.exception", "ftn.e2.udd.websearch.crawler"})
@EnableTransactionManagement
@ImportResource({"classpath:springmvc-resteasy.xml"})
public class SearchAppContext {

    @Bean
    public static ApplicationContextProvider applicationContextProvider() {
        return new ApplicationContextProvider();
    }

    @Bean
    public SimpleControllerHandlerAdapter simpleControllerHandlerAdapter() {
        return new SimpleControllerHandlerAdapter();
    }

}
