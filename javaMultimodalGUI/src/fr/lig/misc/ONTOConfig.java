/* ==========================================
 * btcore - ONTOConfig.java
 * ==========================================
 *
 * Copyright (c) 2007-2007, the University of Aberdeen
 * All rights reserved.
 *
 * are permitted FOR RESEARCH PURPOSES ONLY, provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, 
 * 		this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation and/or 
 *    other materials provided with the distribution.
 * 3. Neither the name of the University of Aberdeen nor the names of its contributors 
 * 	  may be used to endorse or promote products derived from this software without 
 *    specific prior written permission.
 *    
 *    THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 *    AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
 *    THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 *    ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE 
 *    FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 *    (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *     LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND 
 *     ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 *     (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
 *     EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *     
 *  Redistribution and use for purposes other than research requires special permission by the
 *  copyright holders and contributors. Please contact Ehud Reiter (e.reiter@abdn.ac.uk) for
 *  more information.
 */
package fr.lig.misc;

/**
 * The Class ONTOConfig is used to store any identifier to the Class or properties of the Ontology.
 * It should NOT contain anything not defined in the ontology
 * 
 * @author fportet
 */
public class ONTOConfig {

	/** The default file path to the main ontology*/ 
	public static String DEFAULT_PROTEGE_FILE = GeneralConfig.BASE_DIRECTORY + "kb/ontologies/UMLS-NICU.owl";
	
	// --------------- CLASSES ------------------
	
	// main top-level classes
	
	/** The Constant ENTITY_CLASS. All that is a thing.*/
	public static final String ENTITY_CLASS = "root:ENTITY";
	
	/** The Constant EVENT_CLASS. All that "happen" and that is observed. */
	public static final String EVENT_CLASS = "root:EVENT";
	
	/** The Constant INTERVAL_CLASS.  A wrapper to the event class that adds temporal property in it*/
	public static final String INTERVAL_CLASS = "root:INTERVAL";
	
	/** Class that define the main property of a baby ICU is interested in. It is divided into: root:BODY_SYSTEM (cardiovascular, immune, etc.) and PHYSIOLOGIC_FUNCTION_VIEWED_AS_ENTITY (growth, movement, etc)*/
	public static final String FUNCTIONAL_CONCEPT_CLASS = "root:FUNCTIONAL_CONCEPT";
	
	/** The Constant THERAPY_CLASS. What the clinicians do. */
	public static final String THERAPY_CLASS ="root:THERAPEUTIC_OR_PREVENTIVE_PROCEDURE";
	
	/** The Constant MEDICATION_CLASS. All kinds of drug*/
	public static final String MEDICATION_CLASS = "root:PHARMACOLOGIC_SUBSTANCE";
	
	
	
	/** The Constant DRUG_ADMIN_CLASS. */
	public static final String DRUG_ADMIN_CLASS = "DRUG_ADMINISTRATION";

	// NLG oriented classes (may be removed?) 
	/** The Constant INFORMATION_MESSAGE. */
	public final static String INFORMATION_MESSAGE = "INFORMATION_MESSAGE"; 

	/** The introduction message of the text*/
	public static final String SEEBABY_CLASS = "SEE_BABY";
	
	/** Channel value used for the first paragraph of the text*/
	public static final String CHANNEL_VALUE_CLASS="CHANNEL_VALUE";

	// more detailed classes 
	
	//... INTERVENTIONS
	
	/** The Constant INVESTIGATION_CLASS, superclass of investigation classes */
	public final static String INVESTIGATION_CLASS = "INVESTIGATION";   
	
	/** The Constant EXAMINE_BABY_CLASS. */
	public final static String EXAMINE_BABY_CLASS = "EXAMINE_BABY";  
	
	/** The Constant FEEDING_CLASS. */
	public static final String FEEDING_CLASS = "FEEDING";
	
	/** Class representing all nutrition given orally or via gastric tube */
	public static final String ENTERAL_FEEDING_CLASS = "ENTERAL_FEEDING";
	
	/** Class representing all nutrition given in another way that orally or via gastric tube (generally via IV line) */
	public static final String PARENTERAL_FEEDING_CLASS = "PARENTERAL_FEEDING";
	
	/** The Constant INTERVENTION_CLASS. */
	public final static String INTERVENTION_CLASS = "INTERVENTION";  

	/** the intervention to adjust any equipment setting*/
	public static final String ADJUST_EQUIPMENT = "ADJUST_EQUIPMENT";
	
	/** The INTUBATED class.*/
	public static final String INTUBATED_CLASS = "INTUBATED";
	
	/** The EXTUBATED class.*/
	public static final String EXTUBATED_CLASS = "EXTUBATED";
	
	/** The Constant START_PHOTOTHERAPY_CLASS. */
	public static final String START_PHOTOTHERAPY_CLASS = "START_PHOTOTHERAPY";
	
	/** The Constant STOP_PHOTOTHERAPY_CLASS. */
	public static final String STOP_PHOTOTHERAPY_CLASS = "STOP_PHOTOTHERAPY";

	/** The Constant ADMISSION_CLASS. */
	public static final String ADMISSION_CLASS = "ADMISSION";
	
	/** The Constant TRANSPORT_CLASS. */
	public static final String TRANSPORT_CLASS = "TRANSPORTED";
	
	/** The Constant LINE_REMOVED_CLASS*/
	public static final String LINE_REMOVED_CLASS = "LINE_REMOVED";
	
	/** The Constant LINE_INSERTED_CLASS*/
	public static final String LINE_INSERTED_CLASS = "LINE_INSERTED";

	/** The Constant COMMUNITY_CONTACT_CLASS. */
	public static final String COMMUNITY_CONTACT_CLASS ="COMMUNITY_CONTACT";
	
	public static final String SITE_CHECKED_CLASS = "SITE_CHECKS";
	
	//... OBSERVATIONS	

	/** The Constant PATIENT_WEIGHT. */
	public static final String PATIENT_WEIGHT = "BABY_WEIGHT";

	/** The Constant DELIVERY_CLASS. */
	public static final String DELIVERY_CLASS = "OBSTETRIC_DELIVERY";
	
	/** The Constant LAB_OR_TEST_RESULTS_CLASS. */
	public static final String LAB_OR_TEST_RESULTS_CLASS = "LAB_OR_TEST_RESULTS";
	
	/** The Constant LAB_OR_TEST_VALUE_CLASS. */
	public static final String LAB_OR_TEST_VALUE_CLASS = "LAB_OR_TEST_VALUE";

	/** The Constant SPIKE_CLASS*/
	public static final String SPIKE_CLASS = "SPIKE";
	
	/** The Constant STEP_CLASS*/
	public static final String STEP_CLASS = "SHIFT";
	
	/** The Constant BRADYCARDIA_CLASS*/
	public static final String BRADYCARDIA_CLASS="BRADYCARDIA";
	
	/** The Constant DESATURATION_CLASS*/
	public static final String DESATURATION_CLASS="DESATURATION";
	
	/** class subsuming bradycardia, desaturation etc */
	public static final String MEDICAL_PATTERN_CLASS = "MEDICAL_PATTERN";
	
	/** The Constant TREND_CLASS. */
	public final static String TREND_CLASS = "TREND";
	
	/** The Constant OBSERVATION_CLASS. */
	public final static String OBSERVATION_CLASS = "OBSERVATION";  
	
	/** The Constant BABY_DESCRIPTOR_CLASS, superclass of baby descriptor classes */
	public final static String BABY_DESCRIPTOR_CLASS = "BABY_DESCRIPTOR";  
	
	/** The Constant SIGNAL_ANALYSIS_CLASS, superclass of signal analysis classes */
	public final static String SIGNAL_ANALYSIS_CLASS = "DATA_ANALYSIS"; 
	
	/** The Constant NUMERICAL_DESCRIPTION_CLASS, superclass of numerical description classes */
	public final static String NUMERICAL_DESCRIPTION_CLASS ="NUMERICAL_DESCRIPTION_OVER_TIME";
	
	/** The Constant PATTERN, superclass of patterns */
	public final static String PATTERN_CLASS ="PATTERN";
	
	/** The Constant COMPLEX_EVENT, superclass of complex event classes */
	public final static String COMPLEX_EVENT_CLASS ="COMPLEX_EVENT";
	
	/** The Constant INVESTIGATION_RESULT_CLASS, superclass of investigation result classes */
	public final static String INVESTIGATION_RESULT_CLASS = "INVESTIGATION_RESULT";  	
	
	/** The Constant SEQUENCE_CLASS. */
	public final static String SEQUENCE_CLASS = "SEQUENCE"; 
	
	/** The overview concept */
	public static final String CHANNEL_OVERVIEW_CLASS = "OVERVIEW";
	
	/** The Communication overview concept */
	public static final String COMMUNICATION_CLASS = "COMMUNICATION";
	
	
	//... THINGS

	/** the root of all body systems (cardiovascular, biliary etc.) */
	public static final String BODY_SYSTEM_CLASS = "root:BODY_SYSTEM";
	
	/** the root of all physiological function (thermoregulation, etc.) */
	public static final String PHYSIOLOGIC_FUNCTION_CLASS = "PHYSIOLOGIC_FUNCTION_VIEWED_AS_ENTITY";
	
	/** the infection class */
	public static final String INFECTION_CLASS = "INFECTION";
	
	
	/** The Constant FOOD_CLASS. */
	public static final String FOOD_CLASS = "root:FOOD";
	
	/** The Constant PATIENT_CLASS. */
	public static final String PATIENT_CLASS = "PATIENT";

	/** The Constant PATIENT_MOTHER_CLASS. */
	public static final String PATIENT_MOTHER_CLASS = "MOTHER";

	/** The Constant DOCUMENT_CLASS. */
	public static final String DOCUMENT_CLASS="DOCUMENT";
	
	/** The Constant NOTES_CLASS. */
	public static final String NOTES_CLASS = "NOTES";
	
	/** The Constant DELIVERY_NOTES_CLASS. */
	public static final String DELIVERY_NOTES_CLASS = "DELIVERY_NOTES";
	
	/** The Constant COMMENT_CLASS. */
	public static final String COMMENT_CLASS = "COMMENT";
	
	/** The Constant TEMPERATURE_CHANNEL_CLASS*/
	public static final String TEMPERATURE_CHANNEL_CLASS="TEMPERATURE_CHANNEL";
	
	/** The Constant EQUIPMENT_SETTING_CLASS. */
	public final static String EQUIPMENT_SETTING_CLASS = "EQUIPMENT_SETTING"; //
	
	/** The Constant PERIPHERAL_IV_LINE_CLASS. */
	public static final String PERIPHERAL_IV_LINE_CLASS = " PERIPHERAL_IV_LINE";
	
	/** The Constant LONG_LINE_CLASS. */
	public static final String LONG_LINE_CLASS = "PERIPHERALLY_INSERTED_CENTRAL_CATHETER";
	
	public static final String UMBILICAL_VENOUS_CATHETER_CLASS = "UMBILICAL_VENOUS_CATHETER";
	
	public static final String URINARY_CATHETER_CLASS = "URINARY_CATHETER";
	
	public static final String PERIPHERAL_ARTERIAL_LINE_CLASS = "PERIPHERAL_ARTERIAL_LINE";

	public static final String UMBILICAL_ARTERIAL_CATHETER_CLASS = "UMBILICAL_ARTERIAL_CATHETER";
	
	public static final String LAB_OR_TEST_VALUE = "LAB_OR_TEST_VALUE";
	
	/** The Constant HR_CLASS. */
	public static final String HR_CLASS = "HR"; // heart rate
	
	/** The Constant BM_CLASS. */
	public static final String BM_CLASS = "BM"; // mean blood pressure
	
	/** The Constant OX_CLASS. */
	public static final String OX_CLASS = "OX"; // trans oxygen
	
	/** The Constant CO_CLASS. */
	public static final String CO_CLASS = "CO"; // trans CO2
	
	/** The Constant SO_CLASS. */
	public static final String SO_CLASS = "SO"; // oxygen saturation
	
	/** The Constant TC_CLASS. */
	public static final String TC_CLASS = "TC"; // core temp
	
	/** The Constant TP_CLASS. */
	public static final String TP_CLASS = "TP"; // peripheral temp
	
	/** The Constant TD_CLASS. */
	public static final String TD_CLASS = "TD"; // core/per temp diff
	
	// --------------- HEADINGS --------------------
	// THESE CLASSES ARE PARTICULARLY USED TO FIND THE RELATION OF ANY INSTANCES TO A "SYSTEM" CONCEPT 
	
	public static final String RESPIRATORY_SYSTEM = "LUNGS_AND_RESPIRATORY_SYSTEM";
	
	public static final String CARDIOVASCULAR_SYSTEM = "CARDIOVASCULAR_SYSTEM";
	
	public static final String HAEMATOLOGICAL_SYSTEM = "HAEMATOLOGICAL_SYSTEM";
	
	public static final String THERMOREGULATION = "THERMOREGULATION";
	
	public static final String RESPIRATION = "RESPIRATION";
	
	public static final String NUTRITION = "NUTRITION";
	
	public static final String ELIMINATION = "ELIMINATION";
	
	
	
	// --------------- ATTRIBUTES/PROPERTIES ------------------
	
	
	//... general attributes
	/** The Constant EARLY_START_TIME_FEATURE. */
	public static final String EARLY_START_TIME_FEATURE = "root:early_start";
	
	/** The Constant EARLY_END_TIME_FEATURE. */
	public static final String EARLY_END_TIME_FEATURE = "root:early_end";
	
	/** The Constant LATE_START_TIME_FEATURE. */
	public static final String LATE_START_TIME_FEATURE = "root:late_start";
	
	/** The Constant LATE_END_TIME_FEATURE. */
	public static final String LATE_END_TIME_FEATURE = "root:late_end";
	
	/** problem during an activity generally free-text*/
	public static final String PROBLEM_FEATURE = "problems";
	
	/** date of entry of the instance in the database*/
	public static final String ENTRY_DATE_FEATURE = "date_of_entry";	

	/** The Constant NAME_FEATURE. */
	public static final String NAME_FEATURE = "name";
	
	/** 
	 * For abbreviations of proper names
	 */
	public static final String ABBREV_NAME_FEATURE = "abbreviated_name";
	
	/** The Constant NUMERICAL_VALUE_FEATURE. As in the database  */
	public static final String NUMERICAL_VALUE_FEATURE = "numerical_value";
	
	/** The Constant STRING_VALUE_FEATURE. As in the database*/
	public static final String STRING_VALUE_FEATURE = "string_value";
	
	/** The Constant IMPORTANCE_FEATURE. used for document planning and data filtering*/
	public static final String IMPORTANCE_FEATURE = "importance";
	
	/** The Constant DEFAULT_IMPORTANCE_FEATURE. annotation attached to most class and defined by expertise*/
	public static final String DEFAULT_IMPORTANCE_FEATURE = "default_importance";
	
	/** information used particularly for equipment setting. How important is to report change in value.*/
	public static final String DEFAULT_IMPORTANT_VALUE_CHANGE_FEATURE = "default_importance_value_change";

	
	
	//... attribute of complex event to ease the mapping between instance and linguistic representation 
	/** The Constant TYPE_OF_THING_FEATURE. E.g. medication, */
	public static final String DONE_WITH_FEATURE = "done_with";
	
	/** The thing to which a action is performed*/
	public static final String DONE_TO_FEATURE = "done_to";
	
	/** The person(s) who did the action/ made the observation. */
	public static final String DONE_BY_FEATURE = "done_by";

	/** where the action has been performed*/
	public static final String LOCATION_FEATURE = "location";
	
	/** The Constant ROUTE_FEATURE. E.g. food by NGT, drug administered intravenously etc.*/
	public final static String ROUTE_FEATURE = "route";
	
	/** the type of the entity concerned by the action*/
	public static final String TYPE_OF_NAME_FEATURE = "type_of_name";
	
	/** the manner of the action*/ 
	public static final String TYPE_OF_DOSE_FEATURE = "type_of_injection";
	
	/** the reason for this action or non action (generally free-text or choice list)*/
	public static final String REASON_FEATURE = "reason";
	
	/** the frequency (or rate) at which the activity is performed (e.g. suction frequency)*/
	public static final String FREQUENCY_FEATURE = "frequency";
	
	/**the quantity of the concerned entity which is involved in the action/observation*/
	public static final String QUANTITY_FEATURE = "quantity";
	
	/** unit in which the quantity/value is expressed*/
	public static final String UNIT_FEATURE = "unit";

	/** The number of time the action has been try.*/
	public static final String NUMBER_OF_ATTEMPTS_FEATURE = "number_of_attempts";
	
	/** true if it is a success, false otherwise*/
	public static final String SUCCESS_FEATURE = "success";

	/** The Constant FOOD_FEATURE. */
	public static final String FOOD_FEATURE = "food";

	//... Patient features
	
	/** The Constant PATIENT_ID_FEATURE. */
	public static final String PATIENT_ID_FEATURE = "id";
	
	/** The Constant PATIENT_GENDER_FEATURE. */
	public static final String PATIENT_GENDER_FEATURE = "gender";
	
	/** The Constant PATIENT_DOB_FEATURE. */
	public static final String PATIENT_DOB_FEATURE = "date_of_birth";

	/** The Constant PATIENT_GESTATION_FEATURE. */
	public static final String PATIENT_GESTATION_FEATURE = "gestation";
	
	/** The Constant PATIENT_BIRTHWEIGHT_FEATURE. */
	public static final String PATIENT_BIRTHWEIGHT_FEATURE = "weight_at_birth";

	/** The Constant FETUS_POSITION_FEATURE. */
	public static final String FOETUS_POSITION_FEATURE = "foetus_position";
	
	//... parent features
	
	/** The Constant OCCUPATION_FEATURE. */
	public static final String OCCUPATION_FEATURE = "occupation";
	
	/** The Constant MARITAL_STATUS_FEATURE. */
	public static final String MARITAL_STATUS_FEATURE = "marital_status";
	
	/** The Constant BLOOD_GROUP_FEATURE. */
	public static final String BLOOD_GROUP_FEATURE = "blood_group";
	
	public static final String RELIGION_FEATURE = "religion";

	public static final String ETHNIC_FEATURE = "ethnic";

	/** The Constant DELIVERY_STATUS_FEATURE. */
	public static final String DELIVERY_STATUS_FEATURE = "delivery_status";
	
	/** The Constant LIST_LAB_VALUES_FEATURE. */
	public static final String LIST_LAB_VALUES_FEATURE = "object_values";

	/** The Constant PEAK_DATE_FEATURE. */
	public final static String PEAK_DATE_FEATURE = "peak_date";
	
	/** The Constant PEAK_VALUE_FEATURE. */
	public final static String PEAK_VALUE_FEATURE = "peak_value";
	
	/** The Constant END_VALUE_FEATURE. */
	public final static String END_VALUE_FEATURE = "end_value";
	
	/** The Constant START_VALUE_FEATURE. */
	public final static String START_VALUE_FEATURE = "start_value";
	
	/** The Constant MEAN_FEATURE. */
	public final static String MEAN_FEATURE = "average";
	
	/** The Constant STD_FEATURE. */
	public final static String STD_FEATURE = "standard_deviation";
	
	
	/** The Constant SHORT_NAME_FEATURE. */
	public final static String SHORT_NAME_FEATURE = "abbreviated_name";
	
	/** The Constant CLASS_FEATURE. */
	public final static String MAGNITUDE_FEATURE = "magnitude";
	
	/** The Constant SPEED_FEATURE. */
	public final static String SPEED_FEATURE = "speed";
	
	public static final String PATTERN_DIRECTION_FEATURE = "direction";
	
	/** The Constant CHANNEL_FEATURE. */
	public final static String CHANNEL_FEATURE = "source";
	
	/** The Constant TREND_DURATION_FEATURE. */
	public final static String DURATION_FEATURE = "duration";
	
	/** The Constant TREND_DURATION_VALUE_FEATURE. */
	public final static String TREND_DURATION_VALUE_FEATURE = "duration_value";
	
	/** The type of object in a sequence*/
	public final static String SEQUENCE_TYPE_FEATURE = "NAME";
	
	/** The instances making up the sequence, if any */
	public final static String SEQUENCE_ELEMENT_FEATURE = "abstracts";
	
	/** The Constant LINK_EVENT_LIST_FEATURE. */
	public final static String LINK_EVENT_LIST_FEATURE = "abstracts";
	
	/** The Constant LINK_TYPE_FEATURE. */
	public final static String LINK_TYPE_FEATURE = "link_type";	
	
	/** The lowest legal value (i.e. anything below this is false). */
	public final static String LOW_LEGAL_RANGE_FEATURE = "lowest_legal_value";
	/** The highest legal value (i.e. anything above this is false). */
	public final static String HIGH_LEGAL_RANGE_FEATURE = "highest_legal_value";
	
	/** The lowest physiological value (i.e. anything below this is highly abnormal). */
	public final static String LOW_PHYSIOLOGICAL_RANGE_FEATURE = "lowest_physiological_value";
	
	/** The highest physiological value(i.e. anything above this is highly abnormal). */
	public final static String HIGH_PHYSIOLOGICAL_RANGE_FEATURE = "highest_physiological_value";
	
	/** The lowest normal value (i.e. anything below this is abnormal). */
	public final static String LOW_NORMAL_RANGE_FEATURE = "lowest_normal_value";
	
	/** The highest normal value(i.e. anything above this is abnormal). */
	public final static String HIGH_NORMAL_RANGE_FEATURE = "highest_normal_value";

	/** The typical value according to the baby. */
	public final static String TYPICAL_VALUE_FEATURE = "typical_value";
	
	// --------------- RELATIONS ------------------
	
	/** The Constant ASSOCIATED_WITH_RELATION. */
	public static final String ASSOCIATED_WITH_RELATION = "root:associated_with";

	public static final String FUNCTIONALLY_RELATED_TO = "root:functionally_related_to";
	
	/** The Constant EVALUATION_OF_RELATION. */
	public static final String EVALUATION_OF_RELATION = "root:evaluation_of";
	
	/** The Constant OCCURS_IN_RELATION. */
	public static final String OCCURS_IN_RELATION = "root:occurs_in";

	/** The Constant CAUSE_RELATION. */
	public static final String CAUSE_RELATION = "root:causes";
	
	/** The Constant CO_OCCURS_WITH_RELATION. */
	public static final String CO_OCCURS_WITH_RELATION = "root:co-occurs_with";
	
	/** The Constant PROCEDURE_PART_OF_RELATION. */
	public static final String PROCEDURE_PART_OF_RELATION = "root:conceptual_part_of";

	
	/** The Constant INCLUDE_LINK_VALUE. */
	public final static String INCLUDE_LINK_VALUE = "include";
	
	/** The Constant CAUSE_LINK_VALUE. */
	public final static String CAUSE_LINK_VALUE = "cause";
	
	/** The Constant ASSOCIATE_LINK_VALUE. */
	public final static String ASSOCIATE_LINK_VALUE = "associate";

	
	// --------------- VALUES ------------------
	
	public static final String UPWARD_VALUE_FEATURE = "increasing";
	
	public static final String DOWNWARD_VALUE_FEATURE = "decreasing";
	
	/** The Constant STEADY_VALUE_FEATURE. */
	public final static String STEADY_VALUE_FEATURE = "steady";
	
	/** The Constant STEADY_VALUE_FEATURE. */
	public final static String WANDERING_VALUE_FEATURE = "wandering";
	
	

	public static final String LOW_SPEED_VALUE = "slow";

	public final static String MEDIUM_SPEED_VALUE = "medium";
	
	public static final String HIGH_SPEED_VALUE = "quick";
	
	public static final String VERY_HIGH_SPEED_VALUE = "sudden";
	
	

	public static final String LOW_MAGNITUDE_VALUE_AS_SIZE = "small";

	public static final String LOW_MAGNITUDE_VALUE_AS_AMPLITUDE = "low";

	public final static String MEDIUM_MAGNITUDE_VALUE = "medium";
	
	public static final String HIGH_MAGNITUDE_VALUE_AS_AMPLITUDE = "high";
	
	public static final String HIGH_MAGNITUDE_VALUE_AS_SIZE = "big";
	
	
	
	public static final String VERY_LOW_DURATION_VALUE = "very short";
	
	public static final String LOW_DURATION_VALUE = "short";

	public final static String MEDIUM_DURATION_VALUE = "medium";
	
	public static final String HIGH_DURATION_VALUE = "long";
	
	public static final String VERY_HIGH_DURATION_VALUE = "very long";
	
	
	// --------------- RDFS ANNOTATIONS ------------------
	
	public static final String BADGER_CODE_FEATURE = "badger_code";

}
