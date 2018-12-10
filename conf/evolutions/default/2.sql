# --- !Ups

create table shift (
  id                            varchar(36) not null,
  deleted                       tinyint(1) default 0 not null,
  organization_id               varchar(36),
  by_id                         varchar(36),
  device_id                     varchar(36),
  user_id                       varchar(36),
  lat                           varchar(255),
  lon                           varchar(255),
  event                         integer,
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_updated                  datetime(6) not null,
  constraint ck_shift_event check ( event in (0,1)),
  constraint pk_shift primary key (id)
);

create index ix_shift_organization_id on shift (organization_id);
alter table shift add constraint fk_shift_organization_id foreign key (organization_id) references organization (id) on delete restrict on update restrict;

create index ix_shift_by_id on shift (by_id);
alter table shift add constraint fk_shift_by_id foreign key (by_id) references user (id) on delete restrict on update restrict;

create index ix_shift_device_id on shift (device_id);
alter table shift add constraint fk_shift_device_id foreign key (device_id) references device (id) on delete restrict on update restrict;

create index ix_shift_user_id on shift (user_id);
alter table shift add constraint fk_shift_user_id foreign key (user_id) references user (id) on delete restrict on update restrict;


# --- !Downs

alter table shift drop foreign key fk_shift_organization_id;
drop index ix_shift_organization_id on shift;

alter table shift drop foreign key fk_shift_by_id;
drop index ix_shift_by_id on shift;

alter table shift drop foreign key fk_shift_device_id;
drop index ix_shift_device_id on shift;

alter table shift drop foreign key fk_shift_user_id;
drop index ix_shift_user_id on shift;

drop table if exists shift;

