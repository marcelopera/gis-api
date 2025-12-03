package br.com.truta.entities;

import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "partner")
public class PartnerEntity extends PanacheEntity {

    @Column(name = "trading_name", nullable = false)
    public String tradingName;

    @Column(name = "owner_name", nullable = false)
    public String ownerName;

    @Column(nullable = false, unique = true)
    public String document;

    @Column(nullable = false, columnDefinition = "geometry(Point, 4326)")
    public Point address; 

    @Column(nullable = false, columnDefinition = "geometry(MultiPolygon, 4326)")
    public MultiPolygon coverageArea;
}