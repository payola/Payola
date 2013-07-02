CREATE CACHED TABLE PUBLIC.PREFIXES( 
    _URL VARCHAR(256) NOT NULL, 
    _PREFIX VARCHAR(10) NOT NULL, 
    ID VARCHAR(36) NOT NULL, 
    OWNERID VARCHAR(36), 
    _NAME VARCHAR(128) NOT NULL 
);
ALTER TABLE PUBLIC.PREFIXES ADD CONSTRAINT PUBLIC.CONSTRAINT_E1 PRIMARY KEY(ID);
CREATE UNIQUE INDEX PUBLIC.IDX780F0986 ON PUBLIC.PREFIXES(OWNERID, _PREFIX);
CREATE UNIQUE INDEX PUBLIC.IDX5CEC084B ON PUBLIC.PREFIXES(OWNERID, _URL);

INSERT INTO PUBLIC.PREFIXES(_URL, _PREFIX, ID, OWNERID, _NAME) VALUES 
('http://purl.org/procurement/public-contracts#', 'pc', '3fa1846b-124c-4a21-b43f-708c88fd9d18', NULL, 'pc'), 
('http://www.w3.org/2006/http#', 'http', '529876ed-3cd2-4116-b781-3e90d4ac8613', NULL, 'http'), 
('http://opencoinage.org/rdf/', 'oc', 'aca0d5b8-40d3-4a03-8a78-79404daae27c', NULL, 'oc'), 
('http://rdf.data-vocabulary.org/#', 'dv', '49cc1909-9c1c-4439-9670-d183d1315023', NULL, 'dv'), 
('http://www.w3.org/2001/sw/DataAccess/tests/test-dawg#', 'dawgt', 'df7540e0-9d9e-4e08-8ddb-fcc5b522cfaa', NULL, 'dawgt'), 
('http://dbpedia.org/property/', 'dbpprop', 'd08a8d2c-9229-4cbf-9dda-e647155b7252', NULL, 'dbpprop'), 
('http://dbpedia.org/resource/', 'dbpedia', 'a97321ff-e8d9-4690-9a68-333fa3cfc256', NULL, 'dbpedia'), 
('http://purl.org/dc/elements/1.1/', 'dc', '7b125d82-6321-4f7e-9bad-f052e85b937c', NULL, 'dc'), 
('http://www.w3.org/2005/xpath-functions/#', 'fn', '33fa501e-c274-4cd3-a10e-abec582374c5', NULL, 'fn'), 
('http://xmlns.com/foaf/0.1/', 'foaf', 'b3189cf7-14d8-4c56-a865-059ee2399c4c', NULL, 'foaf'), 
('http://www.w3.org/2003/01/geo/wgs84_pos#', 'geo', '39e234b1-e863-4d76-a7cf-5e0c44bc8e9a', NULL, 'geo'), 
('http://purl.org/obo/owl/GO#', 'go', 'd4f5b178-a88f-4f77-b359-5e83b60b3cb6', NULL, 'go'), 
('http://www.w3.org/2000/10/swap/math#', 'math', '365112b7-1e9f-46ee-97c5-edd15fe6e451', NULL, 'math'), 
('http://purl.org/commons/record/mesh/', 'mesh', 'a7c39ca4-7c18-44d2-b321-edd2ad95e024', NULL, 'mesh'), 
('http://www.w3.org/2001/sw/DataAccess/tests/test-manifest#', 'mf', 'f9479984-fbfe-4f47-9679-582aaeef13c0', NULL, 'mf'), 
('http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#', 'nci', '00e10384-0fc1-4efa-b601-71424c370a36', NULL, 'nci'), 
('http://www.geneontology.org/formats/oboInOwl#', 'obo', '7e14ab45-daba-4f0d-b320-fdea45ec02e9', NULL, 'obo'), 
('http://www.w3.org/2002/07/owl#', 'owl', '1c7e8cd2-e96e-4574-9349-68e918cc736e', NULL, 'owl'), 
('http://www.buy.com/rss/module/productV2/', 'product', '6d18af21-ced3-41d7-9ed6-01033d299b3e', NULL, 'product'), 
('http://purl.org/science/protein/bysequence/', 'protseq', '6c9f3919-55cf-4212-a7a5-35b392f39452', NULL, 'protseq'), 
('http://www.w3.org/1999/02/22-rdf-syntax-ns#', 'rdf', '88627018-8e08-46d1-8bc0-703b21dbb9ca', NULL, 'rdf'), 
('http://www.w3.org/ns/rdfa#', 'rdfa', 'aaff9581-ce50-497d-b616-61450e281ad2', NULL, 'rdfa'), 
('http://www.openlinksw.com/virtrdf-data-formats#', 'rdfdf', '52782f2b-1906-43f9-b8d4-4784b9cc2ec8', NULL, 'rdfdf');