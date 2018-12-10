# --- !Ups

alter table sighting add column visit_id varchar(36) default null;

create index ix_sighting_visit_id on sighting (visit_id);
alter table sighting add constraint fk_sighting_visit_id foreign key (visit_id) references visit (id) on delete restrict on update restrict;

# --- !Downs
