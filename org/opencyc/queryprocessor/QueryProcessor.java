package org.opencyc.queryprocessor;

import java.util.*;
import java.io.IOException;
import org.opencyc.cycobject.*;
import org.opencyc.api.*;

/**
 * <tt>Rule</tt> object to model the attributes and behavior of a query processor.<p>
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
public class QueryProcessor {

    /**
     * CycAccess object which provides the OpenCyc api.
     */
    CycAccess cycAccess;

    /**
     * <tt>Backchainer</tt> for this <tt>QueryProcessor</tt>.
     */
    protected Backchainer backchainer = new Backchainer(this);

    /**
     * <tt>Solution</tt> for this <tt>QueryProcessor</tt>.
     */
    protected Solution solution = new Solution(this);

    /**
     * The OpenCyc microtheory in which the query should be asked.
     */
    public CycConstant mt;

    /**
     * The number of solutions requested.  When <tt>null</tt>, all solutions are sought.
     */
    public Integer nbrSolutionsRequested = new Integer(1);

    /**
     * The default verbosity of the query processor output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     */
    public static final int DEFAULT_VERBOSITY = 3;

    /**
     * Sets verbosity of the query processor output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     */
    protected int verbosity = DEFAULT_VERBOSITY;

    /**
     * Collection of the query literals.
     */
    protected ArrayList queryLiterals;

    /**
     * <tt>ProblemParser</tt> object for this <tt>QueryProcessor</tt>.
     */
    protected QueryParser queryParser = new QueryParser(this);

    /**
     * Collection of the query variables as <tt>CycVariable</tt> objects.
     */
    protected ArrayList variables = new ArrayList();

    /**
     * Number of KB asks performed during the search for solution(s).
     */
    protected int nbrAsks = 0;

    /**
     * The input query <tt>CycList</tt>.
     */
    protected CycList query = null;

    /**
     * Constructs a new <tt>QueryProcessor</tt> object, creating a new <tt>CycAccess</tt>
     * object.
     */
    public QueryProcessor() {
        this(initializeCycAccess());
    }

    /**
     * Creates a <tt>CycAccess</tt> object to connect to the OpenCyc server and provide api
     * services.
     *
     * @return a <tt>CycAccess</tt> object to connect to the OpenCyc server and provide api
     * services
     */
    protected static CycAccess initializeCycAccess() {
        CycAccess cycAccess = null;
        try {
            cycAccess = new CycAccess();
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Cannot access OpenCyc server " + e.getMessage());
            System.exit(1);
        }
        return cycAccess;
    }

    /**
     * Constructs a new <tt>QueryProcessor</tt> object given an existing connection to the
     * OpenCyc server.
     *
     * @param cycAccess the <tt>CycAccess</tt> object to use for this query
     */
    public QueryProcessor(CycAccess cycAccess) {
        this.cycAccess = cycAccess;
        try {
            // Default microtheory
            mt = cycAccess.makeCycConstant("EverythingPSC");
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Cannot access OpenCyc server " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Asks the input query and returns a list of solutions if one or more
     * was found, otherwise returns <tt>null</tt>.
     *
     * @param queryString a query in the form of an OpenCyc query string
     * @return an <tt>ArrayList</tt> of solutions or <tt>null</tt> if no solutions were
     * found.  Each solution is an <tt>ArrayList</tt> of variable binding <tt>ArrayList</tt>
     * objects, each binding having the form of an <tt>ArrayList</tt> where the first
     * element is the <tt>CycVariable</tt> and the second element is the bound value
     * <tt>Object</tt>.
     */
    public ArrayList ask(String queryString) {
        return ask(cycAccess.makeCycList(queryString));
    }

    /**
     * Solves a query and return a list of solutions if one or more
     * was found, otherwise returns <tt>null</tt>.
     *
     * @param query a query in the form of an OpenCyc <tt>CycList</tt> object
     * @return an <tt>ArrayList</tt> of solutions or <tt>null</tt> if no solutions were
     * found.  Each solution is an <tt>ArrayList</tt> of variable binding <tt>ArrayList</tt>
     * objects, each binding having the form of an <tt>ArrayList</tt> where the first
     * element is the <tt>CycVariable</tt> and the second element is the bound value
     * <tt>Object</tt>.
     */
    public ArrayList solve(CycList query) {
        long startMilliseconds = System.currentTimeMillis();
        this.query = query;
        try {
            if (! queryParser.extractLiterals()) {
                long endMilliseconds = System.currentTimeMillis();
                if (verbosity > 0) {
                    System.out.println("No solution because an input literal cannot be satisfied");
                    System.out.println((endMilliseconds - startMilliseconds) + " milliseconds");
                }
                return new ArrayList();
            }
            queryParser.gatherVariables();
            if (verbosity > 0) {
                if (nbrSolutionsRequested == null) {
                    if (backchainer.backchainDepth > 0)
                        System.out.println("Asking for all solutions at backchain depth " +
                                           backchainer.backchainDepth);
                    else
                        System.out.println("Asking for all solutions");
                }
                else if (nbrSolutionsRequested.intValue() == 1)
                    System.out.println("Asking for the first solution");
                else
                    System.out.println("Asking for " + nbrSolutionsRequested + " solutions");
            }
            literalAsker = new LiteralAsker(this);
            literalAsker.ask();
        }
        catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error accessing OpenCyc " + e.getMessage());
            System.exit(1);
        }
        long endMilliseconds = System.currentTimeMillis();
        if (verbosity > 0)
            System.out.println("  " + (endMilliseconds - startMilliseconds) + " milliseconds");

        return solution.solutions;
    }

    /**
     * Returns the number of <tt>QueryLiteral</tt> objects derived from
     * the input query.
     *
     * @return the number of constraint <tt>Rule</tt> objects.
     */
    public int getNbrQueryLiterals() {
        return queryLiterals.size();
    }

    /**
     * Returns the number of <tt>Variable</tt> objects derived from
     * the input problem.
     *
     * @return the number of <tt>CycVariable</tt> objects.
     */
    public int getNbrVariables() {
        return variables.size();
    }

    /**
     * Displays the input query literals.
     */
    public void displayQueryLiterals() {
        if (queryLiterals.size() > 0) {
            System.out.println("Query literals");
            for (int i = 0; i < queryLiterals.size(); i++) {
                QueryLiteral queryLiteral = (QueryLiteral) queryLiterals.get(i);
                if (queryLiteral.nbrFormulaInstances == -1)
                    System.out.println("  " + queryLiteral.cyclify());
                else
                    System.out.println("  " + queryLiteral.cyclify() + "  " +
                                       queryLiteral.nbrFormulaInstances + " instances");
            }
        }
        else
            System.out.println("No query literals");
        System.out.println();
    }

    /**
     * Displays the literals and their binding set.
     */
    public void displayLiteralsAndBindings() {
        literalBindings.displayLiteralsAndBindings();
    }

    /**
     * Sets verbosity of the query processor output.  0 --> quiet ... 9 -> maximum
     * diagnostic input.
     *
     * @param verbosity 0 --> quiet ... 9 -> maximum diagnostic input
     */
    public void setVerbosity(int verbosity) {
        this.verbosity = verbosity;
        queryParser.setVerbosity(verbosity);
        literalBindings.setVerbosity(verbosity);
        backchainer.setVerbosity(verbosity);
        hashJoiner.setVerbosity(verbosity);
        literalAsker.setVerbosity(verbosity);
        solution.setVerbosity(verbosity);
    }

    /**
     * Sets the maximum depth of backchaining from an input query. A value of zero indicates
     * no backchaining.
     *
     * @param maxBackchainDepth the maximum depth of backchaining, or zero if no backchaing on the input
     * queries
     */
    public void setMaxBackchainDepth(int maxBackchainDepth) {
        backchainer.setMaxBackchainDepth(maxBackchainDepth);
    }

    /**
     * Sets whether backchaining is performed on rules with the predicate of #$isa or #$genls.  Large
     * numbers of rules conclude #$isa or #$genls, which are not usually relevant - so the default is
     * false.
     *
     * @param sbhlBackchain whether backchaining is performed on rules with the predicate of #$isa or #$genls
     */
    public void setSbhlBackchain(boolean sbhlBackchain) {
        backchainer.setSbhlBackchain(sbhlBackchain);
    }



}