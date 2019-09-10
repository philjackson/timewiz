-- name: comments
SELECT c1.id AS comment__id,
       c1.comment AS comment__comment,
       c1.root_id AS comment__root_id,
       c1.parent_id AS comment__parent_id,
       EXISTS( SELECT * FROM comment WHERE parent_id = c1.id ) AS comment__has_children,
       c1.created_at AS comment__created_at,

       pauth.id AS author__id,
       pauth.first_name AS author__first_name,
       pauth.last_name AS author__last_name

FROM comment c1
     LEFT JOIN person AS pauth ON pauth.id = c1.author
WHERE c1.root_id = :root_id
      AND is_deleted = FALSE
ORDER BY comment__has_children DESC, c1.created_at DESC
LIMIT 100;

-- name: get-comment
SELECT c1.id AS comment__id,
       c1.comment AS comment__comment,
       c1.root_id AS comment__root_id,
       c1.parent_id AS comment__parent_id,

       EXISTS( SELECT * FROM comment WHERE parent_id = c1.id ) AS comment__has_children,
       c1.created_at AS comment__created_at,

       pauth.id AS author__id,
       pauth.first_name AS author__first_name,
       pauth.last_name AS author__last_name

FROM comment c1
     LEFT JOIN person AS pauth ON pauth.id = c1.author
WHERE c1.id = :comment_id;
