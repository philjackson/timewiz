-- name: person-by-email-with-teams
SELECT p.*,
      o.max_person_count           AS sp_max_person_count,
      o.stripe_plan_id             AS sp_id,
      o.stripe_subscription_id     AS sp_subscription_id,

      o.free_trial_ends_at         AS organisation_free_trial_ends_at,
      o.id                         AS organisation_id,
      o.title                      AS organisation_title,
      op.is_admin                  AS is_organisation_admin,

      t.id                         AS team_id,
      t.title                      AS team_title,
      tp.is_admin                  AS is_team_admin,
      t.organisation_id            AS team_organisation_id,
      t.colour                     AS team_colour,
      t.latest_board_id            AS latest_board_id
FROM person                        AS p
     LEFT JOIN team_person         AS tp  ON tp.person_id = p.id
     LEFT JOIN team                AS t   ON tp.team_id = t.id
     LEFT JOIN organisation_person AS op  ON op.person_id = p.id
     LEFT JOIN organisation        AS o   ON op.organisation_id = o.id
WHERE p.email = :email

-- name: person-by-id-with-teams
SELECT p.*,
      o.max_person_count           AS sp_max_person_count,
      o.stripe_plan_id             AS sp_id,
      o.stripe_subscription_id     AS sp_subscription_id,

      o.free_trial_ends_at         AS organisation_free_trial_ends_at,
      o.id                         AS organisation_id,
      o.title                      AS organisation_title,
      op.is_admin                  AS is_organisation_admin,

      t.id                         AS team_id,
      t.title                      AS team_title,
      tp.is_admin                  AS is_team_admin,
      t.organisation_id            AS team_organisation_id,
      t.colour                     AS team_colour,
      t.latest_board_id            AS latest_board_id
FROM person                        AS p
     LEFT JOIN team_person         AS tp  ON tp.person_id = p.id
     LEFT JOIN team                AS t   ON tp.team_id = t.id
     LEFT JOIN organisation_person AS op  ON op.person_id = p.id
     LEFT JOIN organisation        AS o   ON op.organisation_id = o.id
WHERE p.id = :id

-- name: person-by-id
SELECT *
FROM person
WHERE id = :id

-- name: person-by-email
SELECT *
FROM person
WHERE email = :email

-- name: email-verified!
UPDATE person
SET is_email_verified       = true,
    email_verification_code = uuid_generate_v4()
WHERE id = :person_id
      AND email_verification_code = :verification_code

-- name: add-to-team!
INSERT INTO team_person (person_id, team_id)
VALUES (:person_id, :team_id)

-- name: password-reset!
UPDATE person
SET password_reset_code = uuid_generate_v4(),
    password = :password
WHERE id = :person_id
      AND password_reset_code = :code
