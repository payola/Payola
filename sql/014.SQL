create table EMBEDDINGDESCRIPTION (
  URIHASH VARCHAR2(36) NOT NULL,
  DEFAULTVISUALPLUGIN VARCHAR2(128),
  analysisResultId VARCHAR(128) NOT NULL,
  id VARCHAR(36) NOT NULL,
  evaluationid varchar(36) not null,
  lastupdate timestamp,
  ownerid varchar(36),
  _name varchar(36),
  foreign key(analysisResultId ) references ANALYSESRESULTS(ID),
   unique UNI_EMBEDDINGDESCRIPTION  (URIHASH)
);