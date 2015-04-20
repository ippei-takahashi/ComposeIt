# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table `users` (`id` INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,`email` VARCHAR(254) NOT NULL,`name` VARCHAR(254) NOT NULL);
create unique index `idx_users_unq_1` on `users` (`email`);

# --- !Downs

drop table `users`;

