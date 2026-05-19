CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE users (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email       VARCHAR(255) NOT NULL UNIQUE,
    pw_hash     VARCHAR(255) NOT NULL,
    display_name VARCHAR(100) NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE goals (
    id                    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    u_id                  UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    target_weight_lbs     FLOAT NOT NULL,
    start_weight_lbs      FLOAT NOT NULL,
    target_daily_cal_intake INTEGER NOT NULL,
    status                VARCHAR(20) NOT NULL DEFAULT 'active',
    start_date            DATE NOT NULL,
    created_at            TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE weight_logs (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    u_id        UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    weight_lbs  FLOAT NOT NULL,
    on_date     DATE NOT NULL,
    notes       VARCHAR(500),
    created_at  TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE weekly_summaries (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    u_id                UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    week_start          DATE NOT NULL,
    week_end            DATE NOT NULL,
    avg_cals            FLOAT,
    workouts_completed  INTEGER NOT NULL DEFAULT 0,
    weight_diff         FLOAT,
    created_at          TIMESTAMP NOT NULL DEFAULT now(),
    UNIQUE(u_id, week_start)
);

CREATE TABLE meals (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    u_id        UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    meal_date   DATE NOT NULL,
    meal_name   VARCHAR(100) NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE food_logs (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    u_id             UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    meal_id          UUID NOT NULL REFERENCES meals(id) ON DELETE CASCADE,
    food_id_api      VARCHAR(100) NOT NULL,
    food_name        VARCHAR(255) NOT NULL,
    serving_quantity FLOAT NOT NULL,
    calories         FLOAT NOT NULL,
    protein_grams    FLOAT NOT NULL,
    carb_grams       FLOAT NOT NULL,
    fat_grams        FLOAT NOT NULL,
    log_date         DATE NOT NULL,
    created_at       TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE workout_splits (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    u_id        UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    split_name  VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    is_active   BOOLEAN NOT NULL DEFAULT true,
    created_at  TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE split_days (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    split_id     UUID NOT NULL REFERENCES workout_splits(id) ON DELETE CASCADE,
    on_day       VARCHAR(20) NOT NULL,
    workout_name VARCHAR(100) NOT NULL,
    created_at   TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE split_day_workouts (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    split_day_id   UUID NOT NULL REFERENCES split_days(id) ON DELETE CASCADE,
    workout_id_api VARCHAR(100) NOT NULL,
    exercise_name  VARCHAR(255) NOT NULL,
    muscle_group   VARCHAR(100),
    disp_order     INTEGER NOT NULL DEFAULT 0,
    created_at     TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE workout_logs (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    u_id          UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    split_day_id  UUID NOT NULL REFERENCES split_days(id) ON DELETE CASCADE,
    performed_on  DATE NOT NULL,
    duration_min  INTEGER,
    created_at    TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE workouts_performed (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    workout_log_id   UUID NOT NULL REFERENCES workout_logs(id) ON DELETE CASCADE,
    workout_id_api   VARCHAR(100) NOT NULL,
    set_number       INTEGER NOT NULL,
    reps             INTEGER NOT NULL,
    weight_for_set_lbs FLOAT NOT NULL,
    completed        BOOLEAN NOT NULL DEFAULT false,
    created_at       TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_goals_u_id ON goals(u_id);
CREATE INDEX idx_weight_logs_u_id_date ON weight_logs(u_id, on_date);
CREATE INDEX idx_weekly_summaries_u_id ON weekly_summaries(u_id);
CREATE INDEX idx_meals_u_id_date ON meals(u_id, meal_date);
CREATE INDEX idx_food_logs_meal_id ON food_logs(meal_id);
CREATE INDEX idx_food_logs_u_id_date ON food_logs(u_id, log_date);
CREATE INDEX idx_workout_splits_u_id ON workout_splits(u_id);
CREATE INDEX idx_split_days_split_id ON split_days(split_id);
CREATE INDEX idx_split_day_workouts_split_day_id ON split_day_workouts(split_day_id);
CREATE INDEX idx_workout_logs_u_id_date ON workout_logs(u_id, performed_on);
CREATE INDEX idx_workouts_performed_log_id ON workouts_performed(workout_log_id);
