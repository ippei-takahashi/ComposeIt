# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table `todos` (`id` INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,`description` VARCHAR(254) NOT NULL,`done` BOOLEAN NOT NULL);
create table `users` (`id` INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,`email` VARCHAR(254) NOT NULL,`name` VARCHAR(254) NOT NULL);
create unique index `idx_users_unq_1` on `users` (`email`);

# --- !Downs

drop table `users`;
drop table `todos`;

