package br.com.truta.resources;

import java.util.List;

import org.jboss.logging.Logger;

import br.com.truta.entities.PartnerEntity;
import br.com.truta.models.PartnerDTO;
import br.com.truta.service.PartnerService;
import io.smallrye.common.annotation.RunOnVirtualThread;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@RequestScoped
@Path("/partner")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PartnerResource {

    @Inject
    Logger logger;

    @Inject
    PartnerService ps;

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @RunOnVirtualThread
    public Response createPartner(PartnerDTO partner) {
        return ps.persistPartner(partner);
        
    }

    @GET
    public Uni<List<PartnerEntity>> getAllPartners() {
        return Uni.createFrom().<List<PartnerEntity>>item(() -> 
            PartnerEntity.listAll()
        ).runSubscriptionOn(Infrastructure.getDefaultWorkerPool());
    }

    @GET
    @Path("{id}")
    public Uni<Response> getPartnerById(@PathParam("id") long id) {
        return Uni.createFrom().item(() -> PartnerEntity.<PartnerEntity>findById(id))
                    .map(pe -> Response.ok(pe.id).build())
                    .replaceIfNullWith(() -> Response.status(Response.Status.NOT_FOUND).build()).runSubscriptionOn(Infrastructure.getDefaultWorkerPool());
    }

    @POST
    @Path("/coverage")
    public Uni<List<String>> getPartners(@QueryParam("lng") double lng, @QueryParam("lat") double lat) {
        return Uni.createFrom().item(() -> ps.getPartnersByCoverage(lng, lat)).runSubscriptionOn(Infrastructure.getDefaultWorkerPool());
    }
    @POST
    @Path("/closest")
    public Uni<String> getPartner(@QueryParam("lng") double lng, @QueryParam("lat") double lat) {
        return Uni.createFrom().item(() -> ps.getClosestPartner(lng, lat)).runSubscriptionOn(Infrastructure.getDefaultWorkerPool());
    }
}