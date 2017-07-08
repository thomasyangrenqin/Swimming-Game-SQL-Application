--- Create tables
DROP TABLE IF EXISTS Swim CASCADE;
DROP TABLE IF EXISTS Heat CASCADE;
DROP TABLE IF EXISTS StrokeOf CASCADE;
DROP TABLE IF EXISTS Event CASCADE;
DROP TABLE IF EXISTS Participant CASCADE;
DROP TABLE IF EXISTS Meet CASCADE;
DROP TABLE IF EXISTS Distance CASCADE;
DROP TABLE IF EXISTS Stroke CASCADE;
DROP TABLE IF EXISTS Leg CASCADE;
DROP TABLE IF EXISTS Org CASCADE;
DROP TABLE IF EXISTS Score CASCADE;


CREATE TABLE Org(
    id VARCHAR(50),
    name VARCHAR(50),
    is_univ BOOLEAN,
    PRIMARY KEY (id)
);


-- Lookup table
CREATE TABLE Leg(
  leg INT CHECK (leg >0),
  PRIMARY KEY (Leg)
);

-- Lookup table
CREATE TABLE Stroke(
  stroke VARCHAR(50),
  PRIMARY KEY (stroke)
);

-- Lookup table
CREATE TABLE Distance(
  distance INT CHECK (distance >0),
  PRIMARY KEY (distance)
);

CREATE TABLE Meet(
  name VARCHAR(50),
  start_date DATE,
  num_days INT,
  org_id VARCHAR(50) NOT NULL,
  PRIMARY KEY (name),
  FOREIGN KEY (org_id) REFERENCES Org(id)
);

CREATE TABLE Participant(
  id VARCHAR(50),
  name VARCHAR(50),
  gender VARCHAR(1),
    CONSTRAINT chk_gender CHECK (gender IN ('M', 'F')),
  org_id VARCHAR(50) NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (org_id) REFERENCES Org(id)
);

CREATE TABLE Event(
  id VARCHAR(50),
  gender VARCHAR(1),
    CONSTRAINT chk_gender CHECK (gender IN ('M', 'F')),
  distance INT CHECK (distance >0),
  PRIMARY KEY (id),
  FOREIGN KEY (distance) REFERENCES Distance(distance)
);

CREATE TABLE StrokeOf(
    event_id VARCHAR,
    leg INT,
    stroke VARCHAR(50),
    PRIMARY KEY (event_id,leg),
    FOREIGN KEY (event_id) REFERENCES Event(id),
    FOREIGN KEY (leg) REFERENCES Leg(leg),
    FOREIGN KEY (stroke) REFERENCES Stroke(stroke)
);

CREATE TABLE Heat(
  id INT CHECK (id > 0),
  event_id VARCHAR(50),
  meet_name VARCHAR(50),
  PRIMARY KEY (id,event_id,meet_name),
  FOREIGN KEY (event_id) REFERENCES Event(id),
  FOREIGN KEY (meet_name) REFERENCES Meet(name)
);

CREATE TABLE Swim(
  heat_id INT,
  event_id VARCHAR(50),
  meet_name VARCHAR(50),
  Participant_id VARCHAR(50),
  leg INT,
  time DOUBLE PRECISION,
  PRIMARY KEY (heat_id,event_id,meet_name,Participant_id),
  FOREIGN KEY (heat_id,event_id,meet_name) REFERENCES Heat(id,event_id,meet_name),
  FOREIGN KEY (Participant_id) REFERENCES Participant(id),
  FOREIGN KEY (leg) REFERENCES Leg(leg)
);

CREATE TABLE Score(
    id SERIAL,
  normal_score INT,
  relay_score INT,
  PRIMARY KEY (id)
);

INSERT INTO Score(normal_score,relay_score) VALUES 
  (6,8),
  (4,4),
  (3,2),
  (2,NULL),
  (1,NULL);



--- Create insert functions
CREATE OR REPLACE FUNCTION InsertOrg(insert_id VARCHAR(50), insert_name VARCHAR(50), insert_is_univ BOOLEAN)
RETURNS VOID
AS $insert1$
  BEGIN
    INSERT INTO Org VALUES (insert_id, insert_name, insert_is_univ)
    ON CONFLICT (id) DO UPDATE
    SET name = EXCLUDED.name,
    is_univ = EXCLUDED.is_univ;
  END $insert1$
LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION InsertLeg(insert_leg INT)
RETURNS VOID
AS $insert2$
  BEGIN
    INSERT INTO Leg VALUES (insert_leg)
    ON CONFLICT (leg) DO NOTHING;
  END $insert2$
LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION InsertStroke(insert_stroke VARCHAR(50))
RETURNS VOID
AS $insert3$
  BEGIN
    INSERT INTO Stroke VALUES (insert_stroke)
    ON CONFLICT (stroke) DO NOTHING;
  END $insert3$
LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION InsertDistance(insert_distance INT)
RETURNS VOID
AS $insert4$
  BEGIN
    INSERT INTO Distance VALUES (insert_distance)
    ON CONFLICT (distance) DO NOTHING;
  END $insert4$
LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION InsertMeet(insert_name VARCHAR(50), insert_start_date DATE, insert_num_day INT, insert_org_id VARCHAR(50))
RETURNS VOID
AS $insert5$
  BEGIN
    INSERT INTO Meet VALUES (insert_name, insert_start_date,insert_num_day, insert_org_id)
    ON CONFLICT (name) DO UPDATE
    SET start_date = EXCLUDED.start_date,
      num_days = EXCLUDED.num_days,
      org_id = EXCLUDED.org_id;
  END $insert5$
LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION InsertParticipant(insert_id VARCHAR(50),insert_name VARCHAR(50), insert_gender VARCHAR(1), insert_org_id VARCHAR(50))
RETURNS VOID
AS $insert6$
  BEGIN
    INSERT INTO Participant VALUES (insert_id, insert_name, insert_gender, insert_org_id)
    ON CONFLICT (id) DO UPDATE
    SET name = EXCLUDED.name,
        gender = EXCLUDED.gender,
    org_id = EXCLUDED.org_id;
  END $insert6$
LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION InsertEvent(insert_id VARCHAR(50), insert_gender VARCHAR(1),insert_distance INT)
RETURNS VOID
AS $insert7$
  BEGIN
    INSERT INTO Event VALUES (insert_id, insert_gender,insert_distance)
    ON CONFLICT (id) DO UPDATE
    SET gender = EXCLUDED.gender,
    distance = EXCLUDED.distance;
  END $insert7$
LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION InsertStrokeOf(insert_event_id VARCHAR(50), insert_leg INT,insert_stroke VARCHAR(50))
RETURNS VOID
AS $insert8$
  BEGIN
    INSERT INTO StrokeOf VALUES (insert_event_id, insert_leg,insert_stroke)
    ON CONFLICT (event_id,leg) DO UPDATE
    SET stroke = EXCLUDED.stroke;
  END $insert8$
LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION InsertHeat(insert_id INT, insert_event_id VARCHAR(50), insert_meet_name VARCHAR(50))
RETURNS VOID
AS $insert9$
  BEGIN
    INSERT INTO Heat VALUES (insert_id, insert_event_id, insert_meet_name)
    ON CONFLICT (id,event_id,meet_name) DO NOTHING;
  END $insert9$
LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION InsertSwim(insert_heat_id INT, insert_event_id VARCHAR(50), 
  insert_meet_name VARCHAR(50),insert_participant_id VARCHAR(50), insert_leg INT, insert_time DOUBLE PRECISION)
RETURNS VOID
AS $insert10$
  BEGIN
    INSERT INTO Swim VALUES (insert_heat_id, insert_event_id, insert_meet_name, 
      insert_participant_id, insert_leg, insert_time)
    ON CONFLICT (heat_id,event_id,meet_name,Participant_id) DO UPDATE
    SET leg = EXCLUDED.leg,
    time = EXCLUDED.time;
  END $insert10$
LANGUAGE plpgsql;

--- Create triggers
CREATE OR REPLACE FUNCTION Swimtrig() 
RETURNS TRIGGER
AS $trigger1$
    BEGIN
    IF NEW.leg <1 OR NEW.leg>4 THEN
        RAISE EXCEPTION 'leg cannot be negative or larger than 4'; 
    ELSIF NEW.leg IN
    (SELECT Swim.leg as leg FROM Swim
    INNER JOIN Participant ON Participant.id = Swim.Participant_id
    INNER JOIN Org ON Org.id = Participant.org_id
    WHERE Org.id = (SELECT Org.id AS uid FROM Participant
    INNER JOIN Org ON Org.id = Participant.org_id
    WHERE Participant.id = NEW.Participant_id)
    AND Swim.meet_name = NEW.meet_name
    AND Swim.event_id = NEW.event_id
    AND Swim.heat_id = NEW.heat_id)
    THEN
        RAISE EXCEPTION 'swimmer cannot take the same leg with his/her teammates';
    ELSIF (SELECT gender FROM Participant
    WHERE Participant.id = NEW.Participant_id)
    NOT IN
    (SELECT gender FROM Event
    WHERE Event.id = NEW.event_id)
    THEN
        RAISE EXCEPTION 'the gender of the swimmer is not correct for this event';
    END IF;
    RETURN NEW; 
    END $trigger1$
LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS swim_trig ON Swim;
CREATE TRIGGER swim_trig BEFORE INSERT OR UPDATE ON Swim FOR EACH ROW EXECUTE PROCEDURE Swimtrig();

--- Create query functions
CREATE OR REPLACE FUNCTION heat_sheet1(the_name VARCHAR(50))
RETURNS TABLE(event VARCHAR(50),leg INT,heat INT, 
              swimmer_id VARCHAR(50),swimmer VARCHAR(50), school VARCHAR(50),
              person_time DOUBLE PRECISION, group_time DOUBLE PRECISION, rank BIGINT)
RETURNS NULL ON NULL INPUT
AS $query1$
    BEGIN
    RETURN query
    SELECT * FROM
    (SELECT Swim.event_id as event,Swim.leg as leg,Swim.heat_id as heat, 
    Swim.Participant_id as swimmer_id, Participant.name as swimmer,
     Org.name as school,Swim.time as personal_time, Swim.time as group_time, Y.rank as rank FROM Swim
    INNER JOIN Participant ON Participant.id = Swim.Participant_id
    INNER JOIN Org ON Org.id = Participant.org_id
    INNER JOIN
    (SELECT event_id,Participant_id, MIN(time), rank() OVER (PARTITION BY Swim.event_id ORDER BY MIN(time)) FROM Swim
    WHERE Swim.meet_name = the_name AND Swim.event_id NOT IN
     (SELECT event_id FROM Swim
        WHERE Swim.leg >1
        GROUP BY Swim.event_id,Swim.leg)
    GROUP BY Swim.event_id,Participant_id) AS Y ON Y.event_id = Swim.event_id AND Y.Participant_id = Swim.Participant_id
    WHERE Swim.meet_name = the_name AND Swim.event_id NOT IN
     (SELECT event_id FROM Swim
        WHERE Swim.leg >1
        GROUP BY Swim.event_id,Swim.leg)
    ORDER BY Swim.heat_id) as table1
    UNION ALL
    SELECT * FROM
    (SELECT Swim.event_id as event,Swim.leg as leg,Swim.heat_id as heat, 
    Swim.Participant_id as swimmer_id, Participant.name as swimmer,
    Org.name as school, Swim.time as personal_time, G.group_time as group_time,
    Y.rank as rank FROM Swim
    INNER JOIN Participant ON Participant.id = Swim.Participant_id
    INNER JOIN Org ON Org.id = Participant.org_id
    INNER JOIN
    (SELECT Swim.event_id as event,Swim.heat_id as heat, Org.id as school_id,SUM(Swim.time) as group_time
    FROM Swim
    INNER JOIN Participant ON Participant.id = Swim.Participant_id
    INNER JOIN Org ON Org.id = Participant.org_id
    WHERE Swim.meet_name = the_name AND Swim.event_id IN
     (SELECT event_id FROM Swim
        WHERE Swim.leg >1
        GROUP BY Swim.event_id,Swim.leg)
    GROUP BY Swim.event_id,Swim.heat_id,Org.id) AS G
    ON G.event = Swim.event_id AND G.school_id = Org.id AND G.heat = Swim.heat_id
    INNER JOIN
    (SELECT g.event as event, g.school_id as school, 
     MIN(g.group_time), rank() OVER (PARTITION BY g.event ORDER BY MIN(g.group_time)) FROM 
    (SELECT Swim.event_id as event, Swim.heat_id as heat,Org.id as school_id,SUM(Swim.time) as group_time
    FROM Swim
    INNER JOIN Participant ON Participant.id = Swim.Participant_id
    INNER JOIN Org ON Org.id = Participant.org_id
    WHERE Swim.meet_name = the_name AND Swim.event_id IN
     (SELECT event_id FROM Swim
        WHERE Swim.leg >1
        GROUP BY Swim.event_id,Swim.leg)
    GROUP BY Swim.event_id,Swim.heat_id,Org.id) AS g
    GROUP BY g.event,g.school_id
    ) AS Y ON Y.event = Swim.event_id AND Y.school = Org.id
    WHERE Swim.meet_name = the_name AND Swim.event_id IN
     (SELECT event_id FROM Swim
        WHERE Swim.leg >1
        GROUP BY Swim.event_id,Swim.leg)
    ORDER BY Swim.heat_id) as table2
    ORDER BY heat;
  RETURN;
  END $query1$
LANGUAGE plpgsql 
STABLE;


CREATE OR REPLACE FUNCTION heat_sheet2(the_name VARCHAR(50), p_name VARCHAR(50))
RETURNS TABLE (even VARCHAR(50),leg INT, 
               heat INT, swimmer_id VARCHAR(50),swimmer VARCHAR(50), 
               school VARCHAR(50),person_time DOUBLE PRECISION, group_time DOUBLE PRECISION, rank BIGINT)
RETURNS NULL ON NULL INPUT
AS $query2$
  BEGIN
        RETURN query
        SELECT * FROM heat_sheet1(the_name) as f
        WHERE f.swimmer = p_name;
        RETURN;
  END $query2$
LANGUAGE plpgsql 
STABLE;


CREATE OR REPLACE FUNCTION heat_sheet3(the_name VARCHAR(50), school_name VARCHAR(50))
RETURNS TABLE (even VARCHAR(50),leg INT, 
               heat INT, swimmer_id VARCHAR(50),swimmer VARCHAR(50), 
               school VARCHAR(50),person_time DOUBLE PRECISION, group_time DOUBLE PRECISION, rank BIGINT)
RETURNS NULL ON NULL INPUT
AS $query3$
  BEGIN
        RETURN query
        SELECT * FROM heat_sheet1(the_name) as f
        WHERE f.school = school_name;
        RETURN;
  END $query3$
LANGUAGE plpgsql 
STABLE;


CREATE OR REPLACE FUNCTION heat_sheet4(the_name VARCHAR(50), school_name VARCHAR(50))
RETURNS TABLE(swimmer_name VARCHAR(50))
RETURNS NULL ON NULL INPUT
AS $query4$
  BEGIN
        RETURN query
    SELECT DISTINCT Participant.name as swimmer_name FROM Swim
    INNER JOIN Participant ON Participant.id = Swim.Participant_id
    INNER JOIN Org ON Org.id = Participant.org_id
    WHERE Swim.meet_name = the_name AND Org.name = school_name;
    RETURN;
  END $query4$
LANGUAGE plpgsql
STABLE;


CREATE OR REPLACE FUNCTION heat_sheet5(the_name VARCHAR(50), this_event_id VARCHAR(50))
RETURNS TABLE (even VARCHAR(50),leg INT, 
               heat INT, swimmer_id VARCHAR(50),swimmer VARCHAR(50), school VARCHAR(50),
               person_time DOUBLE PRECISION, group_time DOUBLE PRECISION, rank BIGINT)
RETURNS NULL ON NULL INPUT
AS $query5$
  BEGIN
        RETURN query
        SELECT * FROM heat_sheet1(the_name) as f
        WHERE f.event = this_event_id
        ORDER BY f.group_time;
        RETURN;
  END $query5$
LANGUAGE plpgsql 
STABLE;


CREATE OR REPLACE FUNCTION heat_sheet6(the_name VARCHAR(50))
RETURNS TABLE (school VARCHAR(50), sum BIGINT)
RETURNS NULL ON NULL INPUT
AS $query6$
    BEGIN
        RETURN query
        SELECT q1.school as school,coalesce(q2.sum, 0) as sum FROM
        (SELECT DISTINCT (Org.name) as school FROM Swim
        INNER JOIN Participant ON Participant.id = Swim.Participant_id
        INNER JOIN Org ON Org.id = Participant.org_id
        WHERE Swim.meet_name = the_name) as q1
        LEFT JOIN
        (SELECT U.school, SUM(U.score) FROM
        (SELECT f1.school as school,f1.rank as rank, Score.normal_score as score FROM
        (SELECT f.event, f.school, f.rank 
         FROM heat_sheet1(the_name) as f
         WHERE f.rank <= 5 AND  f.event NOT IN
        (SELECT event_id FROM Swim
             WHERE Swim.leg >1
             GROUP BY Swim.event_id,Swim.leg))AS f1
        INNER JOIN Score ON Score.id = f1.rank
        UNION ALL
        SELECT f2.school as school,f2.rank as rank, Score.relay_score as score FROM
        (SELECT f.event, f.school, f.rank 
         FROM heat_sheet1(the_name) as f
         WHERE f.rank <= 3 AND f.event IN
         (SELECT event_id FROM Swim
            WHERE Swim.leg >1
            GROUP BY Swim.event_id,Swim.leg)
         GROUP BY f.event,f.school,f.rank)AS f2
        INNER JOIN Score ON Score.id = f2.rank) AS U
        GROUP BY U.school) as q2
        ON q2.school = q1.school
        ORDER BY sum DESC;
        RETURN;
  END $query6$
LANGUAGE plpgsql
STABLE;


--- Create functions for saving csv file
CREATE OR REPLACE FUNCTION saveOrg()
RETURNS TABLE(id VARCHAR(50),name VARCHAR(50),is_univ BOOLEAN)
AS $$
    BEGIN
    RETURN QUERY
    SELECT * FROM Org;
    RETURN;
END $$
LANGUAGE plpgsql
STABLE;

CREATE OR REPLACE FUNCTION saveLeg()
RETURNS TABLE(leg INT)
AS $$
    BEGIN
    RETURN QUERY
    SELECT * FROM Leg;
    RETURN;
END $$
LANGUAGE plpgsql
STABLE;

CREATE OR REPLACE FUNCTION saveStroke()
RETURNS TABLE(stroke VARCHAR(50))
AS $$
    BEGIN
    RETURN QUERY
    SELECT * FROM Stroke;
    RETURN;
END $$
LANGUAGE plpgsql
STABLE;

CREATE OR REPLACE FUNCTION saveDistance()
RETURNS TABLE(distance INT)
AS $$
    BEGIN
    RETURN QUERY
    SELECT * FROM Distance;
    RETURN;
END $$
LANGUAGE plpgsql
STABLE;

CREATE OR REPLACE FUNCTION saveMeet()
RETURNS TABLE(name VARCHAR(50),start_date DATE,num_days INT,org_id VARCHAR(50))
AS $$
    BEGIN
    RETURN QUERY
    SELECT * FROM Meet;
    RETURN;
END $$
LANGUAGE plpgsql
STABLE;

CREATE OR REPLACE FUNCTION saveParticipant()
RETURNS TABLE(id VARCHAR(50),name VARCHAR(50),gender VARCHAR(1),org_id VARCHAR(50))
AS $$
    BEGIN
    RETURN QUERY
    SELECT * FROM Participant;
    RETURN;
END $$
LANGUAGE plpgsql
STABLE;

CREATE OR REPLACE FUNCTION saveEvent()
RETURNS TABLE(id VARCHAR(50),gender VARCHAR(1),distance INT)
AS $$
    BEGIN
    RETURN QUERY
    SELECT * FROM Event;
    RETURN;
END $$
LANGUAGE plpgsql
STABLE;

CREATE OR REPLACE FUNCTION saveStrokeOf()
RETURNS TABLE(event_id VARCHAR,leg INT,stroke VARCHAR(50))
AS $$
    BEGIN
    RETURN QUERY
    SELECT * FROM StrokeOf;
    RETURN;
END $$
LANGUAGE plpgsql
STABLE;

CREATE OR REPLACE FUNCTION saveHeat()
RETURNS TABLE(id INT,event_id VARCHAR(50),meet_name VARCHAR(50))
AS $$
    BEGIN
    RETURN QUERY
    SELECT * FROM Heat;
    RETURN;
END $$
LANGUAGE plpgsql
STABLE;

CREATE OR REPLACE FUNCTION saveSwim()
RETURNS TABLE(heat_id INT,event_id VARCHAR(50),meet_name VARCHAR(50),
              Participant_id VARCHAR(50),leg INT, swim_time DOUBLE PRECISION)
AS $$
    BEGIN
    RETURN QUERY
    SELECT * FROM Swim;
    RETURN;
END $$
LANGUAGE plpgsql
STABLE;