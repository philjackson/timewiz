-- name: get-queue-ticket
SELECT *
FROM queue_ticket
WHERE queue_id = :queue_id AND ticket_id = :ticket_id
