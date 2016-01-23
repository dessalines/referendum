drop view if exists candidate_view, ballot_view, user_view, poll_view;

create view candidate_view as
select 
candidate.id,
candidate.poll_id,
discussion_id,
discussion.subject,
discussion.text,
candidate.user_id
from candidate
inner join discussion
on candidate.discussion_id = discussion.id;

create view ballot_view as 
select ballot.poll_id,
ballot.user_id,
ballot.candidate_id,
ballot.rank
from ballot;




create view user_view as 
select user.id,
ip_address,
name,
password_encrypted
from user
left join full_user
on full_user.user_id = user.id;

create view poll_view as 
select poll.id,
poll_type_id,
poll_type.name as poll_type_name,
poll_sum_type_id,
poll_sum_type.name as poll_sum_type_name,
private_password,
discussion_id,
user_id,
subject,
text
from poll
inner join discussion
on discussion.id = poll.discussion_id
inner join poll_type 
on poll_type.id = poll.poll_type_id
inner join poll_sum_type
on poll_sum_type.id = poll.poll_sum_type_id;


select poll_id,
candidate_id,
avg(rank) as avg,
count(*) as votes_count
from ballot
group by poll_id,
candidate_id;
