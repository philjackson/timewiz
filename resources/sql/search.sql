-- name: search-tickets
SELECT *, EXISTS( SELECT * FROM ticket WHERE epic_id = tic.id ) AS is_epic 
FROM ticket AS tic
WHERE team_id IN (
    SELECT t.team_id
    FROM team_person t
    WHERE t.person_id = :person_id)
  AND tic.search_index @@ plainto_tsquery(:query)
ORDER BY created_at DESC
LIMIT 100;

-- name: search-people
SELECT *
FROM person
WHERE id IN (
    SELECT person_id
    FROM team_person
    WHERE team_id IN (
        SELECT team_id
        FROM team_person tp
        WHERE tp.person_id = :person_id))
  AND search_index @@ plainto_tsquery(:query)
ORDER BY created_at DESC
LIMIT 100;
