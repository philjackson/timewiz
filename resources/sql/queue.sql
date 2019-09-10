-- name: get-queue
SELECT *
FROM queue
WHERE id = :queue_id

-- name: queue-ordering-remove-id!
UPDATE board
SET queue_order = array_remove(queue_order, :queue_id)
WHERE id = :board_id;

-- name: queue-ordering-prepend-id!
UPDATE board
SET queue_order = array_prepend(:queue_id, queue_order)
WHERE id = :board_id;

-- name: queue-ordering-append-id!
UPDATE board
SET queue_order = array_append(queue_order, :queue_id)
WHERE id = :board_id;
