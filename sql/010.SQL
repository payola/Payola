create table ANALYSESRESULTS
(
   ANALYSISID varchar(36) not null,
   _PERSIST boolean default false,
   EVALUATIONID varchar(36) not null,
   VERTICESCOUNT int,
   _TOUCHED timestamp,
   _name varchar(128) not null,
   ownerid varchar(36),
   ID varchar(36) not null,
   foreign key(ANALYSISID) references ANALYSES(ID),
   unique UNI_ANALYSESRESULTS (EVALUATIONID)
);

alter table ONTOLOGYCUSTOMIZATIONS add userDefined bit;
alter table ontologycustomizations alter column ontologyurls rename to urls;
alter table classcustomizations alter column ontologycustomizationid rename to customizationid;
alter table classcustomizations add _conditionalvalue clob