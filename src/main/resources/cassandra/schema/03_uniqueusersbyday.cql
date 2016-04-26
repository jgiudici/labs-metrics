CREATE TABLE appmetrics.uniqueusersbyday (
	id text,
	country text,
	appname text,
	version text,
	event text,
	PRIMARY KEY (id, country, appname, version, event)
);
