package com.navent.labs.dao;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by jorgecollinet on 01/03/16.
 */
@Component
public class FactorySingletonSession implements Serializable {
    private List<String> ips;
    private String keySpace;
    private static transient Session session;
    private static Map<String, PreparedStatement> map = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(FactorySingletonSession.class);

    @Autowired
    public FactorySingletonSession(@Value("#{'${spring.cassandra.ips}'.split(',')}") List<String> ips,
                                   @Value("${spring.cassandra.keyspace}") String keySpace) {

        this.ips = ips;
        this.keySpace = keySpace;
    }

    public Session getSession() {
        return syncGetSession(keySpace, ips);
    }

    public BoundStatement getBoundStatement(String query) {
        return syncGetBoundStatement(this.getSession(), query);
    }

    public PreparedStatement getPreparedStatement (String query) {
        return syncGetPreparedStatement(this.getSession(), query);
    }

    private static synchronized Session syncGetSession(String keySpace, List<String> ips) {
        if (session == null) {
            session = createSession(keySpace, ips);
        }
        return session;
    }

    private static synchronized BoundStatement syncGetBoundStatement(Session session, String query) {
        PreparedStatement ps = map.get(query);
        if (ps == null) {
            log.info("FIRST PREPARED QUERY: " + query);
            ps = session.prepare(query);
            map.put(query, ps);
        }
        return ps.bind();
    }

    private static synchronized PreparedStatement syncGetPreparedStatement(Session session, String query) {
        PreparedStatement ps = map.get(query);
        if (ps == null) {
            log.info("FIRST PREPARED QUERY: " + query);
            ps = session.prepare(query);
            map.put(query, ps);
        }
        return ps;
    }

    private static Session createSession(String keySpace, List<String> ips) {
        log.info("CASSANDRA SESSION CREATED");
        return createCluster(ips).connect(keySpace); //TODO ver si necesito guardar el cluster
    }

    private static Cluster createCluster(List<String> ips) {
        List<InetAddress> inetAddresses = createCassandraContactPoints(ips);
        return Cluster.builder()
                .addContactPoints(inetAddresses)
                //.withPort(cassandraPort)
                .build();
    }

    private static List<InetAddress> createCassandraContactPoints(List<String> ips) {
        //see: https://navent.atlassian.net/browse/IDP-209
        return ips.stream().map(ip -> {
            try {
                return InetAddress.getByName(ip);
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
    }
}
