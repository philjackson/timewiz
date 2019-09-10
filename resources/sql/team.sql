-- name: add-person!
INSERT INTO team_person ("team_id", "person_id")
VALUES (:team_id, :person_id);

-- name: get-team
SELECT *
FROM team
WHERE id = :team_id;

-- name: person-in-team?
SELECT 1
FROM team_person
WHERE team_id = :team_id AND person_id = :person_id

-- name: team-members
SELECT person.id AS person__id,
       person.first_name AS person__first_name,
       person.last_name AS person__last_name,
       person.email AS person__email,

       team_person.is_admin AS member__is_admin,
       team_person.id AS member__id
FROM team_person
     LEFT JOIN person ON person_id = person.id
WHERE team_id = :team_id
ORDER BY last_name ASC;

-- name: get-member
SELECT *
FROM team_person
WHERE id = :member_id

-- name: unassign-team-tickets-from-person!
UPDATE ticket
SET assignee = NULL
WHERE assignee = ( SELECT person_id FROM team_person WHERE id = :member_id )
  AND team_id = :team_id
