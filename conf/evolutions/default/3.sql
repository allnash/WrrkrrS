# --- !Ups

alter table shift add column processed tinyint(1) default 0 not null;
alter table shift add column when_received datetime(6) not null;

create table shift_report (
  id                            varchar(36) not null,
  deleted                       tinyint(1) default 0 not null,
  organization_id               varchar(36),
  by_id                         varchar(36),
  user_id                       varchar(36),
  clocked_hours                 integer,
  clocked_minutes               integer,
  for_date                      datetime(6) not null,
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_updated                  datetime(6) not null,
  constraint pk_shift_report primary key (id)
);

create index ix_shift_report_organization_id on shift_report (organization_id);
alter table shift_report add constraint fk_shift_report_organization_id foreign key (organization_id) references organization (id) on delete restrict on update restrict;

create index ix_shift_report_by_id on shift_report (by_id);
alter table shift_report add constraint fk_shift_report_by_id foreign key (by_id) references user (id) on delete restrict on update restrict;

create index ix_shift_report_user_id on shift_report (user_id);
alter table shift_report add constraint fk_shift_report_user_id foreign key (user_id) references user (id) on delete restrict on update restrict;


# --- !Downs

alter table shift_report drop foreign key fk_shift_report_organization_id;
drop index ix_shift_report_organization_id on shift_report;

alter table shift_report drop foreign key fk_shift_report_by_id;
drop index ix_shift_report_by_id on shift_report;

alter table shift_report drop foreign key fk_shift_report_user_id;
drop index ix_shift_report_user_id on shift_report;

drop table if exists shift_report;

