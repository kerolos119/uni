package org.example.smartunipro.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GPSService {

    private static final double EARTH_RADIUS_METERS = 6371000.0;
    private static final double MAX_DISTANCE_METERS = 50.0;

    //Haversine
    public double calculateHaversineDistance(double lat1, double lon1,
                                             double lat2, double lon2) {
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);

        double deltaLat = lat2Rad - lat1Rad;
        double deltaLon = lon2Rad - lon1Rad;

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                        Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = EARTH_RADIUS_METERS * c;

        log.info(" Calculated distance: {} meters", String.format("%.2f", distance));
        return distance;
    }

    /**
     * Calculate the direction from the student to the building
     */
    public String calculateDirection(double lat1, double lon1,
                                     double lat2, double lon2) {

        double phi1 = Math.toRadians(lat1);
        double phi2 = Math.toRadians(lat2);
        double deltaLambda = Math.toRadians(lon2 - lon1);

        double y = Math.sin(deltaLambda) * Math.cos(phi2);
        double x = Math.cos(phi1) * Math.sin(phi2) -
                Math.sin(phi1) * Math.cos(phi2) * Math.cos(deltaLambda);

        double bearing = Math.toDegrees(Math.atan2(y, x));
        bearing = (bearing + 360) % 360;

        String[] directions = {
                "North ↑", "North East ↗", "East →", "South East ↘",
                "South ↓", "South West ↙", "West ←", "North West ↖"
        };

        int index = (int) Math.round(bearing / 45) % 8;
        return directions[index];
    }

    /**
     * Check distance and direction together
     */
    public AttendanceCheckResult checkDistance(double studentLat, double studentLon,
                                               double buildingLat, double buildingLon) {

        double distance = calculateHaversineDistance(studentLat, studentLon,
                buildingLat, buildingLon);

        boolean isWithinRange = distance <= MAX_DISTANCE_METERS;
        String direction = calculateDirection(studentLat, studentLon,
                buildingLat, buildingLon);

        if (isWithinRange) {
            log.info(" Student is within range: {} meters", String.format("%.1f", distance));
        } else {
            log.warn(" Student is out of range: {} meters > 50 meters", String.format("%.1f", distance));
            log.info(" Required direction: {}", direction);
        }

        return new AttendanceCheckResult(distance, isWithinRange, direction);
    }


    public record AttendanceCheckResult(
            double distance,
            boolean withinRange,
            String direction
    ) {}


}