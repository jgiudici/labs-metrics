CREATE KEYSPACE appmetrics
WITH replication = {
	'class' : 'SimpleStrategy',
	'replication_factor' : 1
};
