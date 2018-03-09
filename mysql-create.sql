create table user (
  id                        bigint auto_increment not null,
  nama                      varchar(255),
  username                  varchar(255),
  password                  varchar(255),
  akses                     varchar(255),
  constraint pk_user primary key (id))
;



