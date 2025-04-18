/*
Insert some test events
*/

insert into central_outbox.outbox_event_type (id, event_type, scheduled_cron,queue_name, description,row_created_by)
values (uuid_in(md5(random()::text || clock_timestamp()::text)::cstring),'RECALCULATE_REPORT_FOR_ACCOUNT' ,'0 */3 * * * *','recalculate-account-reports-sqs','Recalculate Report For Account','c465522a-1320-11ed-861d-0242ac12000');

insert into central_outbox.outbox_event_type (id, event_type, scheduled_cron,queue_name, description,row_created_by)
values (uuid_in(md5(random()::text || clock_timestamp()::text)::cstring),'REPOPULATE_PROJECTIONS_FOR_ACCOUNT' ,'0 */4 * * * *','populate-account-projection-sqs','Repopulate Projections By Account Id','c465522a-1320-11ed-861d-0242ac12000');
