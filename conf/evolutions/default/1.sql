# --- !Ups

create table activity (
  id                            varchar(36) not null,
  deleted                       tinyint(1) default 0 not null,
  by_id                         varchar(36),
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_updated                  datetime(6) not null,
  constraint pk_activity primary key (id)
);

create table activity_type (
  id                            varchar(36) not null,
  deleted                       tinyint(1) default 0 not null,
  name                          varchar(255),
  description                   varchar(255),
  image_id                      varchar(36),
  available_publicly            tinyint(1),
  organization_id               varchar(36),
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_updated                  datetime(6) not null,
  constraint uq_activity_type_image_id unique (image_id),
  constraint pk_activity_type primary key (id)
);

create table data_migration (
  id                            varchar(255) not null,
  name                          varchar(255),
  description                   varchar(255),
  meta_data                     varchar(255),
  start_date                    datetime(6),
  completed_date                datetime(6),
  migration_state               integer,
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_updated                  datetime(6) not null,
  constraint ck_data_migration_migration_state check ( migration_state in (0,1)),
  constraint pk_data_migration primary key (id)
);

create table device (
  id                            varchar(36) not null,
  deleted                       tinyint(1) default 0 not null,
  organization_id               varchar(36),
  by_id                         varchar(36),
  external_id                   varchar(255),
  mac_address                   varchar(255),
  owner_id                      varchar(36),
  manufacturer_id               varchar(36),
  current_place_id              varchar(36),
  name                          varchar(128) not null,
  device_secret                 varchar(255),
  device_type_id                varchar(36),
  enabled                       tinyint(1) not null,
  version_number                varchar(64),
  model                         varchar(64),
  software_id                   varchar(36),
  currentbattery_level          varchar(255),
  maximum_battery_level         varchar(255),
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_updated                  datetime(6) not null,
  constraint uq_device_current_place_id unique (current_place_id),
  constraint uq_device_organization_id_id unique (organization_id,id),
  constraint pk_device primary key (id)
);

create table device_place (
  device_id                     varchar(36) not null,
  place_id                      varchar(36) not null,
  constraint pk_device_place primary key (device_id,place_id)
);

create table device_collaborator_organizations (
  device_id                     varchar(36) not null,
  collaborator_organization_id  varchar(36) not null,
  constraint pk_device_collaborator_organizations primary key (device_id,collaborator_organization_id)
);

create table device_collaborator_users (
  device_id                     varchar(36) not null,
  user_id                       varchar(36) not null,
  constraint pk_device_collaborator_users primary key (device_id,user_id)
);

create table device_type (
  id                            varchar(36) not null,
  deleted                       tinyint(1) default 0 not null,
  organization_id               varchar(36),
  by_id                         varchar(36),
  name                          varchar(255) not null,
  description                   varchar(255),
  label_class                   varchar(255),
  image_id                      varchar(36),
  visibility                    integer not null,
  html_id                       varchar(36),
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_updated                  datetime(6) not null,
  constraint ck_device_type_visibility check ( visibility in (0,1,2,3)),
  constraint uq_device_type_image_id unique (image_id),
  constraint uq_device_type_organization_id_name unique (organization_id,name),
  constraint pk_device_type primary key (id)
);

create table device_type_properties (
  device_type_id                varchar(36) not null,
  device_type_property_id       varchar(36) not null,
  constraint pk_device_type_properties primary key (device_type_id,device_type_property_id)
);

create table device_type_property (
  id                            varchar(36) not null,
  deleted                       tinyint(1) default 0 not null,
  organization_id               varchar(36),
  by_id                         varchar(36),
  name                          varchar(255) not null,
  description                   varchar(255),
  label_class                   varchar(255),
  image_id                      varchar(36),
  visibility                    integer not null,
  device_type_property_type_id  varchar(36),
  device_type_property_status_id varchar(36),
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_updated                  datetime(6) not null,
  constraint ck_device_type_property_visibility check ( visibility in (0,1,2,3)),
  constraint uq_device_type_property_image_id unique (image_id),
  constraint uq_device_type_property_organization_id_name unique (organization_id,name),
  constraint pk_device_type_property primary key (id)
);

create table device_type_property_status (
  id                            varchar(36) not null,
  deleted                       tinyint(1) default 0 not null,
  organization_id               varchar(36),
  by_id                         varchar(36),
  name                          varchar(255) not null,
  description                   varchar(255),
  label_class                   varchar(255),
  element_class                 varchar(255),
  image_id                      varchar(36),
  visibility                    integer not null,
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_updated                  datetime(6) not null,
  constraint ck_device_type_property_status_visibility check ( visibility in (0,1,2,3)),
  constraint uq_device_type_property_status_image_id unique (image_id),
  constraint uq_device_type_property_status_organization_id_name unique (organization_id,name),
  constraint pk_device_type_property_status primary key (id)
);

create table device_type_property_type (
  id                            varchar(36) not null,
  deleted                       tinyint(1) default 0 not null,
  organization_id               varchar(36),
  by_id                         varchar(36),
  name                          varchar(255) not null,
  description                   varchar(255),
  label_class                   varchar(255),
  image_id                      varchar(36),
  visibility                    integer not null,
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_updated                  datetime(6) not null,
  constraint ck_device_type_property_type_visibility check ( visibility in (0,1,2,3)),
  constraint uq_device_type_property_type_image_id unique (image_id),
  constraint uq_device_type_property_type_organization_id_name unique (organization_id,name),
  constraint pk_device_type_property_type primary key (id)
);

create table floor (
  id                            varchar(36) not null,
  deleted                       tinyint(1) default 0 not null,
  organization_id               varchar(36),
  by_id                         varchar(36),
  name                          varchar(255),
  local_id                      integer,
  image_id                      varchar(36),
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_updated                  datetime(6) not null,
  constraint uq_floor_image_id unique (image_id),
  constraint pk_floor primary key (id)
);

create table floor_device (
  floor_id                      varchar(36) not null,
  device_id                     varchar(36) not null,
  constraint pk_floor_device primary key (floor_id,device_id)
);

create table floor_floor_tag (
  floor_id                      varchar(36) not null,
  floor_tag_text                varchar(255) not null,
  constraint pk_floor_floor_tag primary key (floor_id,floor_tag_text)
);

create table floor_tag (
  text                          varchar(255) not null,
  organization_id               varchar(36),
  when_created                  datetime(6) not null,
  constraint pk_floor_tag primary key (text)
);

create table history (
  id                            varchar(255) not null,
  entity_id                     varchar(255),
  entity_version                bigint,
  entity_class                  varchar(255),
  data_before                   LONGTEXT,
  data_after                    LONGTEXT,
  when_created                  datetime(6) not null,
  when_updated                  datetime(6) not null,
  constraint pk_history primary key (id)
);

create table html (
  id                            varchar(36) not null,
  deleted                       tinyint(1) default 0 not null,
  organization_id               varchar(36),
  by_id                         varchar(36),
  data                          MEDIUMTEXT,
  notes                         varchar(255),
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_updated                  datetime(6) not null,
  constraint pk_html primary key (id)
);

create table license (
  id                            varchar(36) not null,
  deleted                       tinyint(1) default 0 not null,
  max_messages                  integer,
  name                          varchar(64) not null,
  status                        integer,
  is_client                     tinyint(1),
  renewed_time                  datetime(6),
  expires_time                  datetime(6),
  dashboard_module_access       tinyint(1),
  devices_module_access         tinyint(1),
  projects_module_access        tinyint(1),
  workflow_module_access        tinyint(1),
  issues_module_access          tinyint(1),
  collaborator_module_access    tinyint(1),
  teams_module_access           tinyint(1),
  analytics_module_access       tinyint(1),
  members_module_access         tinyint(1),
  messages_module_access        tinyint(1),
  marketplace_module_access     tinyint(1),
  developer_module_access       tinyint(1),
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_updated                  datetime(6) not null,
  constraint ck_license_status check ( status in (0,1,2)),
  constraint pk_license primary key (id)
);

create table message (
  id                            varchar(36) not null,
  deleted                       tinyint(1) default 0 not null,
  external_id                   varchar(255),
  subject                       varchar(255),
  description_id                varchar(36),
  when_read                     datetime(6),
  message_type                  integer,
  from_user_id                  varchar(36),
  to_user_id                    varchar(36),
  message_read_state            integer,
  message_state                 integer,
  starred                       tinyint(1) default 0 not null,
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_updated                  datetime(6) not null,
  constraint ck_message_message_type check ( message_type in (0,1,2,3,4,5,6,7,8)),
  constraint ck_message_message_read_state check ( message_read_state in (0,1)),
  constraint ck_message_message_state check ( message_state in (0,1,2)),
  constraint uq_message_description_id unique (description_id),
  constraint pk_message primary key (id)
);

create table organization (
  id                            varchar(36) not null,
  deleted                       tinyint(1) default 0 not null,
  external_id                   varchar(255),
  name                          varchar(255) not null,
  email_domain                  varchar(64),
  workspace_name                varchar(64),
  place_id                      varchar(36),
  enabled                       tinyint(1),
  approved                      tinyint(1),
  self_service_signup           tinyint(1),
  slack_hook                    varchar(511),
  license_id                    varchar(36),
  by_id                         varchar(36),
  approved_by_id                varchar(36),
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_updated                  datetime(6) not null,
  constraint uq_organization_email_domain unique (email_domain),
  constraint uq_organization_workspace_name unique (workspace_name),
  constraint uq_organization_place_id unique (place_id),
  constraint uq_organization_license_id unique (license_id),
  constraint pk_organization primary key (id)
);

create table collaborator_organizations (
  organization_id               varchar(36) not null,
  collaborator_organization_id  varchar(36) not null,
  constraint pk_collaborator_organizations primary key (organization_id,collaborator_organization_id)
);

create table organization_key (
  id                            varchar(255) not null,
  organization_id               varchar(36),
  aes_key                       varchar(255) not null,
  deleted                       tinyint(1) default 0 not null,
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_updated                  datetime(6) not null,
  constraint uq_organization_key_organization_id unique (organization_id),
  constraint pk_organization_key primary key (id)
);

create table place (
  id                            varchar(36) not null,
  deleted                       tinyint(1) default 0 not null,
  organization_id               varchar(36),
  by_id                         varchar(36),
  name                          varchar(255),
  address                       varchar(255),
  city                          varchar(255),
  state                         varchar(255),
  telephone_number              varchar(255),
  zip                           varchar(255),
  place_type_id                 varchar(36),
  lat                           varchar(255),
  lon                           varchar(255),
  cartesian_x                   integer not null,
  cartesian_y                   integer not null,
  floor_id                      varchar(36),
  user_id                       varchar(36),
  notes                         varchar(255),
  verified                      tinyint(1) default 0 not null,
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_updated                  datetime(6) not null,
  constraint pk_place primary key (id)
);

create table place_type (
  id                            varchar(36) not null,
  deleted                       tinyint(1) default 0 not null,
  organization_id               varchar(36),
  by_id                         varchar(36),
  name                          varchar(255) not null,
  description                   varchar(255),
  label_class                   varchar(255),
  image_id                      varchar(36),
  visibility                    integer not null,
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_updated                  datetime(6) not null,
  constraint ck_place_type_visibility check ( visibility in (0,1,2,3)),
  constraint uq_place_type_image_id unique (image_id),
  constraint uq_place_type_organization_id_name unique (organization_id,name),
  constraint pk_place_type primary key (id)
);

create table product (
  id                            varchar(36) not null,
  deleted                       tinyint(1) default 0 not null,
  name                          varchar(255),
  description                   varchar(255),
  notes                         varchar(255),
  verified                      tinyint(1),
  enabled                       tinyint(1),
  by_id                         varchar(36),
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_updated                  datetime(6) not null,
  constraint pk_product primary key (id)
);

create table product_tag (
  product_id                    varchar(36) not null,
  tag_text                      varchar(150) not null,
  constraint pk_product_tag primary key (product_id,tag_text)
);

create table product_release (
  id                            varchar(36) not null,
  deleted                       tinyint(1) default 0 not null,
  download_url                  varchar(255),
  product_id                    varchar(36),
  name                          varchar(64) not null,
  version_x                     integer not null,
  version_y                     integer not null,
  version_z                     integer not null,
  beta                          tinyint(1) default 0 not null,
  shipped                       tinyint(1) default 0 not null,
  upgrade_id                    varchar(36),
  by_id                         varchar(36),
  html_id                       varchar(36),
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_updated                  datetime(6) not null,
  when_shipped                  datetime(6) not null,
  constraint uq_product_release_upgrade_id unique (upgrade_id),
  constraint uq_product_release_html_id unique (html_id),
  constraint uq_product_release_product_id_version_x_version_y_version_z unique (product_id,version_x,version_y,version_z),
  constraint pk_product_release primary key (id)
);

create table product_type (
  id                            varchar(36) not null,
  deleted                       tinyint(1) default 0 not null,
  organization_id               varchar(36),
  by_id                         varchar(36),
  name                          varchar(255) not null,
  description                   varchar(255),
  label_class                   varchar(255),
  image_id                      varchar(36),
  visibility                    integer not null,
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_updated                  datetime(6) not null,
  constraint ck_product_type_visibility check ( visibility in (0,1,2,3)),
  constraint uq_product_type_image_id unique (image_id),
  constraint uq_product_type_organization_id_name unique (organization_id,name),
  constraint pk_product_type primary key (id)
);

create table project (
  id                            varchar(36) not null,
  deleted                       tinyint(1) default 0 not null,
  organization_id               varchar(36),
  by_id                         varchar(36),
  name                          varchar(255),
  local_id                      integer,
  description_id                varchar(36),
  client_id                     varchar(36),
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_updated                  datetime(6) not null,
  constraint uq_project_description_id unique (description_id),
  constraint pk_project primary key (id)
);

create table project_project_cycle (
  project_id                    varchar(36) not null,
  project_cycle_id              varchar(36) not null,
  constraint pk_project_project_cycle primary key (project_id,project_cycle_id)
);

create table project_products (
  project_id                    varchar(36) not null,
  product_id                    varchar(36) not null,
  constraint pk_project_products primary key (project_id,product_id)
);

create table project_project_tag (
  project_id                    varchar(36) not null,
  project_tag_text              varchar(255) not null,
  constraint pk_project_project_tag primary key (project_id,project_tag_text)
);

create table project_cycle (
  id                            varchar(36) not null,
  deleted                       tinyint(1) default 0 not null,
  with_deadline                 datetime(6),
  total_budget                  double not null,
  total_budgeted_hours          double not null,
  total_spent_hours             double not null,
  notes                         varchar(255),
  verified                      tinyint(1),
  enabled                       tinyint(1),
  current                       tinyint(1),
  type_id                       varchar(36),
  status_id                     varchar(36),
  priority_id                   varchar(36),
  organization_id               varchar(36),
  by_id                         varchar(36),
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_updated                  datetime(6) not null,
  constraint pk_project_cycle primary key (id)
);

create table project_cycle_tasks (
  project_cycle_id              varchar(36) not null,
  task_id                       varchar(36) not null,
  constraint pk_project_cycle_tasks primary key (project_cycle_id,task_id)
);

create table project_cycle_participants (
  project_cycle_id              varchar(36) not null,
  user_id                       varchar(36) not null,
  constraint pk_project_cycle_participants primary key (project_cycle_id,user_id)
);

create table project_priority (
  id                            varchar(36) not null,
  deleted                       tinyint(1) default 0 not null,
  organization_id               varchar(36),
  by_id                         varchar(36),
  name                          varchar(255) not null,
  description                   varchar(255),
  label_class                   varchar(255),
  i_class                       varchar(255),
  element_class                 varchar(255),
  image_id                      varchar(36),
  available_publicly            tinyint(1),
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_updated                  datetime(6) not null,
  constraint uq_project_priority_image_id unique (image_id),
  constraint uq_project_priority_organization_id_name unique (organization_id,name),
  constraint pk_project_priority primary key (id)
);

create table project_status (
  id                            varchar(36) not null,
  deleted                       tinyint(1) default 0 not null,
  organization_id               varchar(36),
  by_id                         varchar(36),
  name                          varchar(255) not null,
  description                   varchar(255),
  label_class                   varchar(255),
  element_class                 varchar(255),
  image_id                      varchar(36),
  visibility                    integer not null,
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_updated                  datetime(6) not null,
  constraint ck_project_status_visibility check ( visibility in (0,1,2,3)),
  constraint uq_project_status_image_id unique (image_id),
  constraint uq_project_status_organization_id_name unique (organization_id,name),
  constraint pk_project_status primary key (id)
);

create table project_tag (
  text                          varchar(255) not null,
  organization_id               varchar(36),
  when_created                  datetime(6) not null,
  constraint pk_project_tag primary key (text)
);

create table project_type (
  id                            varchar(36) not null,
  deleted                       tinyint(1) default 0 not null,
  organization_id               varchar(36),
  by_id                         varchar(36),
  name                          varchar(255) not null,
  description                   varchar(255),
  label_class                   varchar(255),
  image_id                      varchar(36),
  visibility                    integer not null,
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_updated                  datetime(6) not null,
  constraint ck_project_type_visibility check ( visibility in (0,1,2,3)),
  constraint uq_project_type_image_id unique (image_id),
  constraint uq_project_type_organization_id_name unique (organization_id,name),
  constraint pk_project_type primary key (id)
);

create table role (
  id                            varchar(36) not null,
  deleted                       tinyint(1) default 0 not null,
  organization_id               varchar(36),
  by_id                         varchar(36),
  name                          varchar(255),
  description                   varchar(255),
  role_image_id                 varchar(36),
  available_publicly            tinyint(1),
  dashboard_module_access       integer,
  devices_module_access         integer,
  projects_module_access        integer,
  workflow_module_access        integer,
  issues_module_access          integer,
  collaborator_module_access    integer,
  teams_module_access           integer,
  analytics_module_access       integer,
  members_module_access         integer,
  messages_module_access        integer,
  marketplace_module_access     integer,
  developer_module_access       integer,
  default_route                 varchar(255),
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_updated                  datetime(6) not null,
  constraint ck_role_dashboard_module_access check ( dashboard_module_access in (0,1,2)),
  constraint ck_role_devices_module_access check ( devices_module_access in (0,1,2)),
  constraint ck_role_projects_module_access check ( projects_module_access in (0,1,2)),
  constraint ck_role_workflow_module_access check ( workflow_module_access in (0,1,2)),
  constraint ck_role_issues_module_access check ( issues_module_access in (0,1,2)),
  constraint ck_role_collaborator_module_access check ( collaborator_module_access in (0,1,2)),
  constraint ck_role_teams_module_access check ( teams_module_access in (0,1,2)),
  constraint ck_role_analytics_module_access check ( analytics_module_access in (0,1,2)),
  constraint ck_role_members_module_access check ( members_module_access in (0,1,2)),
  constraint ck_role_messages_module_access check ( messages_module_access in (0,1,2)),
  constraint ck_role_marketplace_module_access check ( marketplace_module_access in (0,1,2)),
  constraint ck_role_developer_module_access check ( developer_module_access in (0,1,2)),
  constraint uq_role_organization_id_name unique (organization_id,name),
  constraint pk_role primary key (id)
);

create table session (
  id                            varchar(36) not null,
  deleted                       tinyint(1) default 0 not null,
  organization_id               varchar(36),
  by_id                         varchar(36),
  email                         varchar(255),
  ip_address                    varchar(255),
  session_id                    varchar(255),
  is_two_factored               tinyint(1),
  app                           varchar(255),
  expires_at                    datetime(6),
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_updated                  datetime(6) not null,
  constraint pk_session primary key (id)
);

create table settings (
  id                            varchar(36) not null,
  deleted                       tinyint(1) default 0 not null,
  organization_id               varchar(36),
  by_id                         varchar(36),
  email_notification            integer,
  daily_activity_email          integer,
  in_app_notification           integer,
  flow_notification             integer,
  device_notification           integer,
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_updated                  datetime(6) not null,
  constraint pk_settings primary key (id)
);

create table sighting (
  id                            varchar(36) not null,
  deleted                       tinyint(1) default 0 not null,
  organization_id               varchar(36),
  by_id                         varchar(36),
  reader_device_id              varchar(36),
  sighted_device_id             varchar(36),
  sighted_user_id               varchar(36),
  rssi                          varchar(255),
  k_rssi                        varchar(255),
  distance                      varchar(255),
  k_distance                    varchar(255),
  temperature                   varchar(255),
  battery_level                 varchar(255),
  when_seen                     datetime(6),
  floor_id                      varchar(36),
  processed                     tinyint(1) default 0 not null,
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_updated                  datetime(6) not null,
  constraint pk_sighting primary key (id)
);

create table tag (
  text                          varchar(150) not null,
  when_created                  datetime(6) not null,
  constraint pk_tag primary key (text)
);

create table task (
  id                            varchar(36) not null,
  deleted                       tinyint(1) default 0 not null,
  organization_id               varchar(36),
  by_id                         varchar(36),
  description                   TEXT,
  detail_id                     varchar(36),
  notes                         varchar(255),
  status_id                     varchar(36),
  type_id                       varchar(36),
  priority_id                   varchar(36),
  risk_id                       varchar(36),
  verified                      tinyint(1),
  budgeted_hours                double not null,
  spent_hours                   double not null,
  enabled                       tinyint(1),
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_updated                  datetime(6) not null,
  constraint uq_task_detail_id unique (detail_id),
  constraint pk_task primary key (id)
);

create table task_participants (
  task_id                       varchar(36) not null,
  user_id                       varchar(36) not null,
  constraint pk_task_participants primary key (task_id,user_id)
);

create table task_priority (
  id                            varchar(36) not null,
  deleted                       tinyint(1) default 0 not null,
  organization_id               varchar(36),
  by_id                         varchar(36),
  name                          varchar(255) not null,
  description                   varchar(255),
  label_class                   varchar(255),
  i_class                       varchar(255),
  element_class                 varchar(255),
  image_id                      varchar(36),
  available_publicly            tinyint(1),
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_updated                  datetime(6) not null,
  constraint uq_task_priority_image_id unique (image_id),
  constraint uq_task_priority_organization_id_name unique (organization_id,name),
  constraint pk_task_priority primary key (id)
);

create table task_risk (
  id                            varchar(36) not null,
  deleted                       tinyint(1) default 0 not null,
  organization_id               varchar(36),
  by_id                         varchar(36),
  name                          varchar(255) not null,
  description                   varchar(255),
  label_class                   varchar(255),
  i_class                       varchar(255),
  element_class                 varchar(255),
  image_id                      varchar(36),
  available_publicly            tinyint(1),
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_updated                  datetime(6) not null,
  constraint uq_task_risk_image_id unique (image_id),
  constraint uq_task_risk_organization_id_name unique (organization_id,name),
  constraint pk_task_risk primary key (id)
);

create table task_status (
  id                            varchar(36) not null,
  deleted                       tinyint(1) default 0 not null,
  organization_id               varchar(36),
  by_id                         varchar(36),
  name                          varchar(255) not null,
  description                   varchar(255),
  label_class                   varchar(255),
  element_class                 varchar(255),
  image_id                      varchar(36),
  visibility                    integer not null,
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_updated                  datetime(6) not null,
  constraint ck_task_status_visibility check ( visibility in (0,1,2,3)),
  constraint uq_task_status_image_id unique (image_id),
  constraint uq_task_status_organization_id_name unique (organization_id,name),
  constraint pk_task_status primary key (id)
);

create table task_type (
  id                            varchar(36) not null,
  deleted                       tinyint(1) default 0 not null,
  organization_id               varchar(36),
  by_id                         varchar(36),
  name                          varchar(255) not null,
  description                   varchar(255),
  label_class                   varchar(255),
  image_id                      varchar(36),
  visibility                    integer not null,
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_updated                  datetime(6) not null,
  constraint ck_task_type_visibility check ( visibility in (0,1,2,3)),
  constraint uq_task_type_image_id unique (image_id),
  constraint uq_task_type_organization_id_name unique (organization_id,name),
  constraint pk_task_type primary key (id)
);

create table type_image (
  id                            varchar(36) not null,
  deleted                       tinyint(1) default 0 not null,
  organization_id               varchar(36),
  by_id                         varchar(36),
  url                           varchar(255),
  w                             integer,
  h                             integer,
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_updated                  datetime(6) not null,
  constraint pk_type_image primary key (id)
);

create table user (
  id                            varchar(36) not null,
  deleted                       tinyint(1) default 0 not null,
  email                         varbinary(255),
  strong_password               varchar(512),
  first_name                    varchar(255),
  external_id                   varchar(255),
  last_name                     varchar(255),
  phone_number                  varchar(64),
  bio                           varchar(512),
  totpkey                       varchar(255),
  totpconfigured                tinyint(1),
  country_code                  varchar(6),
  image_id                      varchar(36),
  confirmation_hash             varchar(255),
  confirmed                     tinyint(1),
  reset_token                   varchar(255),
  place_id                      varchar(36),
  pin                           varchar(255),
  organization_id               varchar(36),
  role_id                       varchar(36),
  settings_id                   varchar(36),
  enabled                       tinyint(1),
  is_admin                      tinyint(1),
  is_superadmin                 tinyint(1),
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_updated                  datetime(6) not null,
  constraint uq_user_image_id unique (image_id),
  constraint uq_user_place_id unique (place_id),
  constraint uq_user_settings_id unique (settings_id),
  constraint pk_user primary key (id)
);

create index ix_project_tag_text on project_tag (text);
create index ix_tag_text on tag (text);
create index ix_activity_by_id on activity (by_id);
alter table activity add constraint fk_activity_by_id foreign key (by_id) references user (id) on delete restrict on update restrict;

alter table activity_type add constraint fk_activity_type_image_id foreign key (image_id) references type_image (id) on delete restrict on update restrict;

create index ix_activity_type_organization_id on activity_type (organization_id);
alter table activity_type add constraint fk_activity_type_organization_id foreign key (organization_id) references organization (id) on delete restrict on update restrict;

create index ix_device_organization_id on device (organization_id);
alter table device add constraint fk_device_organization_id foreign key (organization_id) references organization (id) on delete restrict on update restrict;

create index ix_device_by_id on device (by_id);
alter table device add constraint fk_device_by_id foreign key (by_id) references user (id) on delete restrict on update restrict;

create index ix_device_owner_id on device (owner_id);
alter table device add constraint fk_device_owner_id foreign key (owner_id) references user (id) on delete restrict on update restrict;

create index ix_device_manufacturer_id on device (manufacturer_id);
alter table device add constraint fk_device_manufacturer_id foreign key (manufacturer_id) references organization (id) on delete restrict on update restrict;

alter table device add constraint fk_device_current_place_id foreign key (current_place_id) references place (id) on delete restrict on update restrict;

create index ix_device_device_type_id on device (device_type_id);
alter table device add constraint fk_device_device_type_id foreign key (device_type_id) references device_type (id) on delete restrict on update restrict;

create index ix_device_software_id on device (software_id);
alter table device add constraint fk_device_software_id foreign key (software_id) references product_release (id) on delete restrict on update restrict;

create index ix_device_place_device on device_place (device_id);
alter table device_place add constraint fk_device_place_device foreign key (device_id) references device (id) on delete restrict on update restrict;

create index ix_device_place_place on device_place (place_id);
alter table device_place add constraint fk_device_place_place foreign key (place_id) references place (id) on delete restrict on update restrict;

create index ix_device_collaborator_organizations_device on device_collaborator_organizations (device_id);
alter table device_collaborator_organizations add constraint fk_device_collaborator_organizations_device foreign key (device_id) references device (id) on delete restrict on update restrict;

create index ix_device_collaborator_organizations_organization on device_collaborator_organizations (collaborator_organization_id);
alter table device_collaborator_organizations add constraint fk_device_collaborator_organizations_organization foreign key (collaborator_organization_id) references organization (id) on delete restrict on update restrict;

create index ix_device_collaborator_users_device on device_collaborator_users (device_id);
alter table device_collaborator_users add constraint fk_device_collaborator_users_device foreign key (device_id) references device (id) on delete restrict on update restrict;

create index ix_device_collaborator_users_user on device_collaborator_users (user_id);
alter table device_collaborator_users add constraint fk_device_collaborator_users_user foreign key (user_id) references user (id) on delete restrict on update restrict;

create index ix_device_type_organization_id on device_type (organization_id);
alter table device_type add constraint fk_device_type_organization_id foreign key (organization_id) references organization (id) on delete restrict on update restrict;

create index ix_device_type_by_id on device_type (by_id);
alter table device_type add constraint fk_device_type_by_id foreign key (by_id) references user (id) on delete restrict on update restrict;

alter table device_type add constraint fk_device_type_image_id foreign key (image_id) references type_image (id) on delete restrict on update restrict;

create index ix_device_type_html_id on device_type (html_id);
alter table device_type add constraint fk_device_type_html_id foreign key (html_id) references html (id) on delete restrict on update restrict;

create index ix_device_type_properties_device_type on device_type_properties (device_type_id);
alter table device_type_properties add constraint fk_device_type_properties_device_type foreign key (device_type_id) references device_type (id) on delete restrict on update restrict;

create index ix_device_type_properties_device_type_property on device_type_properties (device_type_property_id);
alter table device_type_properties add constraint fk_device_type_properties_device_type_property foreign key (device_type_property_id) references device_type_property (id) on delete restrict on update restrict;

create index ix_device_type_property_organization_id on device_type_property (organization_id);
alter table device_type_property add constraint fk_device_type_property_organization_id foreign key (organization_id) references organization (id) on delete restrict on update restrict;

create index ix_device_type_property_by_id on device_type_property (by_id);
alter table device_type_property add constraint fk_device_type_property_by_id foreign key (by_id) references user (id) on delete restrict on update restrict;

alter table device_type_property add constraint fk_device_type_property_image_id foreign key (image_id) references type_image (id) on delete restrict on update restrict;

create index ix_device_type_property_device_type_property_type_id on device_type_property (device_type_property_type_id);
alter table device_type_property add constraint fk_device_type_property_device_type_property_type_id foreign key (device_type_property_type_id) references device_type_property_type (id) on delete restrict on update restrict;

create index ix_device_type_property_device_type_property_status_id on device_type_property (device_type_property_status_id);
alter table device_type_property add constraint fk_device_type_property_device_type_property_status_id foreign key (device_type_property_status_id) references device_type_property_status (id) on delete restrict on update restrict;

create index ix_device_type_property_status_organization_id on device_type_property_status (organization_id);
alter table device_type_property_status add constraint fk_device_type_property_status_organization_id foreign key (organization_id) references organization (id) on delete restrict on update restrict;

create index ix_device_type_property_status_by_id on device_type_property_status (by_id);
alter table device_type_property_status add constraint fk_device_type_property_status_by_id foreign key (by_id) references user (id) on delete restrict on update restrict;

alter table device_type_property_status add constraint fk_device_type_property_status_image_id foreign key (image_id) references type_image (id) on delete restrict on update restrict;

create index ix_device_type_property_type_organization_id on device_type_property_type (organization_id);
alter table device_type_property_type add constraint fk_device_type_property_type_organization_id foreign key (organization_id) references organization (id) on delete restrict on update restrict;

create index ix_device_type_property_type_by_id on device_type_property_type (by_id);
alter table device_type_property_type add constraint fk_device_type_property_type_by_id foreign key (by_id) references user (id) on delete restrict on update restrict;

alter table device_type_property_type add constraint fk_device_type_property_type_image_id foreign key (image_id) references type_image (id) on delete restrict on update restrict;

create index ix_floor_organization_id on floor (organization_id);
alter table floor add constraint fk_floor_organization_id foreign key (organization_id) references organization (id) on delete restrict on update restrict;

create index ix_floor_by_id on floor (by_id);
alter table floor add constraint fk_floor_by_id foreign key (by_id) references user (id) on delete restrict on update restrict;

alter table floor add constraint fk_floor_image_id foreign key (image_id) references type_image (id) on delete restrict on update restrict;

create index ix_floor_device_floor on floor_device (floor_id);
alter table floor_device add constraint fk_floor_device_floor foreign key (floor_id) references floor (id) on delete restrict on update restrict;

create index ix_floor_device_device on floor_device (device_id);
alter table floor_device add constraint fk_floor_device_device foreign key (device_id) references device (id) on delete restrict on update restrict;

create index ix_floor_floor_tag_floor on floor_floor_tag (floor_id);
alter table floor_floor_tag add constraint fk_floor_floor_tag_floor foreign key (floor_id) references floor (id) on delete restrict on update restrict;

create index ix_floor_floor_tag_floor_tag on floor_floor_tag (floor_tag_text);
alter table floor_floor_tag add constraint fk_floor_floor_tag_floor_tag foreign key (floor_tag_text) references floor_tag (text) on delete restrict on update restrict;

create index ix_floor_tag_organization_id on floor_tag (organization_id);
alter table floor_tag add constraint fk_floor_tag_organization_id foreign key (organization_id) references organization (id) on delete restrict on update restrict;

create index ix_html_organization_id on html (organization_id);
alter table html add constraint fk_html_organization_id foreign key (organization_id) references organization (id) on delete restrict on update restrict;

create index ix_html_by_id on html (by_id);
alter table html add constraint fk_html_by_id foreign key (by_id) references user (id) on delete restrict on update restrict;

alter table message add constraint fk_message_description_id foreign key (description_id) references html (id) on delete restrict on update restrict;

create index ix_message_from_user_id on message (from_user_id);
alter table message add constraint fk_message_from_user_id foreign key (from_user_id) references user (id) on delete restrict on update restrict;

create index ix_message_to_user_id on message (to_user_id);
alter table message add constraint fk_message_to_user_id foreign key (to_user_id) references user (id) on delete restrict on update restrict;

alter table organization add constraint fk_organization_place_id foreign key (place_id) references place (id) on delete restrict on update restrict;

alter table organization add constraint fk_organization_license_id foreign key (license_id) references license (id) on delete restrict on update restrict;

create index ix_organization_by_id on organization (by_id);
alter table organization add constraint fk_organization_by_id foreign key (by_id) references user (id) on delete restrict on update restrict;

create index ix_organization_approved_by_id on organization (approved_by_id);
alter table organization add constraint fk_organization_approved_by_id foreign key (approved_by_id) references user (id) on delete restrict on update restrict;

create index ix_collaborator_organizations_organization_1 on collaborator_organizations (organization_id);
alter table collaborator_organizations add constraint fk_collaborator_organizations_organization_1 foreign key (organization_id) references organization (id) on delete restrict on update restrict;

create index ix_collaborator_organizations_organization_2 on collaborator_organizations (collaborator_organization_id);
alter table collaborator_organizations add constraint fk_collaborator_organizations_organization_2 foreign key (collaborator_organization_id) references organization (id) on delete restrict on update restrict;

alter table organization_key add constraint fk_organization_key_organization_id foreign key (organization_id) references organization (id) on delete restrict on update restrict;

create index ix_place_organization_id on place (organization_id);
alter table place add constraint fk_place_organization_id foreign key (organization_id) references organization (id) on delete restrict on update restrict;

create index ix_place_by_id on place (by_id);
alter table place add constraint fk_place_by_id foreign key (by_id) references user (id) on delete restrict on update restrict;

create index ix_place_place_type_id on place (place_type_id);
alter table place add constraint fk_place_place_type_id foreign key (place_type_id) references place_type (id) on delete restrict on update restrict;

create index ix_place_floor_id on place (floor_id);
alter table place add constraint fk_place_floor_id foreign key (floor_id) references floor (id) on delete restrict on update restrict;

create index ix_place_user_id on place (user_id);
alter table place add constraint fk_place_user_id foreign key (user_id) references user (id) on delete restrict on update restrict;

create index ix_place_type_organization_id on place_type (organization_id);
alter table place_type add constraint fk_place_type_organization_id foreign key (organization_id) references organization (id) on delete restrict on update restrict;

create index ix_place_type_by_id on place_type (by_id);
alter table place_type add constraint fk_place_type_by_id foreign key (by_id) references user (id) on delete restrict on update restrict;

alter table place_type add constraint fk_place_type_image_id foreign key (image_id) references type_image (id) on delete restrict on update restrict;

create index ix_product_by_id on product (by_id);
alter table product add constraint fk_product_by_id foreign key (by_id) references user (id) on delete restrict on update restrict;

create index ix_product_tag_product on product_tag (product_id);
alter table product_tag add constraint fk_product_tag_product foreign key (product_id) references product (id) on delete restrict on update restrict;

create index ix_product_tag_tag on product_tag (tag_text);
alter table product_tag add constraint fk_product_tag_tag foreign key (tag_text) references tag (text) on delete restrict on update restrict;

create index ix_product_release_product_id on product_release (product_id);
alter table product_release add constraint fk_product_release_product_id foreign key (product_id) references product (id) on delete restrict on update restrict;

alter table product_release add constraint fk_product_release_upgrade_id foreign key (upgrade_id) references product_release (id) on delete restrict on update restrict;

create index ix_product_release_by_id on product_release (by_id);
alter table product_release add constraint fk_product_release_by_id foreign key (by_id) references user (id) on delete restrict on update restrict;

alter table product_release add constraint fk_product_release_html_id foreign key (html_id) references html (id) on delete restrict on update restrict;

create index ix_product_type_organization_id on product_type (organization_id);
alter table product_type add constraint fk_product_type_organization_id foreign key (organization_id) references organization (id) on delete restrict on update restrict;

create index ix_product_type_by_id on product_type (by_id);
alter table product_type add constraint fk_product_type_by_id foreign key (by_id) references user (id) on delete restrict on update restrict;

alter table product_type add constraint fk_product_type_image_id foreign key (image_id) references type_image (id) on delete restrict on update restrict;

create index ix_project_organization_id on project (organization_id);
alter table project add constraint fk_project_organization_id foreign key (organization_id) references organization (id) on delete restrict on update restrict;

create index ix_project_by_id on project (by_id);
alter table project add constraint fk_project_by_id foreign key (by_id) references user (id) on delete restrict on update restrict;

alter table project add constraint fk_project_description_id foreign key (description_id) references html (id) on delete restrict on update restrict;

create index ix_project_client_id on project (client_id);
alter table project add constraint fk_project_client_id foreign key (client_id) references organization (id) on delete restrict on update restrict;

create index ix_project_project_cycle_project on project_project_cycle (project_id);
alter table project_project_cycle add constraint fk_project_project_cycle_project foreign key (project_id) references project (id) on delete restrict on update restrict;

create index ix_project_project_cycle_project_cycle on project_project_cycle (project_cycle_id);
alter table project_project_cycle add constraint fk_project_project_cycle_project_cycle foreign key (project_cycle_id) references project_cycle (id) on delete restrict on update restrict;

create index ix_project_products_project on project_products (project_id);
alter table project_products add constraint fk_project_products_project foreign key (project_id) references project (id) on delete restrict on update restrict;

create index ix_project_products_product on project_products (product_id);
alter table project_products add constraint fk_project_products_product foreign key (product_id) references product (id) on delete restrict on update restrict;

create index ix_project_project_tag_project on project_project_tag (project_id);
alter table project_project_tag add constraint fk_project_project_tag_project foreign key (project_id) references project (id) on delete restrict on update restrict;

create index ix_project_project_tag_project_tag on project_project_tag (project_tag_text);
alter table project_project_tag add constraint fk_project_project_tag_project_tag foreign key (project_tag_text) references project_tag (text) on delete restrict on update restrict;

create index ix_project_cycle_type_id on project_cycle (type_id);
alter table project_cycle add constraint fk_project_cycle_type_id foreign key (type_id) references project_type (id) on delete restrict on update restrict;

create index ix_project_cycle_status_id on project_cycle (status_id);
alter table project_cycle add constraint fk_project_cycle_status_id foreign key (status_id) references project_status (id) on delete restrict on update restrict;

create index ix_project_cycle_priority_id on project_cycle (priority_id);
alter table project_cycle add constraint fk_project_cycle_priority_id foreign key (priority_id) references project_priority (id) on delete restrict on update restrict;

create index ix_project_cycle_organization_id on project_cycle (organization_id);
alter table project_cycle add constraint fk_project_cycle_organization_id foreign key (organization_id) references organization (id) on delete restrict on update restrict;

create index ix_project_cycle_by_id on project_cycle (by_id);
alter table project_cycle add constraint fk_project_cycle_by_id foreign key (by_id) references user (id) on delete restrict on update restrict;

create index ix_project_cycle_tasks_project_cycle on project_cycle_tasks (project_cycle_id);
alter table project_cycle_tasks add constraint fk_project_cycle_tasks_project_cycle foreign key (project_cycle_id) references project_cycle (id) on delete restrict on update restrict;

create index ix_project_cycle_tasks_task on project_cycle_tasks (task_id);
alter table project_cycle_tasks add constraint fk_project_cycle_tasks_task foreign key (task_id) references task (id) on delete restrict on update restrict;

create index ix_project_cycle_participants_project_cycle on project_cycle_participants (project_cycle_id);
alter table project_cycle_participants add constraint fk_project_cycle_participants_project_cycle foreign key (project_cycle_id) references project_cycle (id) on delete restrict on update restrict;

create index ix_project_cycle_participants_user on project_cycle_participants (user_id);
alter table project_cycle_participants add constraint fk_project_cycle_participants_user foreign key (user_id) references user (id) on delete restrict on update restrict;

create index ix_project_priority_organization_id on project_priority (organization_id);
alter table project_priority add constraint fk_project_priority_organization_id foreign key (organization_id) references organization (id) on delete restrict on update restrict;

create index ix_project_priority_by_id on project_priority (by_id);
alter table project_priority add constraint fk_project_priority_by_id foreign key (by_id) references user (id) on delete restrict on update restrict;

alter table project_priority add constraint fk_project_priority_image_id foreign key (image_id) references type_image (id) on delete restrict on update restrict;

create index ix_project_status_organization_id on project_status (organization_id);
alter table project_status add constraint fk_project_status_organization_id foreign key (organization_id) references organization (id) on delete restrict on update restrict;

create index ix_project_status_by_id on project_status (by_id);
alter table project_status add constraint fk_project_status_by_id foreign key (by_id) references user (id) on delete restrict on update restrict;

alter table project_status add constraint fk_project_status_image_id foreign key (image_id) references type_image (id) on delete restrict on update restrict;

create index ix_project_tag_organization_id on project_tag (organization_id);
alter table project_tag add constraint fk_project_tag_organization_id foreign key (organization_id) references organization (id) on delete restrict on update restrict;

create index ix_project_type_organization_id on project_type (organization_id);
alter table project_type add constraint fk_project_type_organization_id foreign key (organization_id) references organization (id) on delete restrict on update restrict;

create index ix_project_type_by_id on project_type (by_id);
alter table project_type add constraint fk_project_type_by_id foreign key (by_id) references user (id) on delete restrict on update restrict;

alter table project_type add constraint fk_project_type_image_id foreign key (image_id) references type_image (id) on delete restrict on update restrict;

create index ix_role_organization_id on role (organization_id);
alter table role add constraint fk_role_organization_id foreign key (organization_id) references organization (id) on delete restrict on update restrict;

create index ix_role_by_id on role (by_id);
alter table role add constraint fk_role_by_id foreign key (by_id) references user (id) on delete restrict on update restrict;

create index ix_role_role_image_id on role (role_image_id);
alter table role add constraint fk_role_role_image_id foreign key (role_image_id) references type_image (id) on delete restrict on update restrict;

create index ix_session_organization_id on session (organization_id);
alter table session add constraint fk_session_organization_id foreign key (organization_id) references organization (id) on delete restrict on update restrict;

create index ix_session_by_id on session (by_id);
alter table session add constraint fk_session_by_id foreign key (by_id) references user (id) on delete restrict on update restrict;

create index ix_settings_organization_id on settings (organization_id);
alter table settings add constraint fk_settings_organization_id foreign key (organization_id) references organization (id) on delete restrict on update restrict;

create index ix_settings_by_id on settings (by_id);
alter table settings add constraint fk_settings_by_id foreign key (by_id) references user (id) on delete restrict on update restrict;

create index ix_sighting_organization_id on sighting (organization_id);
alter table sighting add constraint fk_sighting_organization_id foreign key (organization_id) references organization (id) on delete restrict on update restrict;

create index ix_sighting_by_id on sighting (by_id);
alter table sighting add constraint fk_sighting_by_id foreign key (by_id) references user (id) on delete restrict on update restrict;

create index ix_sighting_reader_device_id on sighting (reader_device_id);
alter table sighting add constraint fk_sighting_reader_device_id foreign key (reader_device_id) references device (id) on delete restrict on update restrict;

create index ix_sighting_sighted_device_id on sighting (sighted_device_id);
alter table sighting add constraint fk_sighting_sighted_device_id foreign key (sighted_device_id) references device (id) on delete restrict on update restrict;

create index ix_sighting_sighted_user_id on sighting (sighted_user_id);
alter table sighting add constraint fk_sighting_sighted_user_id foreign key (sighted_user_id) references user (id) on delete restrict on update restrict;

create index ix_sighting_floor_id on sighting (floor_id);
alter table sighting add constraint fk_sighting_floor_id foreign key (floor_id) references floor (id) on delete restrict on update restrict;

create index ix_task_organization_id on task (organization_id);
alter table task add constraint fk_task_organization_id foreign key (organization_id) references organization (id) on delete restrict on update restrict;

create index ix_task_by_id on task (by_id);
alter table task add constraint fk_task_by_id foreign key (by_id) references user (id) on delete restrict on update restrict;

alter table task add constraint fk_task_detail_id foreign key (detail_id) references html (id) on delete restrict on update restrict;

create index ix_task_status_id on task (status_id);
alter table task add constraint fk_task_status_id foreign key (status_id) references task_status (id) on delete restrict on update restrict;

create index ix_task_type_id on task (type_id);
alter table task add constraint fk_task_type_id foreign key (type_id) references task_type (id) on delete restrict on update restrict;

create index ix_task_priority_id on task (priority_id);
alter table task add constraint fk_task_priority_id foreign key (priority_id) references task_priority (id) on delete restrict on update restrict;

create index ix_task_risk_id on task (risk_id);
alter table task add constraint fk_task_risk_id foreign key (risk_id) references task_risk (id) on delete restrict on update restrict;

create index ix_task_participants_task on task_participants (task_id);
alter table task_participants add constraint fk_task_participants_task foreign key (task_id) references task (id) on delete restrict on update restrict;

create index ix_task_participants_user on task_participants (user_id);
alter table task_participants add constraint fk_task_participants_user foreign key (user_id) references user (id) on delete restrict on update restrict;

create index ix_task_priority_organization_id on task_priority (organization_id);
alter table task_priority add constraint fk_task_priority_organization_id foreign key (organization_id) references organization (id) on delete restrict on update restrict;

create index ix_task_priority_by_id on task_priority (by_id);
alter table task_priority add constraint fk_task_priority_by_id foreign key (by_id) references user (id) on delete restrict on update restrict;

alter table task_priority add constraint fk_task_priority_image_id foreign key (image_id) references type_image (id) on delete restrict on update restrict;

create index ix_task_risk_organization_id on task_risk (organization_id);
alter table task_risk add constraint fk_task_risk_organization_id foreign key (organization_id) references organization (id) on delete restrict on update restrict;

create index ix_task_risk_by_id on task_risk (by_id);
alter table task_risk add constraint fk_task_risk_by_id foreign key (by_id) references user (id) on delete restrict on update restrict;

alter table task_risk add constraint fk_task_risk_image_id foreign key (image_id) references type_image (id) on delete restrict on update restrict;

create index ix_task_status_organization_id on task_status (organization_id);
alter table task_status add constraint fk_task_status_organization_id foreign key (organization_id) references organization (id) on delete restrict on update restrict;

create index ix_task_status_by_id on task_status (by_id);
alter table task_status add constraint fk_task_status_by_id foreign key (by_id) references user (id) on delete restrict on update restrict;

alter table task_status add constraint fk_task_status_image_id foreign key (image_id) references type_image (id) on delete restrict on update restrict;

create index ix_task_type_organization_id on task_type (organization_id);
alter table task_type add constraint fk_task_type_organization_id foreign key (organization_id) references organization (id) on delete restrict on update restrict;

create index ix_task_type_by_id on task_type (by_id);
alter table task_type add constraint fk_task_type_by_id foreign key (by_id) references user (id) on delete restrict on update restrict;

alter table task_type add constraint fk_task_type_image_id foreign key (image_id) references type_image (id) on delete restrict on update restrict;

create index ix_type_image_organization_id on type_image (organization_id);
alter table type_image add constraint fk_type_image_organization_id foreign key (organization_id) references organization (id) on delete restrict on update restrict;

create index ix_type_image_by_id on type_image (by_id);
alter table type_image add constraint fk_type_image_by_id foreign key (by_id) references user (id) on delete restrict on update restrict;

alter table user add constraint fk_user_image_id foreign key (image_id) references type_image (id) on delete restrict on update restrict;

alter table user add constraint fk_user_place_id foreign key (place_id) references place (id) on delete restrict on update restrict;

create index ix_user_organization_id on user (organization_id);
alter table user add constraint fk_user_organization_id foreign key (organization_id) references organization (id) on delete restrict on update restrict;

create index ix_user_role_id on user (role_id);
alter table user add constraint fk_user_role_id foreign key (role_id) references role (id) on delete restrict on update restrict;

alter table user add constraint fk_user_settings_id foreign key (settings_id) references settings (id) on delete restrict on update restrict;


# --- !Downs

alter table activity drop foreign key fk_activity_by_id;
drop index ix_activity_by_id on activity;

alter table activity_type drop foreign key fk_activity_type_image_id;

alter table activity_type drop foreign key fk_activity_type_organization_id;
drop index ix_activity_type_organization_id on activity_type;

alter table device drop foreign key fk_device_organization_id;
drop index ix_device_organization_id on device;

alter table device drop foreign key fk_device_by_id;
drop index ix_device_by_id on device;

alter table device drop foreign key fk_device_owner_id;
drop index ix_device_owner_id on device;

alter table device drop foreign key fk_device_manufacturer_id;
drop index ix_device_manufacturer_id on device;

alter table device drop foreign key fk_device_current_place_id;

alter table device drop foreign key fk_device_device_type_id;
drop index ix_device_device_type_id on device;

alter table device drop foreign key fk_device_software_id;
drop index ix_device_software_id on device;

alter table device_place drop foreign key fk_device_place_device;
drop index ix_device_place_device on device_place;

alter table device_place drop foreign key fk_device_place_place;
drop index ix_device_place_place on device_place;

alter table device_collaborator_organizations drop foreign key fk_device_collaborator_organizations_device;
drop index ix_device_collaborator_organizations_device on device_collaborator_organizations;

alter table device_collaborator_organizations drop foreign key fk_device_collaborator_organizations_organization;
drop index ix_device_collaborator_organizations_organization on device_collaborator_organizations;

alter table device_collaborator_users drop foreign key fk_device_collaborator_users_device;
drop index ix_device_collaborator_users_device on device_collaborator_users;

alter table device_collaborator_users drop foreign key fk_device_collaborator_users_user;
drop index ix_device_collaborator_users_user on device_collaborator_users;

alter table device_type drop foreign key fk_device_type_organization_id;
drop index ix_device_type_organization_id on device_type;

alter table device_type drop foreign key fk_device_type_by_id;
drop index ix_device_type_by_id on device_type;

alter table device_type drop foreign key fk_device_type_image_id;

alter table device_type drop foreign key fk_device_type_html_id;
drop index ix_device_type_html_id on device_type;

alter table device_type_properties drop foreign key fk_device_type_properties_device_type;
drop index ix_device_type_properties_device_type on device_type_properties;

alter table device_type_properties drop foreign key fk_device_type_properties_device_type_property;
drop index ix_device_type_properties_device_type_property on device_type_properties;

alter table device_type_property drop foreign key fk_device_type_property_organization_id;
drop index ix_device_type_property_organization_id on device_type_property;

alter table device_type_property drop foreign key fk_device_type_property_by_id;
drop index ix_device_type_property_by_id on device_type_property;

alter table device_type_property drop foreign key fk_device_type_property_image_id;

alter table device_type_property drop foreign key fk_device_type_property_device_type_property_type_id;
drop index ix_device_type_property_device_type_property_type_id on device_type_property;

alter table device_type_property drop foreign key fk_device_type_property_device_type_property_status_id;
drop index ix_device_type_property_device_type_property_status_id on device_type_property;

alter table device_type_property_status drop foreign key fk_device_type_property_status_organization_id;
drop index ix_device_type_property_status_organization_id on device_type_property_status;

alter table device_type_property_status drop foreign key fk_device_type_property_status_by_id;
drop index ix_device_type_property_status_by_id on device_type_property_status;

alter table device_type_property_status drop foreign key fk_device_type_property_status_image_id;

alter table device_type_property_type drop foreign key fk_device_type_property_type_organization_id;
drop index ix_device_type_property_type_organization_id on device_type_property_type;

alter table device_type_property_type drop foreign key fk_device_type_property_type_by_id;
drop index ix_device_type_property_type_by_id on device_type_property_type;

alter table device_type_property_type drop foreign key fk_device_type_property_type_image_id;

alter table floor drop foreign key fk_floor_organization_id;
drop index ix_floor_organization_id on floor;

alter table floor drop foreign key fk_floor_by_id;
drop index ix_floor_by_id on floor;

alter table floor drop foreign key fk_floor_image_id;

alter table floor_device drop foreign key fk_floor_device_floor;
drop index ix_floor_device_floor on floor_device;

alter table floor_device drop foreign key fk_floor_device_device;
drop index ix_floor_device_device on floor_device;

alter table floor_floor_tag drop foreign key fk_floor_floor_tag_floor;
drop index ix_floor_floor_tag_floor on floor_floor_tag;

alter table floor_floor_tag drop foreign key fk_floor_floor_tag_floor_tag;
drop index ix_floor_floor_tag_floor_tag on floor_floor_tag;

alter table floor_tag drop foreign key fk_floor_tag_organization_id;
drop index ix_floor_tag_organization_id on floor_tag;

alter table html drop foreign key fk_html_organization_id;
drop index ix_html_organization_id on html;

alter table html drop foreign key fk_html_by_id;
drop index ix_html_by_id on html;

alter table message drop foreign key fk_message_description_id;

alter table message drop foreign key fk_message_from_user_id;
drop index ix_message_from_user_id on message;

alter table message drop foreign key fk_message_to_user_id;
drop index ix_message_to_user_id on message;

alter table organization drop foreign key fk_organization_place_id;

alter table organization drop foreign key fk_organization_license_id;

alter table organization drop foreign key fk_organization_by_id;
drop index ix_organization_by_id on organization;

alter table organization drop foreign key fk_organization_approved_by_id;
drop index ix_organization_approved_by_id on organization;

alter table collaborator_organizations drop foreign key fk_collaborator_organizations_organization_1;
drop index ix_collaborator_organizations_organization_1 on collaborator_organizations;

alter table collaborator_organizations drop foreign key fk_collaborator_organizations_organization_2;
drop index ix_collaborator_organizations_organization_2 on collaborator_organizations;

alter table organization_key drop foreign key fk_organization_key_organization_id;

alter table place drop foreign key fk_place_organization_id;
drop index ix_place_organization_id on place;

alter table place drop foreign key fk_place_by_id;
drop index ix_place_by_id on place;

alter table place drop foreign key fk_place_place_type_id;
drop index ix_place_place_type_id on place;

alter table place drop foreign key fk_place_floor_id;
drop index ix_place_floor_id on place;

alter table place drop foreign key fk_place_user_id;
drop index ix_place_user_id on place;

alter table place_type drop foreign key fk_place_type_organization_id;
drop index ix_place_type_organization_id on place_type;

alter table place_type drop foreign key fk_place_type_by_id;
drop index ix_place_type_by_id on place_type;

alter table place_type drop foreign key fk_place_type_image_id;

alter table product drop foreign key fk_product_by_id;
drop index ix_product_by_id on product;

alter table product_tag drop foreign key fk_product_tag_product;
drop index ix_product_tag_product on product_tag;

alter table product_tag drop foreign key fk_product_tag_tag;
drop index ix_product_tag_tag on product_tag;

alter table product_release drop foreign key fk_product_release_product_id;
drop index ix_product_release_product_id on product_release;

alter table product_release drop foreign key fk_product_release_upgrade_id;

alter table product_release drop foreign key fk_product_release_by_id;
drop index ix_product_release_by_id on product_release;

alter table product_release drop foreign key fk_product_release_html_id;

alter table product_type drop foreign key fk_product_type_organization_id;
drop index ix_product_type_organization_id on product_type;

alter table product_type drop foreign key fk_product_type_by_id;
drop index ix_product_type_by_id on product_type;

alter table product_type drop foreign key fk_product_type_image_id;

alter table project drop foreign key fk_project_organization_id;
drop index ix_project_organization_id on project;

alter table project drop foreign key fk_project_by_id;
drop index ix_project_by_id on project;

alter table project drop foreign key fk_project_description_id;

alter table project drop foreign key fk_project_client_id;
drop index ix_project_client_id on project;

alter table project_project_cycle drop foreign key fk_project_project_cycle_project;
drop index ix_project_project_cycle_project on project_project_cycle;

alter table project_project_cycle drop foreign key fk_project_project_cycle_project_cycle;
drop index ix_project_project_cycle_project_cycle on project_project_cycle;

alter table project_products drop foreign key fk_project_products_project;
drop index ix_project_products_project on project_products;

alter table project_products drop foreign key fk_project_products_product;
drop index ix_project_products_product on project_products;

alter table project_project_tag drop foreign key fk_project_project_tag_project;
drop index ix_project_project_tag_project on project_project_tag;

alter table project_project_tag drop foreign key fk_project_project_tag_project_tag;
drop index ix_project_project_tag_project_tag on project_project_tag;

alter table project_cycle drop foreign key fk_project_cycle_type_id;
drop index ix_project_cycle_type_id on project_cycle;

alter table project_cycle drop foreign key fk_project_cycle_status_id;
drop index ix_project_cycle_status_id on project_cycle;

alter table project_cycle drop foreign key fk_project_cycle_priority_id;
drop index ix_project_cycle_priority_id on project_cycle;

alter table project_cycle drop foreign key fk_project_cycle_organization_id;
drop index ix_project_cycle_organization_id on project_cycle;

alter table project_cycle drop foreign key fk_project_cycle_by_id;
drop index ix_project_cycle_by_id on project_cycle;

alter table project_cycle_tasks drop foreign key fk_project_cycle_tasks_project_cycle;
drop index ix_project_cycle_tasks_project_cycle on project_cycle_tasks;

alter table project_cycle_tasks drop foreign key fk_project_cycle_tasks_task;
drop index ix_project_cycle_tasks_task on project_cycle_tasks;

alter table project_cycle_participants drop foreign key fk_project_cycle_participants_project_cycle;
drop index ix_project_cycle_participants_project_cycle on project_cycle_participants;

alter table project_cycle_participants drop foreign key fk_project_cycle_participants_user;
drop index ix_project_cycle_participants_user on project_cycle_participants;

alter table project_priority drop foreign key fk_project_priority_organization_id;
drop index ix_project_priority_organization_id on project_priority;

alter table project_priority drop foreign key fk_project_priority_by_id;
drop index ix_project_priority_by_id on project_priority;

alter table project_priority drop foreign key fk_project_priority_image_id;

alter table project_status drop foreign key fk_project_status_organization_id;
drop index ix_project_status_organization_id on project_status;

alter table project_status drop foreign key fk_project_status_by_id;
drop index ix_project_status_by_id on project_status;

alter table project_status drop foreign key fk_project_status_image_id;

alter table project_tag drop foreign key fk_project_tag_organization_id;
drop index ix_project_tag_organization_id on project_tag;

alter table project_type drop foreign key fk_project_type_organization_id;
drop index ix_project_type_organization_id on project_type;

alter table project_type drop foreign key fk_project_type_by_id;
drop index ix_project_type_by_id on project_type;

alter table project_type drop foreign key fk_project_type_image_id;

alter table role drop foreign key fk_role_organization_id;
drop index ix_role_organization_id on role;

alter table role drop foreign key fk_role_by_id;
drop index ix_role_by_id on role;

alter table role drop foreign key fk_role_role_image_id;
drop index ix_role_role_image_id on role;

alter table session drop foreign key fk_session_organization_id;
drop index ix_session_organization_id on session;

alter table session drop foreign key fk_session_by_id;
drop index ix_session_by_id on session;

alter table settings drop foreign key fk_settings_organization_id;
drop index ix_settings_organization_id on settings;

alter table settings drop foreign key fk_settings_by_id;
drop index ix_settings_by_id on settings;

alter table sighting drop foreign key fk_sighting_organization_id;
drop index ix_sighting_organization_id on sighting;

alter table sighting drop foreign key fk_sighting_by_id;
drop index ix_sighting_by_id on sighting;

alter table sighting drop foreign key fk_sighting_reader_device_id;
drop index ix_sighting_reader_device_id on sighting;

alter table sighting drop foreign key fk_sighting_sighted_device_id;
drop index ix_sighting_sighted_device_id on sighting;

alter table sighting drop foreign key fk_sighting_sighted_user_id;
drop index ix_sighting_sighted_user_id on sighting;

alter table sighting drop foreign key fk_sighting_floor_id;
drop index ix_sighting_floor_id on sighting;

alter table task drop foreign key fk_task_organization_id;
drop index ix_task_organization_id on task;

alter table task drop foreign key fk_task_by_id;
drop index ix_task_by_id on task;

alter table task drop foreign key fk_task_detail_id;

alter table task drop foreign key fk_task_status_id;
drop index ix_task_status_id on task;

alter table task drop foreign key fk_task_type_id;
drop index ix_task_type_id on task;

alter table task drop foreign key fk_task_priority_id;
drop index ix_task_priority_id on task;

alter table task drop foreign key fk_task_risk_id;
drop index ix_task_risk_id on task;

alter table task_participants drop foreign key fk_task_participants_task;
drop index ix_task_participants_task on task_participants;

alter table task_participants drop foreign key fk_task_participants_user;
drop index ix_task_participants_user on task_participants;

alter table task_priority drop foreign key fk_task_priority_organization_id;
drop index ix_task_priority_organization_id on task_priority;

alter table task_priority drop foreign key fk_task_priority_by_id;
drop index ix_task_priority_by_id on task_priority;

alter table task_priority drop foreign key fk_task_priority_image_id;

alter table task_risk drop foreign key fk_task_risk_organization_id;
drop index ix_task_risk_organization_id on task_risk;

alter table task_risk drop foreign key fk_task_risk_by_id;
drop index ix_task_risk_by_id on task_risk;

alter table task_risk drop foreign key fk_task_risk_image_id;

alter table task_status drop foreign key fk_task_status_organization_id;
drop index ix_task_status_organization_id on task_status;

alter table task_status drop foreign key fk_task_status_by_id;
drop index ix_task_status_by_id on task_status;

alter table task_status drop foreign key fk_task_status_image_id;

alter table task_type drop foreign key fk_task_type_organization_id;
drop index ix_task_type_organization_id on task_type;

alter table task_type drop foreign key fk_task_type_by_id;
drop index ix_task_type_by_id on task_type;

alter table task_type drop foreign key fk_task_type_image_id;

alter table type_image drop foreign key fk_type_image_organization_id;
drop index ix_type_image_organization_id on type_image;

alter table type_image drop foreign key fk_type_image_by_id;
drop index ix_type_image_by_id on type_image;

alter table user drop foreign key fk_user_image_id;

alter table user drop foreign key fk_user_place_id;

alter table user drop foreign key fk_user_organization_id;
drop index ix_user_organization_id on user;

alter table user drop foreign key fk_user_role_id;
drop index ix_user_role_id on user;

alter table user drop foreign key fk_user_settings_id;

drop table if exists activity;

drop table if exists activity_type;

drop table if exists data_migration;

drop table if exists device;

drop table if exists device_place;

drop table if exists device_collaborator_organizations;

drop table if exists device_collaborator_users;

drop table if exists device_type;

drop table if exists device_type_properties;

drop table if exists device_type_property;

drop table if exists device_type_property_status;

drop table if exists device_type_property_type;

drop table if exists floor;

drop table if exists floor_device;

drop table if exists floor_floor_tag;

drop table if exists floor_tag;

drop table if exists history;

drop table if exists html;

drop table if exists license;

drop table if exists message;

drop table if exists organization;

drop table if exists collaborator_organizations;

drop table if exists organization_key;

drop table if exists place;

drop table if exists place_type;

drop table if exists product;

drop table if exists product_tag;

drop table if exists product_release;

drop table if exists product_type;

drop table if exists project;

drop table if exists project_project_cycle;

drop table if exists project_products;

drop table if exists project_project_tag;

drop table if exists project_cycle;

drop table if exists project_cycle_tasks;

drop table if exists project_cycle_participants;

drop table if exists project_priority;

drop table if exists project_status;

drop table if exists project_tag;

drop table if exists project_type;

drop table if exists role;

drop table if exists session;

drop table if exists settings;

drop table if exists sighting;

drop table if exists tag;

drop table if exists task;

drop table if exists task_participants;

drop table if exists task_priority;

drop table if exists task_risk;

drop table if exists task_status;

drop table if exists task_type;

drop table if exists type_image;

drop table if exists user;

drop index ix_project_tag_text on project_tag;
drop index ix_tag_text on tag;
