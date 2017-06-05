package ftn.e2.udd.websearch.api.crawler;

import org.springframework.security.access.prepost.PreAuthorize;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * Created by Aleksandar
 */

@Path("/crawler")
public interface CrawlerService {

    @POST
    @Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @PreAuthorize("hasRole('ADMIN')")
    ContinuousCrawlerJobResponse  getResults(ContinuousCrawlJobConfig request);

}
