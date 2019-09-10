-- name: get-valid-invitation
SELECT *
FROM team_invitation
WHERE id = :invite_id
      AND accepted_time IS NULL

-- name: get-invitees-by-team
SELECT *
FROM team_invitation
WHERE team_id = :team_id
      AND accepted_time IS NULL
ORDER BY created_at DESC;
