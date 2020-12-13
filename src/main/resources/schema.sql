drop table if exists camp_site;

create table camp_site (
    id INT AUTO_INCREMENT PRIMARY KEY,
    site_name VARCHAR(250) NOT NULL,
    status varchar(100)
);

create table reservation (
    id INT AUTO_INCREMENT PRIMARY KEY,
    camp_site_id int NOT NULL,
    start_date date,
    end_date date,
    booked_by varchar(200),
    email varchar(200),
    status varchar(100)
);

ALTER TABLE reservation ADD foreign key fk_camp_site_id (camp_site_id) references camp_site(id);
