package slick.test.codegen

import scala.concurrent.ExecutionContext.Implicits.global
import slick.ast.{FieldSymbol, Node, Select}
import slick.jdbc.JdbcProfile
import slick.jdbc.meta.MTable
import slick.test.codegen.generated._
import com.typesafe.slick.testkit.util.{TestCodeRunner, JdbcTestDB}
import org.junit.Assert._
import slick.relational.RelationalProfile

/** Test files generated by CodeGeneratorTest */
class GeneratedCodeTest extends TestCodeRunner(AllTests)

object GeneratedCodeTest {
  def testCG1 = {
    import CG1._
    import profile.api._
    DBIO.seq(
      schema.create,
      Suppliers += Supplier(1, "1", "2", "3", "4", "5"),
      Suppliers.length.result.map(assertEquals("Size of Suppliers after change", 1, _)),
      Coffees.length.result.map(assertEquals("Size of Coffees", 0, _)),
      MTable.getTables(Some(""), Some(""), None, None).map { tables =>
        val a = tables.find(_.name.name equals "a").get
        val b = tables.find(_.name.name equals "b").get
        assertEquals("# of FKs of 'a' should be 1",
          1, A.baseTableRow.foreignKeys.size)
        assertEquals("# of FKs of 'b' should be 0",
          0, B.baseTableRow.foreignKeys.size)
        val aFk = A.baseTableRow.foreignKeys.head
        val srcColumns = convertColumnsToString(aFk.linearizedSourceColumns.toList)
        val trgColumns = convertColumnsToString(aFk.linearizedTargetColumns.toList)
        assertEquals("FKs should have the same source column", List("k1"), srcColumns)
        assertEquals("FKs should have the same target column", List("f1"), trgColumns)
        assertTrue("FKs should be from 'a' to 'b'", tableName(aFk.sourceTable) == A.baseTableRow.tableName && tableName(aFk.targetTable) == B.baseTableRow.tableName)

        assertEquals("# of FKs of 'c' should be 1", 1, C.baseTableRow.foreignKeys.size)
        assertEquals("# of FKs of 'd' should be 0", 0, D.baseTableRow.foreignKeys.size)
        val cFk = C.baseTableRow.foreignKeys.head
        val cSrcColumns = convertColumnsToString(cFk.linearizedSourceColumns.toList)
        val cTrgColumns = convertColumnsToString(cFk.linearizedTargetColumns.toList)
        assertEquals("FKs should have the same source column", List("k1", "k2"), cSrcColumns)
        assertEquals("FKs should have the same target column", List("f1", "f2"), cTrgColumns)
        assertTrue("FKs should be from 'c' to 'd'", tableName(cFk.sourceTable) == C.baseTableRow.tableName && tableName(cFk.targetTable) == D.baseTableRow.tableName)

        assertEquals("# of unique indices of 'c' should be 0", 0, C.baseTableRow.indexes.size)
        assertEquals("# of unique indices of 'd' should be 1", 1, D.baseTableRow.indexes.size)
        val dIdx = D.baseTableRow.indexes.head
        val dIdxFieldsName = convertColumnsToString(dIdx.on)
        assertTrue("Indices should refer to correct field", dIdxFieldsName sameElements List("f1", "f2"))

        def optionsOfColumn(c: slick.lifted.Rep[_]) =
          c.toNode.asInstanceOf[Select].field.asInstanceOf[FieldSymbol].options.toList
        val k1Options = optionsOfColumn(E.baseTableRow.k1)
        val k2Options = optionsOfColumn(E.baseTableRow.k2)
        val sOptions = optionsOfColumn(E.baseTableRow.s)
        assertTrue("k1 should be AutoInc", k1Options.exists(option => (option equals E.baseTableRow.O.AutoInc)))
        assertTrue("k2 should not be AutoInc", k2Options.forall(option => !(option equals E.baseTableRow.O.AutoInc)))
        assertTrue("s should not be AutoInc", sOptions.forall(option => !(option equals E.baseTableRow.O.AutoInc)))
        // test default values
        assertEquals(None, ERow(1,2).n)
        assertEquals("test", ERow(1,2).s)
        assertEquals("asdf", ERow(1,2,"asdf").s)
      }
    )
  }

  def testCG2 = {
    class Db1 extends CG2 {
      val profile = slick.jdbc.HsqldbProfile
    }
    val Db1 = new Db1
    import Db1._
    import profile.api._
    val s = Supplier(49, "Superior Coffee", "1 Party Place", "Mendocino", "CA", "95460")
    DBIO.seq(
      schema.create,
      Suppliers.length.result.map(assertEquals(0, _)),
      Suppliers += s,
      Suppliers.result.map(assertEquals(List(s), _))
    )
  }

  def testCG3 = {
    import CG3._
    import profile.api._
    val s = Supplier(49, "Superior Coffee", "1 Party Place", "Mendocino", "CA", "95460")
    DBIO.seq(
      schema.create,
      Suppliers += s,
      Suppliers.result.map(assertEquals(List(s), _))
    )
  }

  def testCG7 = {
    import CG7._
    import profile.api._
    DBIO.seq(
      schema.create,
      Supps.length.result.map(assertEquals(0, _)),
      Supps += Supplier(1, "1", "2", "3", "4", "5"),
      Supps.length.result.map(assertEquals(1, _)),
      Coffs.length.result.map(assertEquals(0, _))
    )
  }

  def testCG8 = {
    import CG8._
    import profile.api._
    DBIO.seq(
      schema.create,
      SimpleAs.length.result.map(assertEquals(0, _)),
      SimpleAs += SimpleA(CustomTyping.True, "1"),
      SimpleAs.length.result.map(assertEquals(1, _)),
      SimpleAs.result.map(assertEquals(List(SimpleA(CustomTyping.True, "1")), _))
    )
  }

  def testCG9 = {
    import CG9._
    import profile.api._
    def assertAll(all: Seq[ERow]) = {
      assertEquals( 3, all.size )
      assertEquals( Set(1,2,3), all.map(_.k1.get).toSet )
      assertEquals( all.map(_.k2), all.map(_.k1.get) ) // assert auto inc order, should be tested somewhere else as well
    }
    DBIO.seq(
      schema.create,
      E += ERow(1,"foo",Some("bar"),Some(2)),
      E += ERow(2,"foo",Some("bar")),
      E += ERow(3,"foo",Some("bar"),None),
      E.result.map(assertAll),
      sql"select k1, k2, s, n from e".as[ERow].map(assertAll)
    )
  }

  def testPostgres3 = {
    import Postgres3._
    import profile.api._
    DBIO.seq(
      schema.create,
      MTable.getTables(Some(""), Some("public"), None, None).map { tables =>
        def optionsOfColumn(c: slick.lifted.Rep[_]) =
          c.toNode.asInstanceOf[Select].field.asInstanceOf[FieldSymbol].options.toList
        val smallserialOptions = optionsOfColumn(Test.baseTableRow.smallintAutoInc)
        val serialOptions = optionsOfColumn(Test.baseTableRow.intAutoInc)
        val bigserialOptions = optionsOfColumn(Test.baseTableRow.bigintAutoInc)
        val charEmptyOptions = optionsOfColumn(Test.baseTableRow.charDefaultEmpty)
        val charValidOptions = optionsOfColumn(Test.baseTableRow.charDefaultValid)
        val charInvalidOptions = optionsOfColumn(Test.baseTableRow.charDefaultInvalid)
        assertTrue("smallint_auto_inc should be AutoInc", smallserialOptions.exists(option => (option equals Test.baseTableRow.O.AutoInc)))
        assertTrue("int_auto_inc should be AutoInc", serialOptions.exists(option => (option equals Test.baseTableRow.O.AutoInc)))
        assertTrue("bigint_auto_inc should be AutoInc", bigserialOptions.exists(option => (option equals Test.baseTableRow.O.AutoInc)))
        assertTrue("default value of char_default_empty should be ' '", charEmptyOptions.exists(option => (option equals Test.baseTableRow.O.Default(' '))))
        assertTrue("default value of char_default_valid should be 'a'", charValidOptions.exists(option => (option equals Test.baseTableRow.O.Default('a'))))
        assertTrue("default value of char_default_invalid should not exist", charInvalidOptions.forall(option => (option.isInstanceOf[RelationalProfile.ColumnOption.Default[_]])))
      }
    )
  }

  def testEmptyDB = slick.dbio.DBIO.successful(())

  def tableName( node:Node ) : String = {
    import slick.ast._
    node match {
      case TableExpansion(_, tableNode, _) => tableName(tableNode)
      case t: TableNode => t.tableName
    }
  }

  def convertColumnsToString(columns: Seq[Node]) = columns.map(_.asInstanceOf[Select].field.name)
}
