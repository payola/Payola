UPDATE STRINGPARAMETERS 
SET _CANCONTAINURL = TRUE
WHERE _NAME IN (
	'Graph URIs',
	'Output Webservice URL',
	'Sparql Endpoint URL',
	'Property URIs',
	'Property URI',
	'RDF Type URI',
	'Join Property URI',
	'Ontology URLs',
	'Origin URI',
	'Destination URI',
	'Endpoint URL',
	'Graph URIs',
	'Group URI'
) 