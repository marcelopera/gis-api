package br.com.truta.service;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@ApplicationScoped
public class PartnerService {

    @PersistenceContext
    EntityManager em;

    public List<String> getPartnersByCoverage(double lng, double lat) {
        String sql = "SELECT trading_name FROM partner WHERE ST_Contains(coveragearea, ST_SetSRID(ST_Point(:lng, :lat), 4326))";
        
        Query query = em.createNativeQuery(sql, String.class);
        query.setParameter("lng", lng);
        query.setParameter("lat", lat);
        return (List<String>) query.getResultList();
    }

    public String getClosestPartner (double lng, double lat) {
        String sql = "SELECT trading_name FROM partner ORDER BY ST_Distance(address, ST_SetSRID(ST_Point(:lng, :lat), 4326)) LIMIT 1";
        Query query = em.createNativeQuery(sql, String.class);
        query.setParameter("lng", lng);
        query.setParameter("lat", lat);
        return (String) query.getResultList().get(0);
    }
}