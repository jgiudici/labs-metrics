package com.navent.labs.api.rest;

import com.navent.labs.dao.MetricsDao;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.ws.rs.Produces;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
public class MetricsREST {
    private static final Logger log = Logger.getLogger(MetricsREST.class);

    @Resource
    private MetricsDao metricsDao;


    @RequestMapping(value = "/v1/metrics/{countryid}/{appname}/{version}/{event}/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> hitUUIDForAnApp(@PathVariable String countryid, @PathVariable String appname, @PathVariable
    String version, @PathVariable String event, @PathVariable String id) throws Exception {
        metricsDao.hitAppNameWithUUID(countryid, appname, version, event, id);
        return new ResponseEntity<Object>(HttpStatus.OK);
    }
}

