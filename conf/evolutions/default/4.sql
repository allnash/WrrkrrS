# --- !Ups

create table visit (
  id                            varchar(36) not null,
  deleted                       tinyint(1) default 0 not null,
  organization_id               varchar(36),
  by_id                         varchar(36),
  endpoint_device_id            varchar(36),
  user_device_id                varchar(36),
  user_id                       varchar(36),
  zone_id                       varchar(255),
  place_id                      varchar(36),
  floor_id                      varchar(36),
  visit_type                    integer not null,
  name                          varchar(255) not null,
  when_started                  datetime(6) not null,
  when_ended                    datetime(6),
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_updated                  datetime(6) not null,
  constraint ck_visit_visit_type check ( visit_type in (0,1,2,3)),
  constraint pk_visit primary key (id)
);

create index ix_visit_organization_id on visit (organization_id);
alter table visit add constraint fk_visit_organization_id foreign key (organization_id) references organization (id) on delete restrict on update restrict;

create index ix_visit_by_id on visit (by_id);
alter table visit add constraint fk_visit_by_id foreign key (by_id) references user (id) on delete restrict on update restrict;

create index ix_visit_endpoint_device_id on visit (endpoint_device_id);
alter table visit add constraint fk_visit_endpoint_device_id foreign key (endpoint_device_id) references device (id) on delete restrict on update restrict;

create index ix_visit_user_device_id on visit (user_device_id);
alter table visit add constraint fk_visit_user_device_id foreign key (user_device_id) references device (id) on delete restrict on update restrict;

create index ix_visit_user_id on visit (user_id);
alter table visit add constraint fk_visit_user_id foreign key (user_id) references user (id) on delete restrict on update restrict;

create index ix_visit_place_id on visit (place_id);
alter table visit add constraint fk_visit_place_id foreign key (place_id) references place (id) on delete restrict on update restrict;

create index ix_visit_floor_id on visit (floor_id);
alter table visit add constraint fk_visit_floor_id foreign key (floor_id) references floor (id) on delete restrict on update restrict;


# --- !Downs

alter table visit drop foreign key fk_visit_organization_id;
drop index ix_visit_organization_id on visit;

alter table visit drop foreign key fk_visit_by_id;
drop index ix_visit_by_id on visit;

alter table visit drop foreign key fk_visit_endpoint_device_id;
drop index ix_visit_endpoint_device_id on visit;

alter table visit drop foreign key fk_visit_user_device_id;
drop index ix_visit_user_device_id on visit;

alter table visit drop foreign key fk_visit_user_id;
drop index ix_visit_user_id on visit;

alter table visit drop foreign key fk_visit_place_id;
drop index ix_visit_place_id on visit;

alter table visit drop foreign key fk_visit_floor_id;
drop index ix_visit_floor_id on visit;

drop table if exists visit;


