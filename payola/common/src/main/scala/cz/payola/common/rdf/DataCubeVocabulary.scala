package cz.payola.common.rdf

/**
 * A set of classes designed to be able to hold information about a Data Cube vocabulary.
 *
 * It can have multiple data structures with multiple dimensions, measures and attributes. Ordering supported.
 * @author Jiri Helmich
 */

/**
 * Vocabulary definition
 * @param dataStructureDefinitions Data Structure object representation
 * @param uri URI
 */
case class DataCubeVocabulary(dataStructureDefinitions: Seq[DataCubeDataStructureDefinition], uri: String)

/**
 * DataStructure definition
 *
 * @param uri URI
 * @param label Description
 * @param dimensions List of inner dimensions
 * @param measures List of inner measures
 * @param attributes List of inner attributes
 */
case class DataCubeDataStructureDefinition(uri: String, label: String, dimensions: Seq[DataCubeDimension],
    measures: Seq[DataCubeMeasure], attributes: Seq[DataCubeAttribute])

/**
 * Dimension representation
 * @param uri URI
 * @param label Description
 * @param order This parameter needs to be var in order to support failover ordering
 */
case class DataCubeDimension(uri: String, label: Option[String], var order: Option[Int])

/**
 * Measure representation
 * @param uri URI
 * @param label Description
 * @param order Order
 */
case class DataCubeMeasure(uri: String, label: Option[String], order: Option[Int])

/**
 * Attribute definition
 * @param uri URI
 * @param label Description
 * @param order Order
 */
case class DataCubeAttribute(uri: String, label: Option[String], order: Option[Int])