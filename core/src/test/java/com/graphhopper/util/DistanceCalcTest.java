/*
 *  Licensed to GraphHopper and Peter Karich under one or more contributor license 
 *  agreements. See the NOTICE file distributed with this work for 
 *  additional information regarding copyright ownership.
 * 
 *  GraphHopper licenses this file to you under the Apache License, 
 *  Version 2.0 (the "License"); you may not use this file except 
 *  in compliance with the License. You may obtain a copy of the 
 *  License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.graphhopper.util;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Peter Karich
 */
public class DistanceCalcTest {

    @Test
    public void testCalcCircumference() {
        assertEquals(DistanceCalc.C, new DistanceCalc().calcCircumference(0), 1e-7);
    }

    @Test
    public void testGeohashMaxDist() {
        assertEquals(DistanceCalc.C / 2, new DistanceCalc().calcSpatialKeyMaxDist(0), 1);
        assertEquals(DistanceCalc.C / 2, new DistanceCalc().calcSpatialKeyMaxDist(1), 1);
        assertEquals(DistanceCalc.C / 4, new DistanceCalc().calcSpatialKeyMaxDist(2), 1);
        assertEquals(DistanceCalc.C / 4, new DistanceCalc().calcSpatialKeyMaxDist(3), 1);
        assertEquals(DistanceCalc.C / 8, new DistanceCalc().calcSpatialKeyMaxDist(4), 1);
        assertEquals(DistanceCalc.C / 8, new DistanceCalc().calcSpatialKeyMaxDist(5), 1);
    }

    @Test
    public void testDistance() {
        float lat = 24.235f;
        float lon = 47.234f;
        DistanceCalc approxDist = new DistancePlaneProjection();
        DistanceCalc dist = new DistanceCalc();
        double res = 15051;
        assertEquals(res, dist.calcDist(lat, lon, lat - 0.1, lon + 0.1), 1);
        assertEquals(dist.calcNormalizedDist(res), dist.calcNormalizedDist(lat, lon, lat - 0.1, lon + 0.1), 1);
        assertEquals(res, approxDist.calcDist(lat, lon, lat - 0.1, lon + 0.1), 1);

        res = 15046;
        assertEquals(res, dist.calcDist(lat, lon, lat + 0.1, lon - 0.1), 1);
        assertEquals(dist.calcNormalizedDist(res), dist.calcNormalizedDist(lat, lon, lat + 0.1, lon - 0.1), 1);
        assertEquals(res, approxDist.calcDist(lat, lon, lat + 0.1, lon - 0.1), 1);

        res = 150748;
        assertEquals(res, dist.calcDist(lat, lon, lat - 1, lon + 1), 1);
        assertEquals(dist.calcNormalizedDist(res), dist.calcNormalizedDist(lat, lon, lat - 1, lon + 1), 1);
        assertEquals(res, approxDist.calcDist(lat, lon, lat - 1, lon + 1), 10);

        res = 150211;
        assertEquals(res, dist.calcDist(lat, lon, lat + 1, lon - 1), 1);
        assertEquals(dist.calcNormalizedDist(res), dist.calcNormalizedDist(lat, lon, lat + 1, lon - 1), 1);
        assertEquals(res, approxDist.calcDist(lat, lon, lat + 1, lon - 1), 10);

        res = 1527919;
        assertEquals(res, dist.calcDist(lat, lon, lat - 10, lon + 10), 1);
        assertEquals(dist.calcNormalizedDist(res), dist.calcNormalizedDist(lat, lon, lat - 10, lon + 10), 1);
        assertEquals(res, approxDist.calcDist(lat, lon, lat - 10, lon + 10), 10000);

        res = 1474016;
        assertEquals(res, dist.calcDist(lat, lon, lat + 10, lon - 10), 1);
        assertEquals(dist.calcNormalizedDist(res), dist.calcNormalizedDist(lat, lon, lat + 10, lon - 10), 1);
        assertEquals(res, approxDist.calcDist(lat, lon, lat + 10, lon - 10), 10000);

        res = 1013735.28;
        assertEquals(res, dist.calcDist(lat, lon, lat, lon - 10), 1);
        assertEquals(dist.calcNormalizedDist(res), dist.calcNormalizedDist(lat, lon, lat, lon - 10), 1);
        // 1013952.659
        assertEquals(res, approxDist.calcDist(lat, lon, lat, lon - 10), 1000);

        // if we have a big distance for latitude only then PlaneProjection is exact!!
        res = 1111949.3;
        assertEquals(res, dist.calcDist(lat, lon, lat + 10, lon), 1);
        assertEquals(dist.calcNormalizedDist(res), dist.calcNormalizedDist(lat, lon, lat + 10, lon), 1);
        assertEquals(res, approxDist.calcDist(lat, lon, lat + 10, lon), 1);
    }

    @Test
    public void testEdgeDistance() {
        DistanceCalc calc = new DistanceCalc();
        double dist = calc.calcNormalizedEdgeDistance(49.94241, 11.544356,
                49.937964, 11.541824,
                49.942272, 11.555643);
        double expectedDist = calc.calcNormalizedDist(49.94241, 11.544356,
                49.9394, 11.54681);
        assertEquals(expectedDist, dist, 1e-4);

        // test identical lats
        dist = calc.calcNormalizedEdgeDistance(49.936299, 11.543992,
                49.9357, 11.543047,
                49.9357, 11.549227);
        expectedDist = calc.calcNormalizedDist(49.936299, 11.543992,
                49.9357, 11.543992);
        assertEquals(expectedDist, dist, 1e-4);
    }

    @Test
    public void testValidEdgeDistance() {
        DistanceCalc calc = new DistanceCalc();
        assertTrue(calc.validEdgeDistance(49.94241, 11.544356, 49.937964, 11.541824, 49.942272, 11.555643));
        assertTrue(calc.validEdgeDistance(49.936624, 11.547636, 49.937964, 11.541824, 49.942272, 11.555643));
        assertTrue(calc.validEdgeDistance(49.940712, 11.556069, 49.937964, 11.541824, 49.942272, 11.555643));

        // left bottom of the edge
        assertFalse(calc.validEdgeDistance(49.935119, 11.541649, 49.937964, 11.541824, 49.942272, 11.555643));
        // left top of the edge
        assertFalse(calc.validEdgeDistance(49.939317, 11.539675, 49.937964, 11.541824, 49.942272, 11.555643));
        // right top of the edge
        assertFalse(calc.validEdgeDistance(49.944482, 11.555446, 49.937964, 11.541824, 49.942272, 11.555643));
        // right bottom of the edge
        assertFalse(calc.validEdgeDistance(49.94085, 11.557356, 49.937964, 11.541824, 49.942272, 11.555643));
    }
}
