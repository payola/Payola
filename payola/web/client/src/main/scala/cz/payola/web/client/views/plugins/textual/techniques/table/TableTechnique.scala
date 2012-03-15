package cz.payola.web.client.views.plugins.textual.techniques.table

import cz.payola.web.client.views.plugins.textual.techniques.BaseTechnique
import s2js.adapters.js.dom.Element
import collection.mutable.ListBuffer
import s2js.adapters.js.browser._
import cz.payola.common.rdf.{LiteralVertex, IdentifiedVertex, Edge}


class TableTechnique extends BaseTechnique
{
    
    private val class_table = "tableTechnique table"
    private val class_table_header = "tableTechnique header element"
    
    private val class_table_line_odd = "tableTechnique line odd"
    private val class_table_line_even = "tableTechnique line even"

    private val class_table_element = "tableTechnique element"
    private val class_list_element = "tableTechnique list element"

    def performTechnique() {
        val table = document.createElement[Element]("table")
        parentElement.get.appendChild(table)
        table.className = class_table
        
        addHeader(table)

        var needToFillFirstRow = false

        var mainRowOddEvent = class_table_line_odd
        
        val sortedByOrigin = sortByOrigin(graphModel.get.edges)
        sortedByOrigin.foreach{ edgeOriginList => //first column of the table contains origins of edges
            var currentRow = addNewRow(table)
            currentRow.className = mainRowOddEvent
            setCell(addCells(currentRow, 1), edgeOriginList.head.origin)

            
            val sortedByType = sortByType(edgeOriginList)
            sortedByType.foreach{ edgeTypeList => //second column of the table contains types of edges
                if(needToFillFirstRow) {
                    currentRow = addNewRow(table)
                    currentRow.className = mainRowOddEvent
                    addCells(currentRow, 1)
                }
                setCell(addCells(currentRow, 1), edgeTypeList.head)
                val list = document.createElement[Element]("ul")
                addCells(currentRow, 1).appendChild(list)
                
                edgeTypeList.foreach{ edge => //third column of the table contains destinations of edges

                    val listRec = document.createElement[Element]("li")
                    listRec.className = class_list_element
                    list.appendChild(listRec)
                    setCell(listRec, edge.destination)
                }

                needToFillFirstRow = true
            }
            needToFillFirstRow = false

            mainRowOddEvent = if(mainRowOddEvent == class_table_line_odd) {
                class_table_line_even
            } else {
                class_table_line_odd
            }
        }
    }
    
    private def addHeader(table: Element) {
        val headerLine = document.createElement[Element]("tr")
        table.appendChild(headerLine)

        val edgeOrigin = document.createElement[Element]("th")
        headerLine.appendChild(edgeOrigin)
        edgeOrigin.className = class_table_header
        edgeOrigin.innerHTML = "Relation origin"

        val edgeType = document.createElement[Element]("th")
        headerLine.appendChild(edgeType)
        edgeType.className = class_table_header
        edgeType.innerHTML = "Relation type"

        val edgeDestination = document.createElement[Element]("th")
        headerLine.appendChild(edgeDestination)
        edgeDestination.className = class_table_header
        edgeDestination.innerHTML = "Relation origin"
    }
    
    private def sortByOrigin(edges: Seq[Edge]): ListBuffer[Seq[Edge]] = {
        
        var result = ListBuffer[Seq[Edge]]()

        edges.foreach{ edge =>
            if(result.find(list => list.head.origin == edge.origin).isEmpty){
                result += edges.filter(filteredEdge => filteredEdge.origin == edge.origin)
            }
        }
        
        result
    }

    private def sortByType(edges: Seq[Edge]): ListBuffer[Seq[Edge]] = {
        var result = ListBuffer[Seq[Edge]]()

        edges.foreach{ edge =>
            if(result.find(list => list.head.uri == edge.uri).isEmpty){
                result += edges.filter(filteredEdge => filteredEdge.uri == edge.uri)
            }
        }

        result
    }

    private def addCells(tableRow: Element, count: Int): Element = {
        var counter = 0
        var lastAdded = tableRow
        while(counter < count) {
            val newCell = document.createElement[Element]("td")
            newCell.className = class_table_element
            lastAdded = newCell
            tableRow.appendChild(newCell)
            counter += 1
        }

        lastAdded
    }

    private def addNewRow(table: Element): Element = {
        val newLine = document.createElement[Element]("tr")
        table.appendChild(newLine)

        newLine
    }

    private def setCell(cell: Element, value: Any){

        value match {
            case i: IdentifiedVertex =>
                cell.innerHTML = "<a href=\"" + value.toString() + "\">" + value.toString() + "</a>"
            case i: LiteralVertex =>
                cell.innerHTML = "<p>" + value.toString() + "</p>"
            case _ => cell.innerHTML = value.toString()
        }
    }

    override def clean() {
        if(parentElement.isDefined) {
            val element = parentElement.get
            window.alert("pocet potomku tabulky: "+element.childNodes.length)
            while(element.childNodes.length > 0) {
                element.removeChild(element.firstChild)
            }
        }
    }

    def getName:String = {
        "textual table"
    }
}
