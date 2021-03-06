package org.opencyc.cyclobject.subl;

import org.opencyc.cyclobject.*;

/*****************************************************************************
 *
 * KB comment for #$SubLNonVariableSymbol as of 2002/05/07:<p>
 *
 * The collection of all #$SubLSymbols except #$SubLVariables (qq.v.); a
 * subcollection of #$CycLClosedAtomicTerm.  Note that `symbol' has a very
 * specific, technical meaning in SubL; #$SubLNonVariableSymbols are rarely used
 * in CycL assertions, except within those built with certain
 * #$CycInferenceDescriptorPredicates like #$defnIff. Examples of SubL
 * non-variable symbols include the symbols `GENLS' and
 * `CYC-SYSTEM-NON-VARIABLE-SYMBOL-P'.  Note that this collection, like most
 * instances of #$CycLExpressionType, is "quoted" (see #$quotedCollection).<p>
 * 
 * @version $Id$
 * @author Tony Brusseau, Steve Reed
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
 *****************************************************************************/
public interface SubLNonVariableSymbol extends SubLAtomicTerm {}
