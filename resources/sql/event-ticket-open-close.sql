--name: get-events
SELECT e.id AS event__id,
       e.created_at AS event__created_at,
       e.team_id    AS event__team_id,
       e.event_type AS event__event_type,

       p.id AS person__id,
       p.first_name AS person__first_name,
       p.last_name AS person__last_name
FROM event_ticket_open_close AS e
     LEFT JOIN person AS p ON p.id = e.person_id
WHERE ticket_id = :ticket_id
ORDER BY e.created_at DESC
LIMIT 20
