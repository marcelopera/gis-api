package br.com.truta.service;

import java.util.List;

import org.jboss.logging.Logger;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.geojson.GeoJsonReader;

import br.com.truta.entities.PartnerEntity;
import br.com.truta.models.PartnerDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

import com.fasterxml.jackson.databind.JsonNode;

@ApplicationScoped
public class PartnerService {

    @PersistenceContext
    EntityManager em;

    @Inject
    Logger logger;

    public List<String> getPartnersByCoverage(double lng, double lat) {
        String sql = "SELECT trading_name FROM partner WHERE ST_Contains(coveragearea, ST_SetSRID(ST_Point(:lng, :lat), 4326))";

        Query query = em.createNativeQuery(sql, String.class);
        query.setParameter("lng", lng);
        query.setParameter("lat", lat);
        return (List<String>) query.getResultList();
    }

    public String getClosestPartner(double lng, double lat) {
        String sql = "SELECT trading_name FROM partner ORDER BY ST_Distance(address, ST_SetSRID(ST_Point(:lng, :lat), 4326)) LIMIT 1";
        Query query = em.createNativeQuery(sql, String.class);
        query.setParameter("lng", lng);
        query.setParameter("lat", lat);
        return (String) query.getResultList().get(0);
    }
    
    public Response persistPartner(PartnerDTO dto) {
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        GeoJsonReader reader = new GeoJsonReader(geometryFactory);

        PartnerEntity entity = new PartnerEntity();
        entity.tradingName = dto.tradingName();
        entity.ownerName = dto.ownerName();
        entity.document = dto.document();

        try {
            if (dto.address() != null) {
                entity.address = (Point) reader.read(dto.address().toString());
            }
            if (dto.coverageArea() != null) {
                entity.coverageArea = (MultiPolygon) reader.read(dto.coverageArea().toString());
            }
        } catch (ParseException e) {
            logger.error("Falha ao transformar json em geometria: " + e.getMessage(), e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        em.persist(entity);

        return Response.status(Response.Status.CREATED).entity(entity).build();
    }
}