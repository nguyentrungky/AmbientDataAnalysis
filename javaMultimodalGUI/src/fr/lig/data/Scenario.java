package fr.lig.data;

import java.util.Date;

/**
 * Class used as a data structure to represent scenario defined by their patient id and their period. 
 * Optionally a scenario identifier can be used
 * 
 * 
 * @author fportet
 */
public class Scenario {

		/** the patient identifier*/
		private int patientID;
		/** start time of the scenario*/
		private Date startTime;
		/** end time of the scenario*/
		private Date endTime;
		/** the scenario identifier */
		private int scenarioNumber;
		
		//the description of a scenario, to be set by user
		private String scenarioDescription;
		
		/**
		 * create a scenario with a specific id 
		 * @param patientID - the patient identifier 
		 * @param startTime - start date and time of the scenario
		 * @param endTime - end date and time of the scenario
		 * @param id - the identifier of the scenario
		 */
		public Scenario(int patientID, Date startTime, Date endTime, int id) {
			scenarioNumber =id;
			this.patientID = patientID;
			this.startTime = startTime;
			this.endTime = endTime;
		}

		/**
		 * create a scenario 
		 * @param patientID - the patient identifier 
		 * @param startTime - start date and time of the scenario
		 * @param endTime - end date and time of the scenario
		 */
		public Scenario(int patientID, Date startTime, Date endTime) {
			scenarioNumber =-1;
			this.patientID = patientID;
			this.startTime = startTime;
			this.endTime = endTime;
		}
		
		
		//... getters and Setters				
		
		public int getScenarioNumber() {
			return scenarioNumber;
		}

		public void setScenarioNumber(int scenarioNumber) {
			this.scenarioNumber = scenarioNumber;
		}

		public int getPatientID() {
			return this.patientID;
		}

		public void setPatientID(int patientID) {
			this.patientID = patientID;
		}

		public Date getStartTime() {
			return startTime;
		}

		public void setStartTime(Date startTime) {
			this.startTime = startTime;
		}

		public Date getEndTime() {
			return endTime;
		}

		public void setEndTime(Date endTime) {
			this.endTime = endTime;
		}

		public String getScenarioDescription() {
			return scenarioDescription;
		}

		public void setScenarioDescription(String scenarioDescription) {
			this.scenarioDescription = scenarioDescription;
		}
}
