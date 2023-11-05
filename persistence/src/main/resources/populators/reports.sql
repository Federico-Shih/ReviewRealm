INSERT INTO reports(id, reason,submissiondate, reviewid, reporteduserid, reporterid, state) VALUES
                (1,'SPAM',DATE '2090-11-30',1,1,2,'UNRESOLVED'),
                (2,'PIRACY',DATE '2090-12-15',2,2,3,'UNRESOLVED'),
                (3,'SPAM',DATE '2090-12-15',2,2,3,'UNRESOLVED');
ALTER SEQUENCE report_id_seq RESTART WITH 4;