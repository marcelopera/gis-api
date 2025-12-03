package br.com.truta.resources;

import java.util.List;

import org.jboss.logging.Logger;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.io.geojson.GeoJsonReader;

import com.fasterxml.jackson.databind.JsonNode;

import br.com.truta.entities.PartnerEntity;
import br.com.truta.service.PartnerService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional; // Adicionar para escrita
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
    public Response createPartner(JsonNode json) throws Exception {
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        GeoJsonReader reader = new GeoJsonReader(geometryFactory);

        PartnerEntity entity = new PartnerEntity();
        entity.tradingName = json.get("tradingName").asText();
        entity.ownerName = json.get("ownerName").asText();
        entity.document = json.get("document").asText();

        entity.address = (Point) reader.read(json.get("address").toString());
        entity.coverageArea = (MultiPolygon) reader.read(json.get("coverageArea").toString());

        entity.persist();

        logger.info("Address: " + entity.address);
        logger.info("Coverage Area: " + entity.coverageArea);

        return Response.status(Response.Status.CREATED).entity(entity.coverageArea).build();
    }

    @GET
    public List<PartnerEntity> getAllPartners() {
        return PartnerEntity.listAll();
    }

    @GET
    @Path("{id}")
    public Response getPartnerById(@PathParam("id") long id) {
        PartnerEntity p = PartnerEntity.findById(id);
        if (p == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(p).build();
    }

    @POST
    @Path("/coverage")
    public List<String> getPartners(@QueryParam("lng") double lng, @QueryParam("lat") double lat) {
        return ps.getPartnersByCoverage(lng, lat);
    }
    @POST
    @Path("/closest")
    public String getPartner(@QueryParam("lng") double lng, @QueryParam("lat") double lat) {
        return ps.getClosestPartner(lng, lat);
    }
}