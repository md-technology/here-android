package com.mdtech.social.api;

import com.mdtech.social.api.json.Travel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by any on 2014/10/31.
 */
public class TravelOperationsTest extends AbstractOperationsTest {

    private Log log = LogFactory.getLog(TravelOperationsTest.class);

    private TravelOperations travelOperations;

    @Before
    public void setup() {
        super.setup();
        super.authenticateClient();
        travelOperations = connectionRepository.findPrimaryConnection(Ponmap.class).getApi().travelOperations();
    }

    @Test
    public void testGet() {
        Long travelId = 3L;

        Travel travel = travelOperations.getTravel(travelId);

        assertNotNull(travel);
    }
}
