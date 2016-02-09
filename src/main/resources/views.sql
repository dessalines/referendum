drop view if exists candidate_view, ballot_view, user_login_view, poll_view, comment_view,
poll_tag_view, tag_view, poll_ungrouped_view;

create view candidate_view as
select 
candidate.id,
candidate.poll_id,
discussion_id,
discussion.subject,
discussion.text,
candidate.user_id,
coalesce(full_user.name, concat('user_',candidate.user_id)) as user_name
from candidate
inner join discussion
on candidate.discussion_id = discussion.id
left join full_user
on candidate.user_id = full_user.user_id;

create view ballot_view as 
select ballot.poll_id,
ballot.user_id,
ballot.candidate_id,
ballot.rank
from ballot;

create view poll_tag_view as 
select poll_tag.id,
poll_tag.poll_id,
poll_tag.tag_id,
tag.name
from poll_tag
left join tag
on tag.id = poll_tag.tag_id;

create view tag_view as 
select id,
user_id,
name,
tag_visit_trending_hour.hits as hour_hits,
tag_visit_trending_hour.z_score as hour_score,
tag_visit_trending_day.hits as day_hits,
tag_visit_trending_day.z_score as day_score,
tag_visit_trending_week.hits as week_hits,
tag_visit_trending_week.z_score as week_score,
tag_visit_trending_month.hits as month_hits,
tag_visit_trending_month.z_score as month_score,
tag_visit_trending_year.hits as year_hits,
tag_visit_trending_year.z_score as year_score,
created,
modified
from tag
left join tag_visit_trending_hour
on tag_visit_trending_hour.tag_id = tag.id
left join tag_visit_trending_day
on tag_visit_trending_day.tag_id = tag.id
left join tag_visit_trending_week
on tag_visit_trending_week.tag_id = tag.id
left join tag_visit_trending_month
on tag_visit_trending_month.tag_id = tag.id
left join tag_visit_trending_year
on tag_visit_trending_year.tag_id = tag.id
group by tag.id;




create view user_login_view as 
select user.id,
ip_address,
name,
email,
password_encrypted,
auth,
expire_time
from user
left join full_user
on full_user.user_id = user.id
left join login 
on login.user_id = user.id;

create view poll_view as 
select poll.id,
poll_type_id,
poll_type.name as poll_type_name,
poll_sum_type_id,
poll_sum_type.name as poll_sum_type_name,
private_password,
poll.discussion_id,
poll.user_id,
coalesce(full_user.name, concat('user_',poll.user_id)) as user_name,
subject,
discussion.text,
count(comment.id) as number_of_comments,
tag.id as tag_id,
tag.name as tag_name,
poll_visit_trending_hour.hits as hour_hits,
poll_visit_trending_hour.z_score as hour_score,
poll_visit_trending_day.hits as day_hits,
poll_visit_trending_day.z_score as day_score,
poll_visit_trending_week.hits as week_hits,
poll_visit_trending_week.z_score as week_score,
poll_visit_trending_month.hits as month_hits,
poll_visit_trending_month.z_score as month_score,
poll_visit_trending_year.hits as year_hits,
poll_visit_trending_year.z_score as year_score,
poll.created,
poll.modified
from poll
inner join discussion
on discussion.id = poll.discussion_id
inner join poll_type 
on poll_type.id = poll.poll_type_id
inner join poll_sum_type
on poll_sum_type.id = poll.poll_sum_type_id
left join full_user
on poll.user_id = full_user.user_id
left join comment 
on comment.discussion_id = poll.discussion_id
left join poll_tag
on poll_tag.poll_id = poll.id
left join tag 
on tag.id = poll_tag.tag_id
left join poll_visit_trending_hour
on poll_visit_trending_hour.poll_id = poll.id
left join poll_visit_trending_day
on poll_visit_trending_day.poll_id = poll.id
left join poll_visit_trending_week
on poll_visit_trending_week.poll_id = poll.id
left join poll_visit_trending_month
on poll_visit_trending_month.poll_id = poll.id
left join poll_visit_trending_year
on poll_visit_trending_year.poll_id = poll.id
group by poll.id;

create view poll_ungrouped_view as 
select poll.id,
poll_type_id,
poll_type.name as poll_type_name,
poll_sum_type_id,
poll_sum_type.name as poll_sum_type_name,
private_password,
poll.discussion_id,
poll.user_id,
coalesce(full_user.name, concat('user_',poll.user_id)) as user_name,
subject,
discussion.text,
count(comment.id) as number_of_comments,
tag.id as tag_id,
tag.name as tag_name,
poll_visit_trending_hour.hits as hour_hits,
poll_visit_trending_hour.z_score as hour_score,
poll_visit_trending_day.hits as day_hits,
poll_visit_trending_day.z_score as day_score,
poll_visit_trending_week.hits as week_hits,
poll_visit_trending_week.z_score as week_score,
poll_visit_trending_month.hits as month_hits,
poll_visit_trending_month.z_score as month_score,
poll_visit_trending_year.hits as year_hits,
poll_visit_trending_year.z_score as year_score,
poll.created,
poll.modified
from poll
inner join discussion
on discussion.id = poll.discussion_id
inner join poll_type 
on poll_type.id = poll.poll_type_id
inner join poll_sum_type
on poll_sum_type.id = poll.poll_sum_type_id
left join full_user
on poll.user_id = full_user.user_id
left join comment 
on comment.discussion_id = poll.discussion_id
left join poll_tag
on poll_tag.poll_id = poll.id
left join tag 
on tag.id = poll_tag.tag_id
left join poll_visit_trending_hour
on poll_visit_trending_hour.poll_id = poll.id
left join poll_visit_trending_day
on poll_visit_trending_day.poll_id = poll.id
left join poll_visit_trending_week
on poll_visit_trending_week.poll_id = poll.id
left join poll_visit_trending_month
on poll_visit_trending_month.poll_id = poll.id
left join poll_visit_trending_year
on poll_visit_trending_year.poll_id = poll.id
group by poll.id, poll_tag.id;




-- select poll_id,
-- candidate_id,
-- avg(rank) as avg,
-- count(*) as votes_count
-- from ballot
-- group by poll_id,
-- candidate_id;

-- create view comment_view as
-- select 
-- comment.id,
-- comment.discussion_id,
-- text,
-- comment.user_id,
-- comment_tree.parent_id,
-- comment_tree.child_id,
-- path_length,
-- AVG(rank) as avg_rank,
-- comment.created,
-- comment.modified
-- from comment
-- inner join comment_tree
-- on comment.id = comment_tree.child_id
-- left join comment_rank
-- on comment.id = comment_rank.comment_id
-- group by parent_id;

create view comment_view as 
select 
comment.id,
comment.discussion_id,
poll.id as poll_id,
text,
comment.user_id,
coalesce(full_user.name, concat('user_',comment.user_id)) as user_name,
-- min(a.path_length,b.path_length),
AVG(c.rank) as avg_rank,
d.rank as user_rank,
GROUP_CONCAT(distinct b.parent_id order by b.path_length desc) AS breadcrumbs,
count(distinct b.parent_id) as derp,
comment.deleted,
comment.created,
comment.modified
from comment
JOIN comment_tree a ON (comment.id = a.child_id) 
JOIN comment_tree b ON (b.child_id = a.child_id) 
left join comment_rank c
on comment.id = c.comment_id
left join comment_rank d
on comment.id = d.comment_id 
-- and d.user_id = 1
left join poll on
comment.discussion_id = poll.discussion_id
left join full_user
on comment.user_id = full_user.user_id
-- where comment.discussion_id >= 0
-- and a.parent_id = 3 
-- and b.parent_id >= 10


-- and a.path_length = 1
-- where d.user_id = 2
GROUP BY a.child_id;



-- A z-score example
select * from (
select
    t.id,
    t.created,
    abs((t.created - tt.created_avg) / tt.created_stdev) as z_score
from poll_visit as t 
cross join (
	select 
     avg(tt.created) as created_avg,
     stddev(tt.created) as created_stdev
     from poll_visit as tt
   ) as tt
) a
order by a.z_score asc;


-- hits per last hour
select
poll_id,
count(id) as hits
from poll_visit
where created > DATE_SUB(NOW(), INTERVAL 1 HOUR)
group by poll_id;

-- historic hits per last hour
select poll_id,
date_format(created - interval 1 hour, '%Y-%m-%d-%H') AS hour_grouped,
count(created) as hits
from poll_visit
group by poll_id, hour_grouped;

-- average historic hits per hour
select poll_id,
avg(a.hits) as avg_hits,
stddev(a.hits) as std_hits
from (
select poll_id,
date_format(created - interval 1 hour, '%Y-%m') AS month_grouped,
count(created) as hits
from poll_visit
group by poll_id, month_grouped
) as a
group by poll_id;


-- average historic hits along with the last hit, the Z-FUCKING SCORE
-- from last hour
select poll_id,
hits,
avg_hits,
std_hits,
z_score
 from (
	select *,
	coalesce(((b.hits - tt.avg_hits) / tt.std_hits),-9999) as z_score
	from (
		select
		poll_id,
		count(*) as hits
		from poll_visit
		where created > DATE_SUB(NOW(), INTERVAL 1 HOUR)
		group by poll_id) as b
	cross join (
		select poll_id as poll_id_2,
		avg(a.hits) as avg_hits,
		stddev(a.hits) as std_hits
		from (
			select poll_id,
			date_format(created - interval 1 HOUR, '%Y-%m-%d-%H') AS hour_grouped,
			count(created) as hits
			from poll_visit
			group by poll_id, hour_grouped
		) as a
		group by poll_id
	) as tt
	on b.poll_id = tt.poll_id_2
	) as c
order by c.z_score desc;










