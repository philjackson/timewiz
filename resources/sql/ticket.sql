    -- name: get-ticket
    SELECT t.id AS ticket__id,
       t.title AS ticket__title,
       t.description AS ticket__description,
       t.team_id AS ticket__team_id,
       t.created_at AS ticket__created_at,
       t.updated_at AS ticket__updated_at,
       t.epic_id AS ticket__epic_id,
       t.is_closed AS ticket__is_closed,
       t.assignee AS ticket__assignee,
       t.author AS ticket__author,
       t.due_by AS ticket__due_by,
       t.tags AS ticket__tags,

       pass.id AS assignee__id,
       pass.first_name AS assignee__first_name,
       pass.last_name AS assignee__last_name,

       pauth.id AS author__id,
       pauth.first_name AS author__first_name,
       pauth.last_name AS author__last_name,
       EXISTS( SELECT * FROM ticket WHERE epic_id = t.id ) AS ticket__is_epic
FROM ticket AS t
     LEFT JOIN person AS pass ON pass.id = t.assignee
     LEFT JOIN person AS pauth ON pauth.id = t.author
WHERE t.id = :ticket_id;

-- name: close-all-children!
UPDATE ticket
SET is_closed = TRUE
WHERE epic_id = :epic_id

-- name: move-children-of-epic-to-team!
UPDATE ticket
SET team_id = :team_id,
    assignee = null,
    queue_id = null
WHERE epic_id = :epic_id;

-- name: get-children
SELECT t.id AS ticket__id,
       t.title AS ticket__title,
       t.description AS ticket__description,
       t.team_id AS ticket__team_id,
       t.created_at AS ticket__created_at,
       t.updated_at AS ticket__updated_at,
       t.epic_id AS ticket__epic_id,
       t.is_closed AS ticket__is_closed,
       t.assignee AS ticket__assignee,
       t.author AS ticket__author,
       t.tags AS ticket__tags,

       pass.id AS assignee__id,
       pass.first_name AS assignee__first_name,
       pass.last_name AS assignee__last_name,

       pauth.id AS author__id,
       pauth.first_name AS author__first_name,
       pauth.last_name AS author__last_name,
       EXISTS( SELECT * FROM ticket WHERE epic_id = t.id ) AS ticket__is_epic
FROM ticket AS t
     LEFT JOIN person AS pass ON pass.id = t.assignee
     LEFT JOIN person AS pauth ON pauth.id = t.author
WHERE t.epic_id = :epic_id;

-- name: ticket-remove-tag!
UPDATE ticket
SET tags = array_remove(tags, :tag::text)
WHERE id = :ticket_id;

-- name: ticket-prepend-tag!
UPDATE ticket
SET tags = array_prepend(:tag::text, tags)
WHERE id = :ticket_id;
