package com.navent.labs.model;

import com.google.common.base.Stopwatch;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Date;

/**
 * Created by chudi on 25/04/16.
 */
public class MetricData {
    private String metric;
    private LocalDate time = LocalDate.now();

    public MetricData(String metric) {
        this.metric = metric;
    }

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }


}
