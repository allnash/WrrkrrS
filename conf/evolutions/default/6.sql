# --- !Ups

alter table device add column current_state_id varchar(36) default null;

alter table device add column current_heartbeat_id varchar(36) default null;


# --- !Downs
