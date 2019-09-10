-- name: all-organisations
SELECT o.id AS organisation__id,
       o.title AS organisation__title,
       o.created_at AS organisation__created_at,
       admin.id AS admin__id,
       admin.first_name AS admin__first_name,
       admin.last_name AS admin__last_name
FROM organisation AS o
     LEFT JOIN organisation_person AS op ON op.organisation_id = o.id
     LEFT JOIN person AS admin ON op.person_id = admin.id
WHERE op.is_admin IS TRUE
ORDER BY o.created_at DESC

-- name: person-in-organisation?
SELECT 1
FROM organisation_person
WHERE organisation_id = :organisation_id AND person_id = :person_id

-- name: person-count
SELECT COUNT(organisation_person.id)
FROM organisation_person
WHERE organisation_person.organisation_id = :organisation_id

--name: get-organisation
SELECT *
FROM organisation
WHERE id = :id

-- name: get-organisation-by-title
SELECT *
FROM organisation
WHERE title = :title

--name: teams-in-organsiation
SELECT team.*, COUNT(tp.person_id) AS person_count
FROM team
     LEFT JOIN team_person AS tp ON tp.team_id = team.id
WHERE organisation_id = :organisation_id
GROUP BY team.id;
