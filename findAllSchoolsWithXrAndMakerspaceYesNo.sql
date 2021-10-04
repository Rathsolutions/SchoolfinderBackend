select distinct ar_content, latitude, longitude, school_name, makerspace_content, ar_content, alternative_picture_text, short_school_name, 
case when ((select count(*) from person_school_mapping psm where s.id = psm.school_id and psm.functionality = 1) ) then 'Ja' else 'nein' end as 'Makerspace Vorhanden',
case when ((select count(*) from person_school_mapping psm where s.id = psm.school_id and psm.functionality = 0) ) then 'Ja' else 'nein' end as 'XR Vorhanden'
from school s