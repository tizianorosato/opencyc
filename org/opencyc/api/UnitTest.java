package org.opencyc.api;

import junit.framework.*;
import java.util.*;
import java.net.*;
import java.io.*;
import java.text.*;
import org.opencyc.cycobject.*;
import org.opencyc.util.*;

/**
 * Provides a unit test suite for the <tt>org.opencyc.api</tt> package<p>
 *
 * @version $Id$
 * @author Stephen L. Reed
 *
 * <p>Copyright 2001 Cycorp, Inc., license is open source GNU LGPL.
 * <p><a href="http://www.opencyc.org/license.txt">the license</a>
 * <p><a href="http://www.opencyc.org">www.opencyc.org</a>
 * <p><a href="http://www.sourceforge.net/projects/opencyc">OpenCyc at SourceForge</a>
 * <p>
 * THIS SOFTWARE AND KNOWLEDGE BASE CONTENT ARE PROVIDED ``AS IS'' AND
 * ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE OPENCYC
 * ORGANIZATION OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE AND KNOWLEDGE
 * BASE CONTENT, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
public class UnitTest extends TestCase {

    /**
     * Creates a <tt>UnitTest</tt> object with the given name.
     */
    public UnitTest(String name) {
        super(name);
    }

    /**
     * Runs the unit tests.
     */
    public static void runTests() {
        TestSuite testSuite = new TestSuite();
        //testSuite.addTest(new UnitTest("testAsciiCycConnection"));
        //testSuite.addTest(new UnitTest("testBinaryCycConnection"));
        //testSuite.addTest(new UnitTest("testAsciiCycAccess1"));
        //testSuite.addTest(new UnitTest("testBinaryCycAccess1"));
        //testSuite.addTest(new UnitTest("testAsciiCycAccess2"));
        //testSuite.addTest(new UnitTest("testBinaryCycAccess2"));
        //testSuite.addTest(new UnitTest("testAsciiCycAccess3"));
        //testSuite.addTest(new UnitTest("testBinaryCycAccess3"));
        //testSuite.addTest(new UnitTest("testAsciiCycAccess4"));
        //testSuite.addTest(new UnitTest("testBinaryCycAccess4"));
        //testSuite.addTest(new UnitTest("testAsciiCycAccess5"));
        //testSuite.addTest(new UnitTest("testBinaryCycAccess5"));
        testSuite.addTest(new UnitTest("testAsciiCycAccess6"));
        testSuite.addTest(new UnitTest("testBinaryCycAccess6"));
        //testSuite.addTest(new UnitTest("testMakeValidConstantName"));
        TestResult testResult = new TestResult();
        testSuite.run(testResult);
    }

    /**
     * Main method in case tracing is prefered over running JUnit.
     */
    public static void main(String[] args) {
        runTests();
    }

    /**
     * Tests the makeValidConstantName method.
     */
    public void testMakeValidConstantName () {
        System.out.println("**** testMakeValidConstantName ****");
        String candidateName = "abc";
        Assert.assertEquals(candidateName, CycConstant.makeValidConstantName(candidateName));
        candidateName = "()[]//abc";
        String expectedValidName = "______abc";
        Assert.assertEquals(expectedValidName, CycConstant.makeValidConstantName(candidateName));
        System.out.println("**** testMakeValidConstantName OK ****");
    }

    /**
     * Tests the fundamental aspects of the ascii api connection to the OpenCyc server.
     */
    public void testAsciiCycConnection () {
        System.out.println("**** testAsciiCycConnection ****");

        CycConnection cycConnection = null;
        try {
            CycAccess cycAccess = new CycAccess(CycConnection.DEFAULT_HOSTNAME,
                                                CycConnection.DEFAULT_BASE_PORT,
                                                CycConnection.ASCII_MODE,
                                                CycAccess.PERSISTENT_CONNECTION);
            cycConnection = cycAccess.cycConnection;
            //cycConnection.trace = true;
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            e.printStackTrace();
            Assert.fail(e.toString());
        }

        // Test return of atom.
        String command = "(+ 2 3)";
        Object [] response = {new Integer(0), ""};
        try {
            response = cycConnection.converse(command);
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertEquals(Boolean.TRUE, response[0]);
        Assert.assertEquals(new Integer(5), response[1]);

        // Test return of string.
        command = "(quote " + '\"' + "abc" + '\"' + ")";
        try {
            response = cycConnection.converse(command);
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertEquals(Boolean.TRUE, response[0]);
        Assert.assertEquals("abc", response[1]);

        // Test return of symbolic expression.
        command = "(quote (a b (c d (e) f)))";
        try {
            response = cycConnection.converse(command);
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertEquals(Boolean.TRUE, response[0]);
        Assert.assertEquals("(A B (C D (E) F))", response[1].toString());

        // Test return of improper list.
        command = "(quote (a . b))";
        try {
            response = cycConnection.converse(command);
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertEquals(Boolean.TRUE, response[0]);
        Assert.assertEquals("(A . B)", response[1].toString());

        // Test function evaluation.
        command = "(member? #$Dog '(#$DomesticPet #$Dog))";
        try {
            response = cycConnection.converse(command);
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertEquals(Boolean.TRUE, response[0]);
        Assert.assertEquals(CycSymbol.t, response[1]);

        // Test KB Ask.
        command = "(removal-ask '(#$genls #$DomesticPet #$DomesticatedAnimal) #$HumanActivitiesMt)";
        try {
            response = cycConnection.converse(command);
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertEquals(Boolean.TRUE, response[0]);
        Assert.assertTrue(response[1] instanceof CycList);
        Assert.assertEquals("((((T . T)) ((:GENLS (#$genls #$DomesticPet #$DomesticatedAnimal) #$HumanActivitiesMt :TRUE-DEF))))",
                            ((CycList) response[1]).cyclify());

        cycConnection.close();
        System.out.println("**** testAsciiCycConnection OK ****");
    }

    /**
     * Tests the fundamental aspects of the binary (cfasl) api connection to the OpenCyc server.
     */
    public void testBinaryCycConnection () {
        System.out.println("**** testBinaryCycConnection ****");
        CycAccess cycAccess = null;
        CycConnection cycConnection = null;
        try {
            cycAccess = new CycAccess(CycConnection.DEFAULT_HOSTNAME,
                                      CycConnection.DEFAULT_BASE_PORT,
                                      //3654,
                                      CycConnection.BINARY_MODE,
                                      CycAccess.PERSISTENT_CONNECTION);
            cycConnection = cycAccess.cycConnection;
            //cycConnection.trace = true;
        }
        catch (Exception e) {
            Assert.fail(e.toString());
        }

        // Test return of atom.
        CycList command = new CycList();
        command.add(CycSymbol.makeCycSymbol("+"));
        command.add(new Integer(2));
        command.add(new Integer(3));
        Object [] response = {new Integer(0), ""};
        try {
            response = cycConnection.converse(command);
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertEquals(Boolean.TRUE, response[0]);
        Assert.assertEquals(new Integer(5), response[1]);

        // Test return of string.
        command = new CycList();
        command.add(CycSymbol.quote);
        command.add("abc");
        try {
            response = cycConnection.converse(command);
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertEquals(Boolean.TRUE, response[0]);
        Assert.assertEquals("abc", response[1]);

        // Test return of symbolic expression.
        command = new CycList();
        command.add(CycSymbol.quote);
        CycList cycList2 = new CycList();
        command.add(cycList2);
        cycList2.add(CycSymbol.makeCycSymbol("a"));
        cycList2.add(CycSymbol.makeCycSymbol("b"));
        CycList cycList3 = new CycList();
        cycList2.add(cycList3);
        cycList3.add(CycSymbol.makeCycSymbol("c"));
        cycList3.add(CycSymbol.makeCycSymbol("d"));
        CycList cycList4 = new CycList();
        cycList3.add(cycList4);
        cycList4.add(CycSymbol.makeCycSymbol("e"));
        cycList3.add(CycSymbol.makeCycSymbol("f"));
        try {
            response = cycConnection.converse(command);
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertEquals(Boolean.TRUE, response[0]);
        Assert.assertEquals("(A B (C D (E) F))", response[1].toString());

        // Test return of improper list.
        command = new CycList();
        command.add(CycSymbol.quote);
        cycList2 = new CycList();
        command.add(cycList2);
        cycList2.add(CycSymbol.makeCycSymbol("A"));
        cycList2.setDottedElement(CycSymbol.makeCycSymbol("B"));
        try {
            //cycConnection.trace = true;
            response = cycConnection.converse(command);
            //cycConnection.trace = false;
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertEquals(Boolean.TRUE, response[0]);
        Assert.assertEquals("(A . B)", response[1].toString());

        // Test error return
        command = new CycList();
        command.add(CycSymbol.nil);
        try {
            response = cycConnection.converse(command);
        }
        catch (Exception e) {
            Assert.fail(e.toString());
        }
        Assert.assertEquals("(CYC-EXCEPTION :MESSAGE \"Invalid API Request: NIL is not a valid API function symbol\")",
                            response[1].toString());

        cycConnection.close();
        System.out.println("**** testBinaryCycConnection OK ****");
    }

    /**
     * Tests a portion of the CycAccess methods using the ascii api connection.
     */
    public void testAsciiCycAccess1 () {
        System.out.println("**** testAsciiCycAccess 1 ****");
        CycAccess cycAccess = null;
        try {
            cycAccess = new CycAccess(CycConnection.DEFAULT_HOSTNAME,
                                      CycConnection.DEFAULT_BASE_PORT,
                                      CycConnection.ASCII_MODE,
                                      CycAccess.TRANSIENT_CONNECTION);
        }
        catch (Exception e) {
            Assert.fail(e.toString());
        }

        doTestCycAccess1(cycAccess);

        try {
            cycAccess.close();
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        System.out.println("**** testAsciiCycAccess 1 OK ****");
    }

    /**
     * Tests a portion of the CycAccess methods using the binary api connection.
     */
    public void testBinaryCycAccess1 () {
        System.out.println("**** testBinaryCycAccess 1 ****");
        CycAccess cycAccess = null;
        try {
            cycAccess = new CycAccess(CycConnection.DEFAULT_HOSTNAME,
                                      CycConnection.DEFAULT_BASE_PORT,
                                      CycConnection.BINARY_MODE,
                                      CycAccess.TRANSIENT_CONNECTION);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("\nException: " + e.getMessage());
            Assert.fail(e.toString());
        }

        doTestCycAccess1(cycAccess);

        try {
            cycAccess.close();
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        System.out.println("**** testBinaryCycAccess 1 OK ****");
    }

    /**
     * Tests a portion of the CycAccess methods using the given api connection.
     */
    protected void doTestCycAccess1(CycAccess cycAccess) {
        // getConstantByName.
        CycConstant cycConstant = null;
        try {
            cycConstant = cycAccess.getConstantByName("#$Dog");
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(cycConstant);
        Assert.assertEquals("bd58daa0-9c29-11b1-9dad-c379636f7270", cycConstant.guid.toString());

        // getConstantByGuid.
        try {
            cycConstant = cycAccess.getConstantByGuid(Guid.makeGuid("bd58daa0-9c29-11b1-9dad-c379636f7270"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(cycConstant);
        Assert.assertEquals("#$Dog", cycConstant.cyclify());
        Assert.assertEquals("Dog", cycConstant.name);

        // getConstantById
        cycConstant = null;
        try {
            cycConstant = cycAccess.getConstantById(new Integer(23200));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(cycConstant);
        Assert.assertEquals("#$Dog", cycConstant.cyclify());
        Assert.assertEquals("Dog", cycConstant.name);
        Assert.assertEquals(Guid.makeGuid("bd58daa0-9c29-11b1-9dad-c379636f7270"),
                            cycConstant.guid);

        // getComment.
        String comment = null;
        try {
            comment = cycAccess.getComment(cycAccess.getConstantByName("#$Raindrop"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(comment);
        Assert.assertEquals("The collection of drops of liquid water emitted by clouds in instances of #$RainProcess.",
                            comment);

        // getIsas.
        List isas = null;
        try {
            isas = cycAccess.getIsas(cycAccess.getConstantByName("#$Dog"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(isas);
        Assert.assertTrue(isas instanceof CycList);
        isas = ((CycList) isas).sort();
        try {
            Assert.assertTrue(isas.contains(cycAccess.getConstantByName("OrganismClassificationType")));
        }
        catch (Exception e) {
            Assert.fail(e.toString());
        }

    }

    /**
     * Tests a portion of the CycAccess methods using the ascii api connection.
     */
    public void testAsciiCycAccess2 () {
        System.out.println("**** testAsciiCycAccess 2 ****");
        CycAccess cycAccess = null;
        try {
            cycAccess = new CycAccess(CycConnection.DEFAULT_HOSTNAME,
                                      CycConnection.DEFAULT_BASE_PORT,
                                      CycConnection.ASCII_MODE,
                                      CycAccess.PERSISTENT_CONNECTION);
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }

        doTestCycAccess2 (cycAccess);

        try {
            cycAccess.close();
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        System.out.println("**** testAsciiCycAccess 2 OK ****");
    }


    /**
     * Tests a portion of the CycAccess methods using the binary api connection.
     */
    public void testBinaryCycAccess2 () {
        System.out.println("**** testBinaryCycAccess 2 ****");
        CycAccess cycAccess = null;
        try {
            cycAccess = new CycAccess(CycConnection.DEFAULT_HOSTNAME,
                                      CycConnection.DEFAULT_BASE_PORT,
                                      CycConnection.BINARY_MODE,
                                      CycAccess.PERSISTENT_CONNECTION);
        }
        catch (Exception e) {
            Assert.fail(e.toString());
        }

        doTestCycAccess2(cycAccess);

        try {
            cycAccess.close();
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        System.out.println("**** testBinaryCycAccess 2 OK ****");
    }

    /**
     * Tests a portion of the CycAccess methods using the given api connection.
     */
    protected void doTestCycAccess2 (CycAccess cycAccess) {
        // getGenls.
        List genls = null;
        try {
            genls = cycAccess.getGenls(cycAccess.getConstantByName("#$Dog"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(genls);
        Assert.assertTrue(genls instanceof CycList);
        genls = ((CycList) genls).sort();
        Assert.assertEquals("(CanineAnimal DomesticatedAnimal)", genls.toString());

        // getGenlPreds.
        List genlPreds = null;
        try {
            genlPreds = cycAccess.getGenlPreds(cycAccess.getConstantByName("#$target"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(genlPreds);
        Assert.assertTrue((genlPreds.toString().equals("(preActors)")) ||
                          (genlPreds.toString().equals("(actors)")));

        // getArg1Formats.
        List arg1Formats = null;
        try {
            arg1Formats = cycAccess.getArg1Formats(cycAccess.getConstantByName("#$target"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(arg1Formats);
        Assert.assertEquals("()", arg1Formats.toString());

        // getArg1Formats.
        arg1Formats = null;
        try {
            arg1Formats = cycAccess.getArg1Formats(cycAccess.getConstantByName("#$constantName"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(arg1Formats);
        Assert.assertEquals("(SingleEntry)", arg1Formats.toString());


        // getArg2Formats.
        List arg2Formats = null;
        try {
            arg2Formats = cycAccess.getArg2Formats(cycAccess.getConstantByName("#$internalParts"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(arg2Formats);
        Assert.assertEquals("(SetTheFormat)", arg2Formats.toString());

        // getDisjointWiths.
        List disjointWiths = null;
        try {
            disjointWiths = cycAccess.getDisjointWiths(cycAccess.getConstantByName("#$Plant"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(disjointWiths);
        Assert.assertEquals("(Animal)", disjointWiths.toString());

        // getCoExtensionals.
        List coExtensionals = null;
        try {
            //cycAccess.traceOn();
            coExtensionals = cycAccess.getCoExtensionals(cycAccess.getConstantByName("#$CycLTerm"));
            //cycAccess.traceOff();
        }
        catch (UnknownHostException e) {
            e.printStackTrace();
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(coExtensionals);
        Assert.assertEquals("(CycLExpression)", coExtensionals.toString());

        // getCoExtensionals.
        coExtensionals = null;
        try {
            coExtensionals = cycAccess.getCoExtensionals(cycAccess.getConstantByName("#$Dog"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(coExtensionals);
        Assert.assertEquals("()", coExtensionals.toString());

        // getArg1Isas.
        List arg1Isas = null;
        try {
            arg1Isas = cycAccess.getArg1Isas(cycAccess.getConstantByName("#$doneBy"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(arg1Isas);
        Assert.assertEquals("(Event)", arg1Isas.toString());

        // getArg2Isas.
        List arg2Isas = null;
        try {
            arg2Isas = cycAccess.getArg2Isas(cycAccess.getConstantByName("#$doneBy"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(arg2Isas);
        Assert.assertEquals("(SomethingExisting)", arg2Isas.toString());

        // getArgNIsas.
        List argNIsas = null;
        try {
            argNIsas = cycAccess.getArgNIsas(cycAccess.getConstantByName("#$doneBy"), 1);
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(argNIsas);
        Assert.assertEquals("(Event)", argNIsas.toString());

        // getArgNGenls.
        List argGenls = null;
        try {
            argGenls = cycAccess.getArgNGenls(cycAccess.getConstantByName("#$superTaxons"), 2);
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(argGenls);
        Assert.assertEquals("(Organism-Whole)", argGenls.toString());

        // isCollection.
        boolean answer = false;
        try {
            answer = cycAccess.isCollection(cycAccess.getConstantByName("#$Dog"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertTrue(answer);

        // isCollection.
        answer = true;
        try {
            answer = cycAccess.isCollection(cycAccess.getConstantByName("#$doneBy"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertTrue(! answer);

        // isBinaryPredicate.
        answer = false;
        try {
            answer = cycAccess.isBinaryPredicate(cycAccess.getConstantByName("#$doneBy"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertTrue(answer);

        // isBinaryPredicate.
        answer = true;
        try {
            answer = cycAccess.isBinaryPredicate(cycAccess.getConstantByName("#$Dog"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertTrue(! answer);

        // getPluralGeneratedPhrase.
        String phrase = null;
        try {
            phrase = cycAccess.getPluralGeneratedPhrase(cycAccess.getConstantByName("#$Dog"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(phrase);
        Assert.assertEquals("dogs (domesticated animals)", phrase);

        // getSingularGeneratedPhrase.
        phrase = null;
        try {
            phrase = cycAccess.getSingularGeneratedPhrase(cycAccess.getConstantByName("#$Brazil"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(phrase);
        Assert.assertEquals("Brazil (country)", phrase);

        // getGeneratedPhrase.
        phrase = null;
        try {
            phrase = cycAccess.getGeneratedPhrase(cycAccess.getConstantByName("#$doneBy"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(phrase);
        Assert.assertEquals("doer", phrase);
    }

    /**
     * Tests a portion of the CycAccess methods using the ascii api connection.
     */
    public void testAsciiCycAccess3 () {
        System.out.println("**** testAsciiCycAccess 3 ****");
        CycAccess cycAccess = null;
        try {
            cycAccess = new CycAccess(CycConnection.DEFAULT_HOSTNAME,
                                      CycConnection.DEFAULT_BASE_PORT,
                                      CycConnection.ASCII_MODE,
                                      CycAccess.PERSISTENT_CONNECTION);
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }

        doTestCycAccess3 (cycAccess);

        try {
            cycAccess.close();
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        System.out.println("**** testAsciiCycAccess 3 OK ****");
    }


    /**
     * Tests a portion of the CycAccess methods using the binary api connection.
     */
    public void testBinaryCycAccess3 () {
        System.out.println("**** testBinaryCycAccess 3 ****");
        CycAccess cycAccess = null;
        try {
            cycAccess = new CycAccess(CycConnection.DEFAULT_HOSTNAME,
                                      CycConnection.DEFAULT_BASE_PORT,
                                      CycConnection.BINARY_MODE,
                                      CycAccess.PERSISTENT_CONNECTION);
        }
        catch (Exception e) {
            Assert.fail(e.toString());
        }

        doTestCycAccess3(cycAccess);

        try {
            cycAccess.close();
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        System.out.println("**** testBinaryCycAccess 3 OK ****");
    }

    /**
     * Tests a portion of the CycAccess methods using the given api connection.
     */
    protected void doTestCycAccess3 (CycAccess cycAccess) {
        // getParaphrase.
        String phrase = null;
        try {
            //cycAccess.traceOn();
            phrase = cycAccess.getParaphrase(cycAccess.makeCycList("(#$isa #$Brazil #$Country)"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(phrase);
        Assert.assertEquals("Brazil (country) is a country (political entity)", phrase);

        // getComment.
        String comment = null;
        try {
            comment = cycAccess.getComment(cycAccess.getConstantByName("#$MonaLisa-Painting"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(comment);
        Assert.assertEquals("Mona Lisa, the #$OilPainting by #$LeonardoDaVinci-TheArtist", comment);

        // getIsas.
        List isas = null;
        try {
            isas = cycAccess.getIsas(cycAccess.getConstantByName("#$Brazil"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(isas);
        Assert.assertTrue(isas instanceof CycList);
        isas = ((CycList) isas).sort();
        Assert.assertEquals("(Entity IndependentCountry PublicConstant)", isas.toString());

        // getGenls.
        List genls = null;
        try {
            genls = cycAccess.getGenls(cycAccess.getConstantByName("#$Dog"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(genls);
        Assert.assertTrue(genls instanceof CycList);
        genls = ((CycList) genls).sort();
        Assert.assertEquals("(CanineAnimal DomesticatedAnimal)", genls.toString());

        // getMinGenls.
        List minGenls = null;
        try {
            minGenls = cycAccess.getMinGenls(cycAccess.getConstantByName("#$Lion"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(minGenls);
        Assert.assertTrue(minGenls instanceof CycList);
        minGenls = ((CycList) minGenls).sort();
        Assert.assertEquals("(FelidaeFamily)", minGenls.toString());

        // getSpecs.
        List specs = null;
        try {
            specs = cycAccess.getSpecs(cycAccess.getConstantByName("#$CanineAnimal"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(specs);
        Assert.assertTrue(specs instanceof CycList);
        specs = ((CycList) specs).sort();
        Assert.assertEquals("(Coyote-Animal Dog Fox Jackal Wolf)", specs.toString());

        // getMaxSpecs.
        List maxSpecs = null;
        try {
            maxSpecs = cycAccess.getMaxSpecs(cycAccess.getConstantByName("#$CanineAnimal"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(maxSpecs);
        Assert.assertTrue(maxSpecs instanceof CycList);
        maxSpecs = ((CycList) maxSpecs).sort();
        Assert.assertEquals("(Coyote-Animal Dog Fox Jackal Wolf)", maxSpecs.toString());

        // getGenlSiblings.
        List genlSiblings = null;
        try {
            genlSiblings = cycAccess.getGenlSiblings(cycAccess.getConstantByName("#$Dog"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(genlSiblings);
        Assert.assertTrue(genlSiblings instanceof CycList);
        genlSiblings = ((CycList) genlSiblings).sort();
        Assert.assertEquals("(Animal DomesticPet FemaleAnimal JuvenileAnimal)", genlSiblings.toString());

        // getSiblings.
        List siblings = null;
        try {
            siblings = cycAccess.getSiblings(cycAccess.getConstantByName("#$Dog"));
            Assert.assertNotNull(siblings);
            Assert.assertTrue(siblings instanceof CycList);
            Assert.assertTrue(siblings.contains(cycAccess.getConstantByName("Goose-Domestic")));
            Assert.assertTrue(siblings.contains(cycAccess.getConstantByName("Goat-Domestic")));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }

        // getSpecSiblings.
        List specSiblings = null;
        try {
            specSiblings = cycAccess.getSpecSiblings(cycAccess.getConstantByName("#$Dog"));
            Assert.assertNotNull(specSiblings);
            Assert.assertTrue(specSiblings instanceof CycList);
            Assert.assertTrue(specSiblings.contains(cycAccess.getConstantByName("Goose-Domestic")));
            Assert.assertTrue(specSiblings.contains(cycAccess.getConstantByName("Goat-Domestic")));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }

        // getAllGenls.
        List allGenls = null;
        try {
            allGenls = cycAccess.getAllGenls(cycAccess.getConstantByName("#$ExistingObjectType"));
            Assert.assertNotNull(allGenls);
            Assert.assertTrue(allGenls instanceof CycList);
            Assert.assertTrue(allGenls.contains(cycAccess.getConstantByName("ObjectType")));
            Assert.assertTrue(allGenls.contains(cycAccess.getConstantByName("Thing")));
            }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }

        // getAllSpecs.
        List allSpecs = null;
        try {
            allSpecs = cycAccess.getAllSpecs(cycAccess.getConstantByName("#$CanineAnimal"));
            Assert.assertNotNull(allSpecs);
            Assert.assertTrue(allSpecs instanceof CycList);
            Assert.assertTrue(allSpecs.contains(cycAccess.getConstantByName("Jackal")));
            Assert.assertTrue(allSpecs.contains(cycAccess.getConstantByName("Retriever-Dog")));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }

        // getAllGenlsWrt.
        List allGenlsWrt = null;
        try {
            allGenlsWrt = cycAccess.getAllGenlsWrt(cycAccess.getConstantByName("Dog"),
                                                   cycAccess.getConstantByName("#$Animal"));
            Assert.assertNotNull(allGenlsWrt);
            Assert.assertTrue(allGenlsWrt instanceof CycList);
            Assert.assertTrue(allGenlsWrt.contains(cycAccess.getConstantByName("TameAnimal")));
            Assert.assertTrue(allGenlsWrt.contains(cycAccess.getConstantByName("AirBreathingVertebrate")));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }

        // getAllDependentSpecs.
        List allDependentSpecs = null;
        try {
            allDependentSpecs = cycAccess.getAllDependentSpecs(cycAccess.getConstantByName("CanineAnimal"));
            Assert.assertNotNull(allDependentSpecs);
            Assert.assertTrue(allDependentSpecs instanceof CycList);
            Assert.assertTrue(allDependentSpecs.contains(cycAccess.getConstantByName("Wolf-Gray")));
            Assert.assertTrue(allDependentSpecs.contains(cycAccess.getConstantByName("Wolf")));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }

        // getSampleLeafSpecs.
        List sampleLeafSpecs = null;
        try {
            sampleLeafSpecs = cycAccess.getSampleLeafSpecs(cycAccess.getConstantByName("CanineAnimal"), 3);
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(sampleLeafSpecs);
        Assert.assertTrue(sampleLeafSpecs instanceof CycList);
        //System.out.println("sampleLeafSpecs: " + sampleLeafSpecsArrayList);
        Assert.assertTrue(sampleLeafSpecs.size() > 0);

        // isSpecOf.
        boolean answer = true;
        try {
            answer = cycAccess.isSpecOf(cycAccess.getConstantByName("#$Dog"),
                                        cycAccess.getConstantByName("Animal"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertTrue(answer);

        // isGenlOf.
        answer = true;
        try {
            answer = cycAccess.isGenlOf(cycAccess.getConstantByName("CanineAnimal"),
                                        cycAccess.getConstantByName("Wolf"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertTrue(answer);

        // areTacitCoextensional.
        answer = true;
        try {
            answer = cycAccess.areTacitCoextensional(cycAccess.getConstantByName("SinglePurposeDevice"),
                                                     cycAccess.getConstantByName("PhysicalDevice"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertTrue(answer);

        // areAssertedCoextensional.
        answer = true;
        try {
            answer = cycAccess.areAssertedCoextensional(cycAccess.getConstantByName("SinglePurposeDevice"),
                                                        cycAccess.getConstantByName("PhysicalDevice"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertTrue(answer);

        // areIntersecting.
        answer = true;
        //cycAccess.traceOn();
        try {
            answer = cycAccess.areIntersecting(cycAccess.getConstantByName("DomesticatedAnimal"),
                                               cycAccess.getConstantByName("TameAnimal"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertTrue(answer);
        //cycAccess.traceOff();

        // areHierarchical.
        answer = true;
        try {
            answer = cycAccess.areHierarchical(cycAccess.getConstantByName("CanineAnimal"),
                                               cycAccess.getConstantByName("Wolf"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertTrue(answer);

    }

    /**
     * Tests a portion of the CycAccess methods using the ascii api connection.
     */
    public void testAsciiCycAccess4 () {
        System.out.println("**** testAsciiCycAccess 4 ****");
        CycAccess cycAccess = null;
        try {
            cycAccess = new CycAccess(CycConnection.DEFAULT_HOSTNAME,
                                      CycConnection.DEFAULT_BASE_PORT,
                                      CycConnection.ASCII_MODE,
                                      CycAccess.PERSISTENT_CONNECTION);
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }

        doTestCycAccess4(cycAccess);

        try {
            cycAccess.close();
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        System.out.println("**** testAsciiCycAccess 4 OK ****");
    }


    /**
     * Tests a portion of the CycAccess methods using the binary api connection.
     */
    public void testBinaryCycAccess4 () {
        System.out.println("**** testBinaryCycAccess 4 ****");
        CycAccess cycAccess = null;
        try {
            cycAccess = new CycAccess(CycConnection.DEFAULT_HOSTNAME,
                                      CycConnection.DEFAULT_BASE_PORT,
                                      CycConnection.BINARY_MODE,
                                      CycAccess.PERSISTENT_CONNECTION);
        }
        catch (Exception e) {
            Assert.fail(e.toString());
        }

        doTestCycAccess4(cycAccess);

        try {
            cycAccess.close();
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        System.out.println("**** testBinaryCycAccess 4 OK ****");
    }

    /**
     * Tests a portion of the CycAccess methods using the given api connection.
     */
    protected void doTestCycAccess4 (CycAccess cycAccess) {
        // getWhyGenl.
        CycList whyGenl = null;
        try {
            whyGenl = cycAccess.getWhyGenl(cycAccess.getConstantByName("Dog"),
                                           cycAccess.getConstantByName("Animal"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(whyGenl);
        CycConstant whyGenlFirst = (CycConstant) ((CycList) ((CycList) whyGenl.first()).first()).second();
        CycConstant whyGenlLast = (CycConstant) ((CycList) ((CycList) whyGenl.last()).first()).third();
        try {
            Assert.assertEquals(cycAccess.getConstantByName("Dog"), whyGenlFirst);
            Assert.assertEquals(cycAccess.getConstantByName("Animal"), whyGenlLast);
        }
        catch (Exception e) {
            Assert.fail(e.toString());
        }

        // getWhyGenlParaphrase.
        ArrayList whyGenlParaphrase = null;
        try {
            whyGenlParaphrase = cycAccess.getWhyGenlParaphrase(cycAccess.getConstantByName("Dog"),
                                                               cycAccess.getConstantByName("Animal"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(whyGenlParaphrase);
        String oneExpectedGenlParaphrase = "if ?OBJ is a non-human animal, then ?OBJ is a sentient animal";
        /*
        for (int i = 0; i < whyGenlParaphrase.size(); i++) {
            System.out.println(whyGenlParaphrase.get(i));
        }
        */
        Assert.assertTrue(whyGenlParaphrase.contains(oneExpectedGenlParaphrase));

        // getWhyCollectionsIntersect.
        List whyCollectionsIntersect = null;
        try {
            whyCollectionsIntersect =
                cycAccess.getWhyCollectionsIntersect(cycAccess.getConstantByName("DomesticatedAnimal"),
                                                     cycAccess.getConstantByName("NonPersonAnimal"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(whyCollectionsIntersect);
        Assert.assertTrue(whyCollectionsIntersect instanceof CycList);
        CycList expectedWhyCollectionsIntersect =
            cycAccess.makeCycList("(((#$genls #$DomesticatedAnimal #$TameAnimal) :TRUE) " +
                                  "((#$genls #$TameAnimal #$NonPersonAnimal) :TRUE))");
        Assert.assertEquals(expectedWhyCollectionsIntersect.toString(), whyCollectionsIntersect.toString());
        Assert.assertEquals(expectedWhyCollectionsIntersect, whyCollectionsIntersect);

        // getWhyCollectionsIntersectParaphrase.
        ArrayList whyCollectionsIntersectParaphrase = null;
        try {
            //cycAccess.traceOn();
            whyCollectionsIntersectParaphrase =
                cycAccess.getWhyCollectionsIntersectParaphrase(cycAccess.getConstantByName("DomesticatedAnimal"),
                                                               cycAccess.getConstantByName("NonPersonAnimal"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(whyCollectionsIntersectParaphrase);
        String oneExpectedCollectionsIntersectParaphrase =
            "if ?OBJ is a domesticated animal (tame animal), then ?OBJ is a tame animal";
        //System.out.println(whyCollectionsIntersectParaphrase);
        Assert.assertTrue(whyCollectionsIntersectParaphrase.contains(oneExpectedCollectionsIntersectParaphrase));

        // getCollectionLeaves.
        List collectionLeaves = null;
        try {
            collectionLeaves = cycAccess.getCollectionLeaves(cycAccess.getConstantByName("CanineAnimal"));
            Assert.assertNotNull(collectionLeaves);
            Assert.assertTrue(collectionLeaves instanceof CycList);
            Assert.assertTrue(collectionLeaves.contains(cycAccess.getConstantByName("RedWolf")));
            Assert.assertTrue(collectionLeaves.contains(cycAccess.getConstantByName("SanJoaquinKitFox")));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }

        // getLocalDisjointWith.
        List localDisjointWiths = null;
        try {
            localDisjointWiths = cycAccess.getLocalDisjointWith(cycAccess.getConstantByName("Plant"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertEquals(localDisjointWiths, cycAccess.makeCycList("(#$Animal)"));

        // areDisjoint.
        boolean answer = true;
        try {
            answer = cycAccess.areDisjoint(cycAccess.getConstantByName("Animal"),
                                           cycAccess.getConstantByName("Plant"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertTrue(answer);

        // getMinIsas.
        List minIsas = null;
        try {
            minIsas = cycAccess.getMinIsas(cycAccess.getConstantByName("Wolf"));
            Assert.assertTrue(minIsas.contains(cycAccess.getConstantByName("OrganismClassificationType")));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }

        // getInstances.
        List instances = null;
        try {
            instances = cycAccess.getInstances(cycAccess.getConstantByName("Animal"));
            Assert.assertTrue(instances instanceof CycList);
            Assert.assertTrue(((CycList) instances).contains(cycAccess.getConstantByName("Bigfoot")));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }

        // getInstanceSiblings.
        List instanceSiblings = null;
        try {
            instanceSiblings = cycAccess.getInstanceSiblings(cycAccess.getConstantByName("Bigfoot"));
            Assert.assertTrue(instanceSiblings.contains(cycAccess.getConstantByName("Oceanus-TheTitan")));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }

        // getAllIsa.
        List allIsas = null;
        try {
            //cycAccess.traceOn();
            allIsas = cycAccess.getAllIsa(cycAccess.getConstantByName("Animal"));
            //System.out.println(allIsas);
            Assert.assertTrue(allIsas.contains(cycAccess.getConstantByName("#$OrganismClassificationType")));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }

        // getAllInstances.
        List allInstances = null;
        try {
            allInstances = cycAccess.getAllInstances(cycAccess.getConstantByName("Plant"));
            Assert.assertTrue(allInstances.contains(cycAccess.getConstantByName("TreatyOak")));
            Assert.assertTrue(allInstances.contains(cycAccess.getConstantByName("BurningBushOldTestament")));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }

        // isa.
        answer = true;
        try {
            answer = cycAccess.isa(cycAccess.getConstantByName("TreatyOak"),
                                   cycAccess.getConstantByName("Plant"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertTrue(answer);
    }

    /**
     * Tests a portion of the CycAccess methods using the ascii api connection.
     */
    public void testAsciiCycAccess5 () {
        System.out.println("**** testAsciiCycAccess 5 ****");
        CycAccess cycAccess = null;
        try {
            cycAccess = new CycAccess(CycConnection.DEFAULT_HOSTNAME,
                                      CycConnection.DEFAULT_BASE_PORT,
                                      CycConnection.ASCII_MODE,
                                      CycAccess.PERSISTENT_CONNECTION);
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }

        doTestCycAccess5(cycAccess);

        try {
            cycAccess.close();
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        System.out.println("**** testAsciiCycAccess 5 OK ****");
    }


    /**
     * Tests a portion of the CycAccess methods using the binary api connection.
     */
    public void testBinaryCycAccess5 () {
        System.out.println("**** testBinaryCycAccess 5 ****");
        CycAccess cycAccess = null;
        try {
            cycAccess = new CycAccess(CycConnection.DEFAULT_HOSTNAME,
                                      CycConnection.DEFAULT_BASE_PORT,
                                      CycConnection.BINARY_MODE,
                                      CycAccess.PERSISTENT_CONNECTION);
        }
        catch (Exception e) {
            Assert.fail(e.toString());
        }

        doTestCycAccess5(cycAccess);

        try {
            cycAccess.close();
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        System.out.println("**** testBinaryCycAccess 5 OK ****");
    }

    /**
     * Tests a portion of the CycAccess methods using the given api connection.
     */
    protected void doTestCycAccess5 (CycAccess cycAccess) {
        // createNewPermanent.
        CycConstant cycConstant = null;
        try {
            cycConstant = cycAccess.createNewPermanent("CycAccessTestConstant");
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(cycConstant);
        Assert.assertEquals("CycAccessTestConstant", cycConstant.name);

        // kill.
        try {
            cycAccess.kill(cycConstant);
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }

        // assertComment.
        cycConstant = null;
        try {
            cycConstant = cycAccess.createNewPermanent("CycAccessTestConstant");
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(cycConstant);
        Assert.assertEquals("CycAccessTestConstant", cycConstant.name);

        CycConstant baseKb = null;
        try {
            baseKb = cycAccess.getConstantByName("BaseKB");
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(cycConstant);
        Assert.assertEquals("BaseKB", baseKb.name);
        String assertedComment = "A test comment";
        try {
            cycAccess.assertComment(cycConstant, assertedComment, baseKb);
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        String comment = null;
        try {
            comment = cycAccess.getComment(cycConstant);
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertEquals(assertedComment, comment);

        try {
            cycAccess.kill(cycConstant);
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        try {
            Assert.assertNull(cycAccess.getConstantByName("CycAccessTestConstant"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }

        // isValidConstantName.
        boolean answer = true;
        try {
            answer = cycAccess.isValidConstantName("abc");
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertTrue(answer);

        answer = true;
        try {
            answer = cycAccess.isValidConstantName(" abc");
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertTrue(! answer);

        answer = true;
        try {
            answer = cycAccess.isValidConstantName("[abc]");
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertTrue(! answer);

        // createMicrotheory.
        CycConstant mt = null;
        ArrayList genlMts = new ArrayList();
        try {
            genlMts.add(cycAccess.getConstantByName("ModernMilitaryMt"));
            mt = cycAccess.createMicrotheory("CycAccessTestMt",
                                             "a unit test comment for the CycAccessTestMt microtheory.",
                                             cycAccess.getConstantByName("Microtheory"),
                                             genlMts);
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertNotNull(mt);
        try {
            cycAccess.kill(mt);
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        try {
            Assert.assertNull(cycAccess.getConstantByName("CycAccessTestMt"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }

        // createMicrotheorySystem.
        CycConstant[] mts = {null, null, null};
        genlMts = new ArrayList();
        try {
            genlMts.add(cycAccess.getConstantByName("ModernMilitaryMt"));
            mts = cycAccess.createMicrotheorySystem("CycAccessTest",
                                                    "a unit test comment for the CycAccessTestMt microtheory.",
                                                    genlMts);
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        Assert.assertTrue(mts.length == 3);
        Assert.assertNotNull(mts[0]);
        Assert.assertEquals("#$CycAccessTestMt", mts[0].cyclify());
        Assert.assertNotNull(mts[1]);
        Assert.assertEquals("#$CycAccessTestVocabMt", mts[1].cyclify());
        Assert.assertNotNull(mts[2]);
        Assert.assertEquals("#$CycAccessTestDataMt", mts[2].cyclify());
        try {
            cycAccess.kill(mts);
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        try {
            Assert.assertNull(cycAccess.getConstantByName("CycAccessTestMt"));
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }

        // askWithVariable
        try {
            CycList query = CycAccess.current().makeCycList("(#$objectFoundInLocation ?WHAT #$CityOfAustinTX)");
            CycVariable variable = CycVariable.makeCycVariable("?WHAT");
            mt = CycAccess.current().getConstantByName("EverythingPSC");
            CycList response = CycAccess.current().askWithVariable(query, variable, mt);
            Assert.assertNotNull(response);
            Assert.assertTrue(response.contains(CycAccess.current().getConstantByName("#$UniversityOfTexasAtAustin")));
        }
        catch (Exception e) {
            Assert.fail(e.toString());
        }

        // isQueryTrue
        try {
            //cycAccess.traceOn();
            CycList query = CycAccess.current().makeCycList("(#$objectFoundInLocation #$UniversityOfTexasAtAustin #$CityOfAustinTX)");
            mt = CycAccess.current().getConstantByName("EverythingPSC");
            Assert.assertTrue(CycAccess.current().isQueryTrue(query, mt));
            query = CycAccess.current().makeCycList("(#$objectFoundInLocation #$UniversityOfTexasAtAustin #$CityOfHoustonTX)");
            Assert.assertTrue(! CycAccess.current().isQueryTrue(query, mt));
        }
        catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.toString());
        }

        // countAllInstances
        try {
            cycAccess = CycAccess.current();
            Assert.assertTrue(cycAccess.countAllInstances(cycAccess.getConstantByName("Country"),
                                                          cycAccess.getConstantByName("WorldGeographyMt")) > 0);
        }
        catch (Exception e) {
            Assert.fail(e.toString());
        }
    }

    /**
     * Tests a portion of the CycAccess methods using the ascii api connection.
     */
    public void testAsciiCycAccess6 () {
        System.out.println("**** testAsciiCycAccess 6 ****");
        CycAccess cycAccess = null;
        try {
            cycAccess = new CycAccess(CycConnection.DEFAULT_HOSTNAME,
                                      CycConnection.DEFAULT_BASE_PORT,
                                      CycConnection.ASCII_MODE,
                                      CycAccess.PERSISTENT_CONNECTION);
        }
        catch (UnknownHostException e) {
            Assert.fail(e.toString());
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }

        doTestCycAccess6(cycAccess);

        try {
            cycAccess.close();
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        System.out.println("**** testAsciiCycAccess 6 OK ****");
    }


    /**
     * Tests a portion of the CycAccess methods using the binary api connection.
     */
    public void testBinaryCycAccess6 () {
        System.out.println("**** testBinaryCycAccess 6 ****");
        CycAccess cycAccess = null;
        try {
            cycAccess = new CycAccess(CycConnection.DEFAULT_HOSTNAME,
                                      CycConnection.DEFAULT_BASE_PORT,
                                      CycConnection.BINARY_MODE,
                                      CycAccess.PERSISTENT_CONNECTION);
        }
        catch (Exception e) {
            Assert.fail(e.toString());
        }

        doTestCycAccess6(cycAccess);

        try {
            cycAccess.close();
        }
        catch (IOException e) {
            Assert.fail(e.toString());
        }
        System.out.println("**** testBinaryCycAccess 6 OK ****");
    }

    /**
     * Tests a portion of the CycAccess methods using the given api connection.
     */
    protected void doTestCycAccess6 (CycAccess cycAccess) {
        // Test common constants.
        try {
            Assert.assertEquals(cycAccess.getConstantByName("and"), CycAccess.and);
            Assert.assertEquals(cycAccess.getConstantByName("BaseKB"), CycAccess.baseKB);
            Assert.assertEquals(cycAccess.getConstantByName("BinaryPredicate"), CycAccess.binaryPredicate);
            Assert.assertEquals(cycAccess.getConstantByName("comment"), CycAccess.comment);
            Assert.assertEquals(cycAccess.getConstantByName("different"), CycAccess.different);
            Assert.assertEquals(cycAccess.getConstantByName("elementOf"), CycAccess.elementOf);
            Assert.assertEquals(cycAccess.getConstantByName("genlMt"), CycAccess.genlMt);
            Assert.assertEquals(cycAccess.getConstantByName("genls"), CycAccess.genls);
            Assert.assertEquals(cycAccess.getConstantByName("isa"), CycAccess.isa);
            Assert.assertEquals(cycAccess.getConstantByName("numericallyEqual"), CycAccess.numericallyEqual);
            Assert.assertEquals(cycAccess.getConstantByName("or"), CycAccess.or);
            Assert.assertEquals(cycAccess.getConstantByName("PlusFn"), CycAccess.plusFn);
        }
        catch (Exception e) {
            Assert.fail(e.toString());
        }

        // Test isBackchainRequired, isBackchainEncouraged, isBackchainDiscouraged, isBackchainForbidden
        try {
            Assert.assertTrue(cycAccess.isBackchainRequired(cycAccess.getConstantByName("#$notAssertible"),
                                                            cycAccess.baseKB));
            Assert.assertTrue(! cycAccess.isBackchainEncouraged(cycAccess.getConstantByName("#$notAssertible"),
                                                                cycAccess.baseKB));
            Assert.assertTrue(! cycAccess.isBackchainDiscouraged(cycAccess.getConstantByName("#$notAssertible"),
                                                                 cycAccess.baseKB));
            Assert.assertTrue(! cycAccess.isBackchainForbidden(cycAccess.getConstantByName("#$notAssertible"),
                                                               cycAccess.baseKB));

            Assert.assertTrue(! cycAccess.isBackchainRequired(cycAccess.getConstantByName("#$nearestIsa"),
                                                            cycAccess.baseKB));
            Assert.assertTrue(! cycAccess.isBackchainEncouraged(cycAccess.getConstantByName("#$nearestIsa"),
                                                                cycAccess.baseKB));
            Assert.assertTrue(! cycAccess.isBackchainDiscouraged(cycAccess.getConstantByName("#$nearestIsa"),
                                                                 cycAccess.baseKB));
            Assert.assertTrue(cycAccess.isBackchainForbidden(cycAccess.getConstantByName("#$nearestIsa"),
                                                               cycAccess.baseKB));
        }
        catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.toString());
        }


        // Test getBackchainRules.
        try {
            //cycAccess.traceOn();
            CycList backchainRules =
                cycAccess.getBackchainRules(cycAccess.getConstantByName("#$doneBy"),
                                            cycAccess.getConstantByName("HumanActivitiesMt"));
            Assert.assertNotNull(backchainRules);
            //for (int i = 0; i < backchainRules.size(); i++)
            //    System.out.println(((CycList) backchainRules.get(i)).cyclify());
        }
        catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.toString());
        }

        // Test getForwardChainRules.
        try {
            //cycAccess.traceOn();
            CycList forwardChainRules =
                cycAccess.getForwardChainRules(cycAccess.getConstantByName("#$doneBy"),
                                            cycAccess.getConstantByName("HumanActivitiesMt"));
            Assert.assertNotNull(forwardChainRules);
            /*
            for (int i = 0; i < forwardChainRules.size(); i++)
                System.out.println(((CycList) forwardChainRules.get(i)).cyclify());
            */
        }
        catch (Exception e) {
            Assert.fail(e.toString());
        }

        // setSymbolValue, getSymbolValue
        try {
            CycSymbol a = CycSymbol.makeCycSymbol("a");
            cycAccess.setSymbolValue(a, new Integer(1));
            Assert.assertEquals(new Integer(1), cycAccess.getSymbolValue(a));
            cycAccess.setSymbolValue(a, "abc");
            Assert.assertEquals("abc", cycAccess.getSymbolValue(a));
            cycAccess.setSymbolValue(a, CycSymbol.t);
            Assert.assertEquals(CycSymbol.t, cycAccess.getSymbolValue(a));
            cycAccess.setSymbolValue(a, CycSymbol.nil);
            Assert.assertEquals(CycSymbol.nil, cycAccess.getSymbolValue(a));
            CycConstant brazil = cycAccess.makeCycConstant("#$Brazil");
            cycAccess.setSymbolValue(a, brazil);
            Assert.assertEquals(brazil, cycAccess.getSymbolValue(a));
            CycList valueList1 = cycAccess.makeCycList("(QUOTE (#$France #$Brazil))");
            CycList valueList2 = cycAccess.makeCycList("(#$France #$Brazil)");
            cycAccess.setSymbolValue(a, valueList1);
            Assert.assertEquals(valueList2, cycAccess.getSymbolValue(a));
        }
        catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.toString());
        }

        // Test getCycNartById
        Integer nartId = new Integer(1);
        try {
            CycNart nart1 = cycAccess.getCycNartById(nartId);
            Assert.assertNotNull(nart1);
            Assert.assertNotNull(nart1.getFunctor());
            Assert.assertTrue(nart1.getFunctor() instanceof CycFort);
            Assert.assertNotNull(nart1.getArguments());
            Assert.assertTrue(nart1.getArguments() instanceof CycList);
            //System.out.println(nart1.cyclify());
        }
        catch (Exception e) {
            Assert.fail(e.toString());
        }

        // Narts in a list.
        try {
            //cycAccess.traceOn();
            CycNart nart1 = cycAccess.getCycNartById(nartId);
            CycNart nart2 = cycAccess.getCycNartById(nartId);
            Assert.assertEquals(nart1, nart2);
            CycList valueList = new CycList();
            valueList.add(CycSymbol.quote);
            CycList nartList = new CycList();
            valueList.add(nartList);
            nartList.add(nart1);
            nartList.add(nart2);
            CycSymbol a = CycSymbol.makeCycSymbol("a");
            cycAccess.setSymbolValue(a, valueList);
            Object object = cycAccess.getSymbolValue(a);
            Assert.assertNotNull(object);
            Assert.assertTrue(object instanceof CycList);
            CycList nartList1 = (CycList) object;
            Object element1 = nartList1.first();
            Assert.assertTrue((element1 instanceof CycNart) || (element1 instanceof CycList));
            if (element1 instanceof CycList)
                element1 = CycNart.coerceToCycNart(element1);
            CycNart nart3 = (CycNart) element1;
            Assert.assertNotNull(nart3.getFunctor());
            Assert.assertTrue(nart3.getFunctor() instanceof CycFort);
            Assert.assertNotNull(nart3.getArguments());
            Assert.assertTrue(nart3.getArguments() instanceof CycList);
            Object element2 = nartList1.second();
            Assert.assertTrue((element2 instanceof CycNart) || (element2 instanceof CycList));
            if (element2 instanceof CycList)
                element2 = CycNart.coerceToCycNart(element2);
            CycNart nart4 = (CycNart) element2;
            Assert.assertNotNull(nart4.getFunctor());
            Assert.assertTrue(nart4.getFunctor() instanceof CycFort);
            Assert.assertNotNull(nart4.getArguments());
            Assert.assertTrue(nart4.getArguments() instanceof CycList);
            Assert.assertEquals(nart1, nart3);
            Assert.assertEquals(nart1, nart4);
        }
        catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.toString());
        }

        // isWellFormedFormula
        try {
            Assert.assertTrue(cycAccess.isWellFormedFormula(cycAccess.makeCycList("(#$genls #$Dog #$Animal)")));
            // Not true, but still well formed.
            Assert.assertTrue(cycAccess.isWellFormedFormula(cycAccess.makeCycList("(#$genls #$Dog #$Plant)")));
            Assert.assertTrue(cycAccess.isWellFormedFormula(cycAccess.makeCycList("(#$genls ?X #$Animal)")));
            Assert.assertTrue(! cycAccess.isWellFormedFormula(cycAccess.makeCycList("(#$genls #$Dog #$Brazil)")));
            Assert.assertTrue(! cycAccess.isWellFormedFormula(cycAccess.makeCycList("(#$genls ?X #$Brazil)")));
        }
        catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.toString());
        }

        // isEvaluatablePredicate
        try {
            Assert.assertTrue(cycAccess.isEvaluatablePredicate(cycAccess.getKnownConstantByName("#$different")));
            Assert.assertTrue(! cycAccess.isEvaluatablePredicate(cycAccess.getKnownConstantByName("#$doneBy")));
        }
        catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.toString());
        }

        // hasSomePredicateUsingTerm
        try {
            Assert.assertTrue(cycAccess.hasSomePredicateUsingTerm(cycAccess.getKnownConstantByName("#$percentOfRegionIs"),
                                                                  cycAccess.getKnownConstantByName("#$Algeria"),
                                                                  new Integer(1),
                                                                  cycAccess.getKnownConstantByName("CIAWorldFactbook1995Mt")));

            Assert.assertTrue(cycAccess.hasSomePredicateUsingTerm(cycAccess.getKnownConstantByName("#$percentOfRegionIs"),
                                                                  cycAccess.getKnownConstantByName("#$Algeria"),
                                                                  new Integer(1),
                                                                  cycAccess.getKnownConstantByName("#$InferencePSC")));
            Assert.assertTrue(! cycAccess.hasSomePredicateUsingTerm(cycAccess.getKnownConstantByName("#$percentOfRegionIs"),
                                                                    cycAccess.getKnownConstantByName("#$Algeria"),
                                                                    new Integer(2),
                                                                    cycAccess.getKnownConstantByName("CIAWorldFactbook1995Mt")));
        }
        catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.toString());
        }
    }
}




