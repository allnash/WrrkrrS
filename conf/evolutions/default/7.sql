# --- !Ups


create table reader_visit_report (
  id                            varchar(36) not null,
  deleted                       tinyint(1) default 0 not null,
  organization_id               varchar(36),
  by_id                         varchar(36),
  reader_device_id              varchar(36),
  when_seen                     datetime(6),
  android_count                 integer not null,
  apple_count                   integer not null,
  others_count                  integer not null,
  devices_count                 integer not null,
  resolved_devices_count        integer not null,
  place_id                      varchar(36),
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_updated                  datetime(6) not null,
  constraint pk_reader_visit_report primary key (id)
);

alter table device add column oui_company_name varchar(128) default null;

create index ix_reader_visit_report_organization_id on reader_visit_report (organization_id);
alter table reader_visit_report add constraint fk_reader_visit_report_organization_id foreign key (organization_id) references organization (id) on delete restrict on update restrict;

create index ix_reader_visit_report_by_id on reader_visit_report (by_id);
alter table reader_visit_report add constraint fk_reader_visit_report_by_id foreign key (by_id) references user (id) on delete restrict on update restrict;

create index ix_reader_visit_report_reader_device_id on reader_visit_report (reader_device_id);
alter table reader_visit_report add constraint fk_reader_visit_report_reader_device_id foreign key (reader_device_id) references device (id) on delete restrict on update restrict;

create index ix_reader_visit_report_place_id on reader_visit_report (place_id);
alter table reader_visit_report add constraint fk_reader_visit_report_place_id foreign key (place_id) references place (id) on delete restrict on update restrict;

# --- !Downs


alter table reader_visit_report drop foreign key fk_reader_visit_report_organization_id;
drop index ix_reader_visit_report_organization_id on reader_visit_report;

alter table reader_visit_report drop foreign key fk_reader_visit_report_by_id;
drop index ix_reader_visit_report_by_id on reader_visit_report;

alter table reader_visit_report drop foreign key fk_reader_visit_report_reader_device_id;
drop index ix_reader_visit_report_reader_device_id on reader_visit_report;

alter table reader_visit_report drop foreign key fk_reader_visit_report_place_id;
drop index ix_reader_visit_report_place_id on reader_visit_report;

drop table if exists reader_visit_report;