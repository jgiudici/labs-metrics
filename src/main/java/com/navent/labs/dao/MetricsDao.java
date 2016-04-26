package com.navent.labs.dao;

import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Created by chudi on 25/04/16.
 */
@Component
public class MetricsDao {

    private final Session session;
    private FactorySingletonSession factory;

    @Autowired
    public MetricsDao(FactorySingletonSession factory) {
        this.factory = factory;
        this.session = factory.getSession();
    }

    public void hitAppNameWithUUID(String countryid, String appname, String version, String event, String id) {
        if(!this.didThisUserMadeSomethingToThisAppToday(countryid, appname, version, event, id)){
            this.countActivityForThisUUID(countryid, appname, version, event, id);
            this.updateCounterOfThisApp(countryid, appname, version, event);
        }
    }


    private void countActivityForThisUUID(String countryid, String appname, String version, String event, String id) {
        //Get seconds till midnight
        DateTime now = new DateTime();
        DateTime midnight = now.withTimeAtStartOfDay();
        Duration duration = new Duration(midnight, now);
        int seconds = 86400 - duration.toStandardSeconds().getSeconds();

        String query = String.format("INSERT INTO appmetrics.uniqueusersbyday(id, country, appname, version, event) " +
                "VALUES('%s', '%s', '%s', '%s', '%s') USING TTL %d", id, countryid, appname, version , event, seconds);
        session.execute(query);
    }

    private void updateCounterOfThisApp(String countryid, String appname, String version, String event) {
        DateTime now = new DateTime();
        String query = String.format("UPDATE appmetrics.appstats SET counter = counter + 1 WHERE country = '%s' AND " +
                        "appname = '%s' AND version = '%s' AND event = '%s' AND date = '%s'",
                countryid, appname, version, event, now.toString("YYYY-MM-dd"));
        System.out.println(query);
        session.execute(query);
    }

    private boolean didThisUserMadeSomethingToThisAppToday(String countryid, String appname, String version, String event, String id) {
        String query = String.format("SELECT * FROM appmetrics.uniqueusersbyday WHERE " +
                "id = '%s' AND " +
                "country = '%s' AND " +
                "appname = '%s' AND version = '%s' AND event = '%s' ", id, countryid, appname, version, event);

        System.out.println(query);

        return !session.execute(query).all().isEmpty();
    }

}
