package cz.opendata.tenderstats;



/**
 * Geographic position in latitude / longitude format.
 *
 * @author Matej Snoha
 */
public class Position {

    /**
     * Latitude in decimal format
     */
    private Double latitude;

    /**
     * Longitude in decimal format
     */
    private Double longitude;

    /**
     * @return the latitude
     */
    public Double getLatitude() {
        return latitude;
    }

    /**
     * @param latitude
     *            the latitude to set
     */
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    /**
     * @return the longitude
     */
    public Double getLongitude() {
        return longitude;
    }

    /**
     * @param longitude
     *            the longitude to set
     */
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    /**
     * Constructs Position from given parameters
     *
     * @param latitude
     *            in decimal format
     * @param longitude
     *            in decimal format
     */
    public Position(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Constructs undefined Position
     *
     * @see #isUndefined()
     */
    public Position() {
        this.latitude = Double.NaN;
        this.longitude = Double.NaN;
    }

    /**
     * @return true if etiher latitude or longitude is undefined, false otherwise
     */
    public boolean isUndefined() {
        return latitude.isNaN() || longitude.isNaN();
    }

    /**
     * Text representation
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getLatitude() + " " + getLongitude();
    }

    /**
     * Calculates geodetic distance to another Position using Vincenty inverse formula for ellipsoids
     *
     * @returns distance in meters with 5.10<sup>-4</sup> precision
     * @see <a href="http://www.movable-type.co.uk/scripts/latlong-vincenty.html">Adapted from this article</a> Chris
     *      Veness</a>
     */
    public double distanceTo(Position otherPosition) {
        double lat1 = getLatitude();
        double lon1 = getLongitude();
        double lat2 = otherPosition.getLatitude();
        double lon2 = otherPosition.getLongitude();

        final double a = 6378137, b = 6356752.314245, f = 1 / 298.257223563; // WGS-84 ellipsoid params

        double L = Math.toRadians(lon2 - lon1);
        double U1 = Math.atan((1 - f) * Math.tan(Math.toRadians(lat1)));
        double U2 = Math.atan((1 - f) * Math.tan(Math.toRadians(lat2)));
        double sinU1 = Math.sin(U1), cosU1 = Math.cos(U1);
        double sinU2 = Math.sin(U2), cosU2 = Math.cos(U2);

        double sinLambda, cosLambda, sinSigma, cosSigma, sigma, sinAlpha, cosSqAlpha, cos2SigmaM;
        double lambda = L, lambdaP, iterLimit = 256;
        do {
            sinLambda = Math.sin(lambda);
            cosLambda = Math.cos(lambda);
            sinSigma =
                    Math.sqrt((cosU2 * sinLambda) * (cosU2 * sinLambda) + (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda)
                            * (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda));
            if (sinSigma == 0) {
                return 0; // co-incident points
            }
            cosSigma = sinU1 * sinU2 + cosU1 * cosU2 * cosLambda;
            sigma = Math.atan2(sinSigma, cosSigma);
            sinAlpha = cosU1 * cosU2 * sinLambda / sinSigma;
            cosSqAlpha = 1 - sinAlpha * sinAlpha;
            cos2SigmaM = cosSigma - 2 * sinU1 * sinU2 / cosSqAlpha;
            if (Double.isNaN(cos2SigmaM)) {
                cos2SigmaM = 0; // equatorial line: cosSqAlpha=0
            }
            double C = f / 16 * cosSqAlpha * (4 + f * (4 - 3 * cosSqAlpha));
            lambdaP = lambda;
            lambda =
                    L + (1 - C) * f * sinAlpha
                            * (sigma + C * sinSigma * (cos2SigmaM + C * cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM)));
        } while (Math.abs(lambda - lambdaP) > 1e-12 && --iterLimit > 0);

        if (iterLimit == 0) {
            return Double.NaN; // formula failed to converge
        }

        double uSq = cosSqAlpha * (a * a - b * b) / (b * b);
        double A = 1 + uSq / 16384 * (4096 + uSq * (-768 + uSq * (320 - 175 * uSq)));
        double B = uSq / 1024 * (256 + uSq * (-128 + uSq * (74 - 47 * uSq)));
        double deltaSigma =
                B
                        * sinSigma
                        * (cos2SigmaM + B
                        / 4
                        * (cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM) - B / 6 * cos2SigmaM
                        * (-3 + 4 * sinSigma * sinSigma) * (-3 + 4 * cos2SigmaM * cos2SigmaM)));
        double dist = b * A * (sigma - deltaSigma);

        return dist;
    }

    /**
     * Calculates approximate geodetic distance to another Position using Haversine formula.
     *
     * @returns distance in meters with approx. 0.5% precision
     */
    public double distanceToHaversine(Position otherPosition) {
        double lat1 = Math.toRadians(getLatitude());
        double lon1 = Math.toRadians(getLongitude());
        double lat2 = Math.toRadians(otherPosition.getLatitude());
        double lon2 = Math.toRadians(otherPosition.getLongitude());

        final double EARTH_RADIUS = 6371009; // mean radius in m

        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;

        double a = Math.pow((Math.sin(dlat / 2)), 2) + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(dlon / 2), 2);
        double c = 2 * Math.asin(Math.sqrt(a)); // Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }

}
