CREATE TABLE appmetrics.appstats (
	country text,
	appname text,
	version text,
	event text,
	date date,
	counter counter,
	PRIMARY KEY (country, appname, version, event, date)
);