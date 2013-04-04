package cz.payola.common.rdf

case class DataCubeVocabulary(dataStructureDefinitions: Seq[DataCubeDataStructureDefinition], uri: String)
case class DataCubeDataStructureDefinition(uri: String, label: String, dimensions: Seq[DataCubeDimension], measures: Seq[DataCubeMeasure])
case class DataCubeDimension(label: String)
case class DataCubeMeasure(label: String)