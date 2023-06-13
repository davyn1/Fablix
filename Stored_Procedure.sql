USE moviedb;
DELIMITER $$
CREATE PROCEDURE add_movie (IN movie_id varchar(10), IN movie_title varchar(100), IN movie_year INTEGER, IN movie_director varchar(100),
					IN star_id varchar(10), IN star_name varchar(100), IN star_birthYear INTEGER, 
					IN genre_id INTEGER, IN genre_name varchar(32))
BEGIN
	DECLARE new_star_id varchar(10);
    DECLARE new_movie_id varchar(10);
    DECLARE new_genre_id INTEGER;
    -- Generate new IDs 
    SET new_star_id = (SELECT CONCAT(SUBSTRING(MAX(id), 1, 2), CAST(SUBSTRING(MAX(id), 3) AS SIGNED) + 1) FROM stars);
    SET new_movie_id = (SELECT CONCAT(SUBSTRING(MAX(id), 1, 2), LPAD(CAST(SUBSTRING(MAX(id), 3) AS SIGNED) + 1, 7, '0')) FROM movies);
    SET new_genre_id = (SELECT MAX(id) + 1 FROM genres);
-- Check for duplicate movie and return -1 to show error or invalid
IF EXISTS (SELECT * FROM movies WHERE title = movie_title AND director = movie_director AND year = movie_year) THEN
    SELECT -1 AS report; 
ELSE
-- Insert new_id and genre_name into genres, ignores if genre_name already exists. 
IF ((SELECT COUNT(*) FROM genres WHERE name = genre_name) = 0) THEN
			INSERT INTO genres (id, name) VALUES (new_genre_id, genre_name);
            
END IF;
-- Check if there is a star that exists, if not, insert
IF ((SELECT COUNT(*) FROM stars WHERE stars.name = star_name) = 0) THEN
		-- Null for birthYear if there is no input
		IF (star_birthYear = "" OR star_birthYear < 0) THEN
			INSERT INTO stars VALUES (new_star_id, star_name, null);
		-- if there is input, insert the birthYear
        ELSE 
			INSERT INTO stars values (new_star_id, star_name, star_birthYear);
		END IF;
	END IF;
    -- Situation where movie isn't duplicate
IF((SELECT COUNT(*) FROM movies
			WHERE movies.title = movie_title AND movies.director = movie_director AND movies.year = movie_year ) = 0) THEN
			INSERT INTO movies VALUES(new_movie_id, movie_title, movie_year, movie_director);
            -- Handle inserts into stars_in_movies table and genres_in_movies table
			INSERT INTO genres_in_movies VALUES ((SELECT id from genres where genres.name = genre_name LIMIT 1), new_movie_id);
			INSERT INTO stars_in_movies VALUES ((SELECT id from stars WHERE stars.name = star_name LIMIT 1), new_movie_id);
			INSERT INTO ratings VALUES (new_movie_id, 0, 0);
			SELECT 1 as report, stars.id AS s_id, genres.id AS g_id, new_movie_id AS m_id
            FROM genres, stars WHERE genres.name = genre_name AND stars.name = star_name;
		END IF;
	END IF;
END
$$
DELIMITER ;