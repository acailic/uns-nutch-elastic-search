package ftn.e2.udd.websearch.api.search;


import javax.ws.rs.*;

@Path("/search")
public interface SearchService {
    @POST
    @Path("/{resultsPerPage}/{pageNumber}")
    @Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON + ";charset=UTF-8")
    ApiSearchResponse getResults(ApiSearchRequest request, @PathParam("resultsPerPage") Integer resultsPerPage, @PathParam("pageNumber") Integer pageNumber);
}
