drop view candidate_view;

create view candidate_view as
select 
candidate.id,
poll_id,
discussion_id,
discussion.subject
from candidate
inner join discussion
on candidate.discussion_id = discussion.id;
