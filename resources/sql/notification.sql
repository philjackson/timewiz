--name: fetch-unread-for-person*
SELECT *
FROM notification
WHERE is_read = FALSE
      AND recipient_id = :recipient_id

--name: fetch-read-for-person*
SELECT *
FROM notification
WHERE is_read = TRUE
      AND recipient_id = :recipient_id
LIMIT :limit
