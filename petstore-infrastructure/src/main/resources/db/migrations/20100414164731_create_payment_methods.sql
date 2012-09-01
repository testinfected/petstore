alter table orders drop billing_first_name;
alter table orders drop billing_last_name;
alter table orders drop billing_email;
alter table orders drop credit_card_type;
alter table orders drop credit_card_number;
alter table orders drop credit_card_expiry_date;

create table payments (
  id bigint(20) unsigned not null auto_increment,
  billing_first_name varchar(255),
  billing_last_name varchar(255),
  billing_email varchar(255),
  card_type varchar(255),
  card_number varchar(255),
  card_expiry_date varchar(255),
  primary key(id)
) engine=InnoDB;

alter table orders add payment_id bigint(20) unsigned;
alter table orders add index(payment_id);
alter table orders add foreign key(payment_id) references payments(id);