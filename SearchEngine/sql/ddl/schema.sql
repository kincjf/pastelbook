CREATE TABLE contributor(
id character varying(256) NOT NULL
);

CREATE TABLE register_member(
url character varying(2048) NOT NULL,
id character varying(256) NOT NULL
);

CREATE TABLE url_data(
idx_num integer,
url character varying(2048) NOT NULL,
title character varying(512) DEFAULT '''UNKNOWN''',
modified_date character(64) DEFAULT '''sysdate                                                         ''',
registered_date character(64) DEFAULT '''sysdate                                                         ''',
hit integer DEFAULT 0
);

