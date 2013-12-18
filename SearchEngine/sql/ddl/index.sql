ALTER TABLE contributor ADD CONSTRAINT pk PRIMARY KEY (id);
ALTER TABLE register_member ADD CONSTRAINT pk_register_member_idx_num_regi_member PRIMARY KEY (url,id);
ALTER TABLE url_data ADD CONSTRAINT pk_url_data_idx_num PRIMARY KEY (url);

CREATE UNIQUE INDEX u_contributor_id ON contributor(id);
CREATE UNIQUE INDEX u_url_data_idx_num ON url_data(idx_num);

CREATE UNIQUE INDEX u_url_data_url ON url_data(url);

ALTER TABLE register_member ADD FOREIGN KEY (id) REFERENCES contributor(id) ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE register_member ADD FOREIGN KEY (url) REFERENCES url_data(url) ON DELETE RESTRICT ON UPDATE RESTRICT;
