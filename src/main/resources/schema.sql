CREATE TABLE IF NOT EXISTS "users"(
    "user_id" INTEGER PRIMARY KEY NOT NULL,
    "user_email" VARCHAR(255) NOT NULL,
    "user_login" VARCHAR(255) NOT NULL,
    "user_birthdate" DATE NOT NULL,
    "user_name" VARCHAR(255) NULL
);

CREATE TABLE IF NOT EXISTS "films"(
    "film_id" INTEGER PRIMARY KEY NOT NULL,
    "film_name" VARCHAR(255) NOT NULL,
    "film_release_date" DATE NOT NULL,
    "film_duration_min" INTEGER NOT NULL,
    "film_desc" VARCHAR(255) NOT NULL,
    "mpa_id" INTEGER NULL
);

CREATE TABLE IF NOT EXISTS "mpa_rating"(
    "mpa_id" INTEGER PRIMARY KEY NOT NULL,
    "mpa_name" VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS "genres"(
    "genre_id" INTEGER PRIMARY KEY NOT NULL,
    "genre_name" VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS "film_genre"(
    "film_id" INTEGER NOT NULL,
    "genre_id" INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS "user_user_friends"(
    "user_id" INTEGER NOT NULL,
    "friend_id" INTEGER NOT NULL
);
CREATE TABLE IF NOT EXISTS "film_user_likes"(
    "film_id" INTEGER NOT NULL,
    "user_id" INTEGER NOT NULL
);


ALTER TABLE
    "films" ADD CONSTRAINT IF NOT EXISTS "films_mpa_id_foreign" FOREIGN KEY("mpa_id") REFERENCES "mpa_rating"("mpa_id") ON DELETE CASCADE;
ALTER TABLE
    "film_genre" ADD CONSTRAINT IF NOT EXISTS "film_genre_film_id_foreign" FOREIGN KEY("film_id") REFERENCES "films"("film_id") ON DELETE CASCADE;
ALTER TABLE
    "film_genre" ADD CONSTRAINT IF NOT EXISTS "film_genre_genre_id_foreign" FOREIGN KEY("genre_id") REFERENCES "genres"("genre_id") ON DELETE CASCADE;
ALTER TABLE
    "film_user_likes" ADD CONSTRAINT IF NOT EXISTS "film_user_likes_film_id_foreign" FOREIGN KEY("film_id") REFERENCES "films"("film_id") ON DELETE CASCADE;
ALTER TABLE
    "film_user_likes" ADD CONSTRAINT IF NOT EXISTS "film_user_likes_user_id_foreign" FOREIGN KEY("user_id") REFERENCES "users"("user_id") ON DELETE CASCADE;
ALTER TABLE
    "user_user_friends" ADD CONSTRAINT IF NOT EXISTS "user_user_friends_user_id_foreign" FOREIGN KEY("user_id") REFERENCES "users"("user_id") ON DELETE CASCADE;
ALTER TABLE
    "user_user_friends" ADD CONSTRAINT IF NOT EXISTS "user_user_friends_friend_id_foreign" FOREIGN KEY("friend_id") REFERENCES "users"("user_id") ON DELETE CASCADE;
	
	
	
	
	
