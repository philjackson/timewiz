-- name: backlog
SELECT *,
       EXISTS( SELECT *
         FROM ticket
         WHERE epic_id = t.id) AS is_epic,
       ( SELECT count(*)
         FROM ticket
              LEFT JOIN queue_ticket AS qt ON ( t.id = qt.ticket_id AND qt.board_id = :board_id )
         WHERE epic_id = t.id
               AND qt.id IS NULL
               AND is_closed = FALSE) AS unassigned_child_count
FROM ticket AS t
     LEFT JOIN queue_ticket AS qt ON ( t.id = qt.ticket_id AND qt.board_id = :board_id )
WHERE team_id = :team_id
      AND qt.id IS NULL
      AND t.is_closed = FALSE
ORDER BY is_epic DESC, t.created_at;

-- name: backlog-ordering-remove-id!
UPDATE team
SET backlog_order = array_remove(backlog_order, :ticket_id)
WHERE id = :team_id;

-- name: backlog-ordering-prepend-id!
UPDATE team
SET backlog_order = array_prepend(:ticket_id, backlog_order)
WHERE id = :team_id;
