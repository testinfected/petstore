create table orders (
  id bigint(20) unsigned not null auto_increment,
  number varchar(8) not null unique,
  primary key(id)
) engine=InnoDB;

create table line_items (
  id bigint(20) unsigned not null auto_increment,
  item_number varchar(8) not null,
  total_price decimal(10,2) not null,
  quantity smallint unsigned,
  order_id bigint(20) unsigned not null,
  primary key(id),
  index(order_id),
  foreign key(order_id) references orders(id)
) engine=InnoDB;

