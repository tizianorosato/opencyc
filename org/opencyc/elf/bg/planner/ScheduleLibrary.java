package org.opencyc.elf.bg.planner;

//// Internal Imports

//// External Imports
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <P>
 * ScheduleLibrary provides the timing specification for a plan given the job to
 * perform.  There is a singleton instance.
 * </p>
 * 
 * @version $Id$
 * @author Stephen L. Reed  
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
public class ScheduleLibrary {
  
  //// Constructors
  
  /** Creates a new instance of ScheduleLibrary. */
  public ScheduleLibrary() {
    scheduleLibrary = this;
  }
  
  //// Public Area
  
  /**
   * Gets the singleton schedule library instance.
   *
   * @return the singleton schedule library instance
   */
  public static ScheduleLibrary getInstance () {
    return scheduleLibrary;
  }
  
  /**
   * Initializes the schedule library.
   */
  public void initialize() {
    //TODO
  }
  
  /**
   * Gets the list of schedules that accomplish the given action name.
   *
   * @param action the given action name
   * @return the list of schedules that accomplish the given action name
   */
  public ArrayList getSchedules (String actionName) {
    return (ArrayList) scheduleDictionary.get(actionName);
  }
  
  //// Protected Area
   
  //// Private Area
  
  //// Internal Rep
  
  /**
   * the dictionary that associates a given action name with the list of schedules that
   * accomplish it
   */
  protected HashMap scheduleDictionary = new HashMap();
  
  /**
   * the singleton schedule library instance
   */
  protected static ScheduleLibrary scheduleLibrary;
  
  //// Main
  
}