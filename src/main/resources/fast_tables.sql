

-- poll trending hour
DROP TABLE IF EXISTS temp;
create temporary table temp 
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

delete from poll_visit_trending_hour;
insert into poll_visit_trending_hour select * from temp;

-- poll trending day
DROP TABLE IF EXISTS temp;
create temporary table temp 
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
		where created > DATE_SUB(NOW(), INTERVAL 1 DAY)
		group by poll_id) as b
	cross join (
		select poll_id as poll_id_2,
		avg(a.hits) as avg_hits,
		stddev(a.hits) as std_hits
		from (
			select poll_id,
			date_format(created - interval 1 DAY, '%Y-%m-%d') AS day_grouped,
			count(created) as hits
			from poll_visit
			group by poll_id, day_grouped
		) as a
		group by poll_id
	) as tt
	on b.poll_id = tt.poll_id_2
	) as c
order by c.z_score desc;

delete from poll_visit_trending_day;
insert into poll_visit_trending_day select * from temp;

-- poll trending week
DROP TABLE IF EXISTS temp;
create temporary table temp 
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
		where created > DATE_SUB(NOW(), INTERVAL 1 WEEK)
		group by poll_id) as b
	cross join (
		select poll_id as poll_id_2,
		avg(a.hits) as avg_hits,
		stddev(a.hits) as std_hits
		from (
			select poll_id,
			date_format(created - interval 1 WEEK, '%Y-%u') AS week_grouped,
			count(created) as hits
			from poll_visit
			group by poll_id, week_grouped
		) as a
		group by poll_id
	) as tt
	on b.poll_id = tt.poll_id_2
	) as c
order by c.z_score desc;

delete from poll_visit_trending_week;
insert into poll_visit_trending_week select * from temp;

-- poll trending month
DROP TABLE IF EXISTS temp;
create temporary table temp 
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
		where created > DATE_SUB(NOW(), INTERVAL 1 MONTH)
		group by poll_id) as b
	cross join (
		select poll_id as poll_id_2,
		avg(a.hits) as avg_hits,
		stddev(a.hits) as std_hits
		from (
			select poll_id,
			date_format(created - interval 1 MONTH, '%Y-%m') AS month_grouped,
			count(created) as hits
			from poll_visit
			group by poll_id, month_grouped
		) as a
		group by poll_id
	) as tt
	on b.poll_id = tt.poll_id_2
	) as c
order by c.z_score desc;

delete from poll_visit_trending_month;
insert into poll_visit_trending_month select * from temp;

-- poll trending year
DROP TABLE IF EXISTS temp;
create temporary table temp 
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
		where created > DATE_SUB(NOW(), INTERVAL 1 YEAR)
		group by poll_id) as b
	cross join (
		select poll_id as poll_id_2,
		avg(a.hits) as avg_hits,
		stddev(a.hits) as std_hits
		from (
			select poll_id,
			date_format(created - interval 1 YEAR, '%Y') AS year_grouped,
			count(created) as hits
			from poll_visit
			group by poll_id, year_grouped
		) as a
		group by poll_id
	) as tt
	on b.poll_id = tt.poll_id_2
	) as c
order by c.z_score desc;

delete from poll_visit_trending_year;
insert into poll_visit_trending_year select * from temp;

-- Now the tag ones


-- tag trending hour
DROP TABLE IF EXISTS temp;
create temporary table temp 
select tag_id,
hits,
avg_hits,
std_hits,
z_score
 from (
	select *,
	coalesce(((b.hits - tt.avg_hits) / tt.std_hits),-9999) as z_score
	from (
		select
		tag_id,
		count(*) as hits
		from tag_visit
		where created > DATE_SUB(NOW(), INTERVAL 1 HOUR)
		group by tag_id) as b
	cross join (
		select tag_id as tag_id_2,
		avg(a.hits) as avg_hits,
		stddev(a.hits) as std_hits
		from (
			select tag_id,
			date_format(created - interval 1 HOUR, '%Y-%m-%d-%H') AS hour_grouped,
			count(created) as hits
			from tag_visit
			group by tag_id, hour_grouped
		) as a
		group by tag_id
	) as tt
	on b.tag_id = tt.tag_id_2
	) as c
order by c.z_score desc;

delete from tag_visit_trending_hour;
insert into tag_visit_trending_hour select * from temp;

-- tag trending day
DROP TABLE IF EXISTS temp;
create temporary table temp 
select tag_id,
hits,
avg_hits,
std_hits,
z_score
 from (
	select *,
	coalesce(((b.hits - tt.avg_hits) / tt.std_hits),-9999) as z_score
	from (
		select
		tag_id,
		count(*) as hits
		from tag_visit
		where created > DATE_SUB(NOW(), INTERVAL 1 DAY)
		group by tag_id) as b
	cross join (
		select tag_id as tag_id_2,
		avg(a.hits) as avg_hits,
		stddev(a.hits) as std_hits
		from (
			select tag_id,
			date_format(created - interval 1 DAY, '%Y-%m-%d') AS day_grouped,
			count(created) as hits
			from tag_visit
			group by tag_id, day_grouped
		) as a
		group by tag_id
	) as tt
	on b.tag_id = tt.tag_id_2
	) as c
order by c.z_score desc;

delete from tag_visit_trending_day;
insert into tag_visit_trending_day select * from temp;

-- tag trending week
DROP TABLE IF EXISTS temp;
create temporary table temp 
select tag_id,
hits,
avg_hits,
std_hits,
z_score
 from (
	select *,
	coalesce(((b.hits - tt.avg_hits) / tt.std_hits),-9999) as z_score
	from (
		select
		tag_id,
		count(*) as hits
		from tag_visit
		where created > DATE_SUB(NOW(), INTERVAL 1 WEEK)
		group by tag_id) as b
	cross join (
		select tag_id as tag_id_2,
		avg(a.hits) as avg_hits,
		stddev(a.hits) as std_hits
		from (
			select tag_id,
			date_format(created - interval 1 WEEK, '%Y-%u') AS week_grouped,
			count(created) as hits
			from tag_visit
			group by tag_id, week_grouped
		) as a
		group by tag_id
	) as tt
	on b.tag_id = tt.tag_id_2
	) as c
order by c.z_score desc;

delete from tag_visit_trending_week;
insert into tag_visit_trending_week select * from temp;

-- tag trending month
DROP TABLE IF EXISTS temp;
create temporary table temp 
select tag_id,
hits,
avg_hits,
std_hits,
z_score
 from (
	select *,
	coalesce(((b.hits - tt.avg_hits) / tt.std_hits),-9999) as z_score
	from (
		select
		tag_id,
		count(*) as hits
		from tag_visit
		where created > DATE_SUB(NOW(), INTERVAL 1 MONTH)
		group by tag_id) as b
	cross join (
		select tag_id as tag_id_2,
		avg(a.hits) as avg_hits,
		stddev(a.hits) as std_hits
		from (
			select tag_id,
			date_format(created - interval 1 MONTH, '%Y-%m') AS month_grouped,
			count(created) as hits
			from tag_visit
			group by tag_id, month_grouped
		) as a
		group by tag_id
	) as tt
	on b.tag_id = tt.tag_id_2
	) as c
order by c.z_score desc;

delete from tag_visit_trending_month;
insert into tag_visit_trending_month select * from temp;

-- tag trending year
DROP TABLE IF EXISTS temp;
create temporary table temp 
select tag_id,
hits,
avg_hits,
std_hits,
z_score
 from (
	select *,
	coalesce(((b.hits - tt.avg_hits) / tt.std_hits),-9999) as z_score
	from (
		select
		tag_id,
		count(*) as hits
		from tag_visit
		where created > DATE_SUB(NOW(), INTERVAL 1 YEAR)
		group by tag_id) as b
	cross join (
		select tag_id as tag_id_2,
		avg(a.hits) as avg_hits,
		stddev(a.hits) as std_hits
		from (
			select tag_id,
			date_format(created - interval 1 YEAR, '%Y') AS year_grouped,
			count(created) as hits
			from tag_visit
			group by tag_id, year_grouped
		) as a
		group by tag_id
	) as tt
	on b.tag_id = tt.tag_id_2
	) as c
order by c.z_score desc;

delete from tag_visit_trending_year;
insert into tag_visit_trending_year select * from temp;
