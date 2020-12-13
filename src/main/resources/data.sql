-- campsite
insert into camp_site (site_name, status) values ('camp_site1','ACTIVE'),
('camp_site2','ACTIVE'),
('camp_site3','ACTIVE'),
('camp_site4','ACTIVE'),
('camp_site5','ACTIVE'),
('camp_site6','ACTIVE'),
('camp_site7','ACTIVE'),
('camp_site8','ACTIVE'),
('camp_site9','ACTIVE'),
('camp_site10','ACTIVE');


-- reservation

insert into reservation (camp_site_id, start_date, end_date, booked_by, email, status) values
(1, '2020-09-01','2020-09-03', 'cust1', 'cust1@company.com','CONFIRMED'),
(1, '2020-09-05','2020-09-07','cust2', 'cust2@company.com','CONFIRMED'),
(1, '2020-09-11','2020-09-13','cust3', 'cust3@company.com','CONFIRMED'),
(1, '2020-09-20','2020-09-22','cust4', 'cust4@company.com','CONFIRMED'),
(1, '2020-09-25','2020-09-27', 'cust5', 'cust5@company.com','CONFIRMED'),
(1, '2020-09-28','2020-09-30', 'cust6', 'cust6@company.com','CONFIRMED'),
(1, '2020-10-01','2020-10-04', 'cust2', 'cust2@company.com','CONFIRMED'),
(2, '2020-09-06','2020-09-08','cust2', 'cust2@company.com','CONFIRMED'),
(2, '2020-09-12','2020-09-14','cust3', 'cust3@company.com','CONFIRMED'),
(2, '2020-09-21','2020-09-23','cust4', 'cust4@company.com','CONFIRMED'),
(2, '2020-09-26','2020-09-28', 'cust5', 'cust5@company.com','CONFIRMED'),
(2, '2020-09-29','2020-09-30', 'cust6', 'cust6@company.com','CONFIRMED');
