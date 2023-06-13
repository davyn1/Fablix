-- drop database moviedb;
create database moviedb;

use moviedb;

create table movies(
	id varchar(10) not null,
	title varchar(100) not null,
	year integer not null,
	director varchar(100) not null,
    primary key(id)
);

create table stars(
	id varchar(10) not null,
	name varchar(100) not null,
    birthYear integer,
    primary key(id)
);

create table stars_in_movies(
	starId varchar(10) not null,
    movieId varchar(10) not null,
    foreign key(starId) references stars(id) on delete cascade on update cascade,
    foreign key(movieId) references movies(id) on delete cascade on update cascade
);

create table genres(
	id int not null auto_increment,
	name varchar(32) not null,
    primary key(id)
);

create table genres_in_movies(
	genreId integer not null,
    movieId varchar(10) not null,
    foreign key(genreId) references genres(id) on delete cascade on update cascade,
    foreign key(movieId) references movies(id) on delete cascade on update cascade
);

CREATE TABLE creditcards (
  id varchar(20) NOT NULL,
  firstName varchar(50) not null,
  lastName varchar(50) NOT NULL,
  expiration date NOT NULL,
  PRIMARY KEY (id)
);

create table customers(
	id integer auto_increment not null,
    firstName varchar(50) not null,
    lastName varchar(50) not null,
    ccId varchar(20) not null,
    address varchar(200) not null,
    email varchar(50) not null,
    password varchar(20) not null,
    primary key(id),
    foreign key(ccID) references creditcards(id) on delete cascade on update cascade
);

CREATE TABLE sales (
  id int NOT NULL AUTO_INCREMENT,
  customerId int NOT NULL,
  movieId varchar(10) NOT NULL,
  saleDate date NOT NULL,
  PRIMARY KEY (id),
  foreign key(customerId) references customers(id),
  foreign key(movieId) references movies(id)
);

CREATE TABLE ratings (
  movieId varchar(10) NOT NULL,
  rating float NOT NULL,
  numVotes int NOT NULL,
  foreign key (movieId) references movies(id) on delete cascade on update cascade
);
