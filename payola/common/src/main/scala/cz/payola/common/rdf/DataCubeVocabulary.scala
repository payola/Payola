package cz.payola.common.rdf

case class DataCubeVocabulary(dataStructureDefinitions: Seq[DataCubeDataStructureDefinition], uri: String)

case class DataCubeDataStructureDefinition(uri: String, label: String, dimensions: Seq[DataCubeDimension],
    measures: Seq[DataCubeMeasure], attributes: Seq[DataCubeAttribute])

case class DataCubeDimension(uri: String, label: Option[String], var order: Option[Int])
case class DataCubeMeasure(uri: String, label: Option[String], order: Option[Int])
case class DataCubeAttribute(uri: String, label: Option[String], order: Option[Int])