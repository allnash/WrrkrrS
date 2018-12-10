# --- !Ups

alter table reader_visit_report add column  masked_devices_count integer default 0 not null after others_count;
alter table reader_visit_report add column  not_in_oui_count integer default 0 not null after others_count;
alter table reader_visit_report add column  networking_devices_count integer default 0 not null after others_count;
alter table reader_visit_report add column  laptop_count integer default 0 not null after others_count;


# --- !Downs

alter table reader_visit_report drop column laptop_count;
alter table reader_visit_report drop column networking_devices_count;
alter table reader_visit_report drop column masked_devices_count;
alter table reader_visit_report drop column not_in_oui_count;


