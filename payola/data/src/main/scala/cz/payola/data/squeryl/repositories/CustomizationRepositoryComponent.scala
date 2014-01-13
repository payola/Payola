package cz.payola.data.squeryl.repositories

import cz.payola.data.squeryl._
import cz.payola.data.squeryl.entities.settings._
import cz.payola.data.squeryl.entities.User
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.dsl.ast.LogicalBoolean

/**
 * Provides repository to access persisted customizations
 */
trait CustomizationRepositoryComponent extends TableRepositoryComponent
{
    self: SquerylDataContextComponent =>
    private type QueryType = (Customization, Option[User], Option[ClassCustomization],
        Option[PropertyCustomization])

    /**
     * A repository to access persisted customizations
     */
    lazy val customizationRepository =
        new TableRepository[Customization, QueryType](schema.customizations, Customization)
            with CustomizationRepository
            with NamedEntityTableRepository[Customization]
            with OptionallyOwnedEntityTableRepository[Customization, QueryType]
            with ShareableEntityTableRepository[Customization, QueryType]
        {
            override def persist(entity: AnyRef) = wrapInTransaction {

                val convertedEntity = Customization.convert(entity).get

                val inDbCustOpt = convertedEntity match {
                    case c: cz.payola.common.entities.settings.Customization => {
                        getById(c.id)
                    }
                    case _ => None
                }

                val persistedCustomization = inDbCustOpt.getOrElse(super.persist(convertedEntity) )

                convertedEntity match {
                    //case o: Customization =>
                    // The entity is already in the database, but classes/properties may need an update
                    case o: cz.payola.common.entities.settings.Customization => {
                        // Associate and persist the classes.
                        o.classCustomizations.foreach { classCustomization =>

                            val inDbClassCustomization =
                                inDbCustOpt.map(_.classCustomizations.find{ inDbClassCust =>
                                    classCustomization.id == inDbClassCust.id
                                }).getOrElse(None)
                            val persistedClassCustomization = if(inDbClassCustomization.isDefined) {
                                ClassCustomization.convert(inDbClassCustomization).get
                            } else {
                                schema.associate(ClassCustomization(classCustomization),
                                    schema.classCustomizationsOfCustomizations.left(persistedCustomization))
                            }

                            // Associate and persist the properties
                            classCustomization.propertyCustomizations.foreach { propertyCustomization =>
                                if(inDbClassCustomization.isEmpty
                                    || !inDbClassCustomization.get.propertyCustomizations.exists{inDbProp => inDbProp.id == propertyCustomization.id}) {

                                    schema.associate(PropertyCustomization(propertyCustomization),
                                        schema.propertyCustomizationsOfClasses.left(persistedClassCustomization))
                                }
                            }
                        }
                    }
                }

                persistedCustomization
            }

            override def removeById(id: String) = {
                // Unset from DefaultCustomizations of Analyses
                analysisRepository.ontologyCustomizationIsRemoved(id)

                super.removeById(id)
            }

            def persistClassCustomization(classCustomization: AnyRef) {
                persist(ClassCustomization(classCustomization), schema.classCustomizations)
            }

            def persistPropertyCustomization(propertyCustomization: AnyRef) {
                persist(PropertyCustomization(propertyCustomization), schema.propertyCustomizations)
            }

            def removeClassCustomizationById(id: String) = {
                wrapInTransaction {
                    schema.classCustomizations.deleteWhere(e => id === e.id) == 1
                }
            }

            def removePropertyCustomizationById(id: String) = {
                wrapInTransaction {
                    schema.propertyCustomizations.deleteWhere(e => id === e.id) == 1
                }
            }

            protected def getSelectQuery(entityFilter: (Customization) => LogicalBoolean) = {

                join(table, schema.users.leftOuter, schema.classCustomizations.leftOuter,
                    schema.propertyCustomizations.leftOuter)((o, u, c, p) =>
                    where(entityFilter(o))
                        select(o, u, c, p)
                        on(o.ownerId === u.map(_.id),
                        c.map(_.customizationId) === Some(o.id),
                        p.map(_.classCustomizationId) === c.map(_.id))
                )
            }

            protected def processSelectResults(results: Seq[QueryType]) = wrapInTransaction {
                results.groupBy(_._1.id).map {r =>
                    val customization = r._2.head._1
                    customization.owner = r._2.head._2
                    customization.classCustomizations = r._2.groupBy(_._3).flatMap {c =>
                        val classCustomization = c._1
                        if (classCustomization.isDefined) {
                            classCustomization.get.propertyCustomizations = c._2.flatMap(_._4).sortBy(_.uri)
                        }

                        classCustomization
                    }(collection.breakOut).sortBy(_.uri)
                    customization
                }(collection.breakOut)
            }
        }
}