package com.geodsea.pub.repository;

import com.geodsea.pub.domain.Rescue;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Spring Data JPA repository for the Skipper entity.
 */
public interface RescueRepository extends JpaRepository<Rescue, Long> {

    /**
     * Find all the rescue organisations that interest with the geometry specified.
     * <p>
     *     For a point, this method works fine. However line strings need to be "made valid".
     *     This is due to a bug in postgres.
     *     See http://stackoverflow.com/questions/24812031/unexpected-results-from-st-intersectslinestring-polygon
     *     for an explanation why st_makevalid is specified
     * </p>
     * @param geometry
     * @return
     */
    @Query("select r from Rescue r where intersects(r.zone.zone, st_makevalid(?1)) = TRUE")
    List<Rescue> getRescueOrganisationsForLocation(Geometry geometry);

    /**
     * Find one organisation that is no more than maxSeparation meters from the point, line or region defined
     * by the geometry.
     * @param geometry the geometry to find the closest rescue organisation for.
     * @param list the rescue organisations to choose from
     * @param maxSeparation maximum number of metres allowed at the closest point between the rescue zone and the geometry.
     * @return zero or one Rescue organisation
     */
    @Query("select r from Rescue r where r in ?2 and distance(r.zone.zone, ?1) <= ?3 order by distance(r.zone.zone, ?1)")
    List<Rescue> getClosestRescueOrganisation(Geometry geometry, List<Rescue> list, int maxSeparation);
}
