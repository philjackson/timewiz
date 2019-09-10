--name: tickets-created-closed-within-range-for-team
SELECT SUM(CASE WHEN event_type = 'create' THEN 1 ELSE 0 end) AS created_count,
       SUM(CASE WHEN event_type = 'open' THEN 1 ELSE 0 end) AS reopened_count,
       SUM(CASE WHEN event_type = 'close' THEN 1 ELSE 0 end) AS closed_count,
       date_trunc(:trunc_at, created_at) AS grouped_date
FROM event_ticket_open_close
WHERE team_id = :team_id
      AND created_at >= :from
      AND created_at < :to
GROUP BY grouped_date;
