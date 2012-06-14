package cz.payola.domain.entities.permissions.privilege

import cz.payola.common
import cz.payola.domain.entities.settings.ontology.Customization
import cz.payola.domain.entities._

/** An abstract class that grants the user some privilege over @obj.
  *
  * The particular privilege is defined in within the subclasses.
  *
  * @param obj Object, over which is the user granted a privilege.
  * @tparam A A subclass of the ConcreteEntity trait.
  */
abstract class Privilege[A <: Entity](val obj: A) extends Entity with common.entities.permissions.privilege.Privilege[A]


/**
  * Any privilege that extends this trait is considered public and hence safe to be transferred to the
  * client side - i.e. serialized. The User class will filter the privileges and return only those that
  * are public.
  */
trait PublicPrivilege


/**This class narrows down the privilege subject to some group.
  *
  * @param g A group which is the subject of this privilege.
  */
class GroupPrivilege(g: Group) extends Privilege[Group](g) with cz.payola.common.entities.permissions.privilege.GroupPrivilege


/** This class narrows down the privilege subject to some analysis.
  *
  * @param a An analysis which is the subject of this privilege.
  */
abstract class AnalysisPrivilege(a: Analysis) extends Privilege[Analysis](a)
    with cz.payola.common.entities.permissions.privilege.AnalysisPrivilege with PublicPrivilege


/** Grants the right to access the data of analysis @a.
  *
  * @param a An analysis which is the subject of this privilege.
  */
class AccessAnalysisDataPrivilege(a: Analysis) extends AnalysisPrivilege(a)


/** Allows the user to access result of the analysis @a.
  *
  * @param a An analysis which is the subject of this privilege.
  */
class AccessAnalysisResultOnlyPrivilege(a: Analysis) extends AnalysisPrivilege(a)


/**This class narrows down the privilege subject to some ontology customization.
  *
  * @param c An ontology customization which is the subject of this privilege.
  */
class OntologyCustomizationPrivilege(c: Customization) extends Privilege[Customization](c)
        with cz.payola.common.entities.permissions.privilege.OntologyCustomizationPrivilege with PublicPrivilege


