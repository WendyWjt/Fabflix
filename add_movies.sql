use moviedb;

DELIMITER $$

DROP PROCEDURE IF EXISTS add_movie$$

CREATE PROCEDURE add_movie(
OUT result INTEGER, OUT new_movie_id varchar(10), OUT new_genre_id INTEGER, 
OUT new_star_id varchar(10),IN title_input VARCHAR(100), IN year_input INTEGER, IN director_input VARCHAR(100),
IN name_input VARCHAR(100),IN genre_name_input VARCHAR(32))

BEGIN

declare count_movie int;
declare count_star int;
declare count_genre int;
declare movie_id varchar(10);

set count_movie = (select count(*) from movies where movies.title = title_input and movies.year=year_input and movies.director=director_input);
set count_star = (select count(*) from stars where stars.name = name_input);
set count_genre = (select count(*) from genres where genres.name = genre_name_input);

	IF count_movie = 0 THEN 
        set new_movie_id = (select concat('tt', (select (SELECT CAST((SELECT SUBSTRING_INDEX((select max(id) from movies),'t',-1)) AS UNSIGNED)) +1)));
		INSERT INTO movies (id, title, year, director) VALUES (new_movie_id, title_input, year_input, director_input);
		INSERT INTO ratings(movieId, rating, numVotes) values(new_movie_id, 0, 0);
        
        IF (count_star = 0) THEN
            set new_star_id = (select concat('nm', (select (SELECT CAST((SELECT SUBSTRING_INDEX((select max(id) from stars),'m',-1)) AS UNSIGNED)) +1)));
            INSERT INTO stars (id, name) VALUES (new_star_id, name_input);
			INSERT INTO stars_in_movies(starId, movieId) values(new_star_id, new_movie_id);
           
		ELSE
			set new_star_id = (select id from stars where name = name_input);
			INSERT INTO stars_in_movies(starId, movieId) values(new_star_id, new_movie_id);
        END IF;

        IF (count_genre = 0) THEN
            INSERT INTO genres (name) VALUES (genre_name_input);
            SET new_genre_id = (select id from genres where name = genre_name_input);
            
            insert into genres_in_movies(genreId, movieId) values(new_genre_id, new_movie_id);

           
		ELSE
			SET new_genre_id = (select id from genres where name = genre_name_input);
            insert into genres_in_movies(genreId, movieId) values(new_genre_id, new_movie_id);

           
		END IF;
        
        set result= 1;
     ELSE
	   set result = 0;
       set new_movie_id = "0";
       set new_star_id = "0";
       set new_genre_id = 0;
	END IF;
END$$

DELIMITER ;