-- name: get-board
SELECT *
FROM board
WHERE id = :board_id

-- name: get-team-tickets
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

       qt.queue_id AS queue__id,

       pass.id AS assignee__id,
       pass.first_name AS assignee__first_name,
       pass.last_name AS assignee__last_name,

       pauth.id AS author__id,
       pauth.first_name AS author__first_name,
       pauth.last_name AS author__last_name,

       tparent.id AS parent__id,
       tparent.title AS parent__title
FROM ticket AS t
     LEFT JOIN ticket AS tparent ON t.epic_id = tparent.id
     LEFT JOIN queue_ticket AS qt ON ( t.id = qt.ticket_id AND qt.board_id = :board_id )
     LEFT JOIN person AS pauth ON pauth.id = t.author
     LEFT JOIN person AS pass ON pass.id = t.assignee
WHERE t.team_id = :team_id
      AND t.is_closed = FALSE
      AND NOT EXISTS( SELECT * FROM ticket WHERE epic_id = t.id )
ORDER BY ticket__created_at ASC

-- name: get-board-queues
SELECT * FROM queue WHERE team_id = :team_id

-- name: get-board-and-queues
SELECT board.id AS board_id,
       board.title AS board_title,
       board.team_id AS team_id,
       board.queue_order AS queue_order,
       board.created_at AS board_created_at,

       queue.id AS queue_id,
       queue.title AS queue_title,
       queue.ticket_order AS ticket_order,
       queue.board_id AS board_id,
       queue.created_at AS queue_created_at
FROM board
       LEFT JOIN queue ON queue.board_id = board.id
WHERE board.id = :board_id
