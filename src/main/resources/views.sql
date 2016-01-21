drop view if exists candidate_view, ballot_item_view, user_view;

create view candidate_view as
select 
candidate.id,
poll_id,
discussion_id,
discussion.subject,
user_id
from candidate
inner join discussion
on candidate.discussion_id = discussion.id;

create view ballot_item_view as 
select ballot_item.id,
ballot_id,
candidate_id,
rank,
poll_id,
user_id
from ballot_item
inner join ballot
on ballot.id = ballot_item.ballot_id;

create view user_view as 
select user.id,
ip_address,
name,
password_encrypted
from user
left join full_user
on full_user.user_id = user.id;

