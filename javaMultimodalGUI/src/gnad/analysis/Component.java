package gnad.analysis;

import java.util.ArrayList;

/**
 * 
 * @author nguyentk
 *
 */

public class Component {
	
	/** name of the component **/
	private String name;
	/** the carac of component **/
	private  Carac carac;
	/** the chi of component **/
	private Chi chi;
	/** the filtered heart rate signal **/
	private double[] hr;
	/** the filtered breath rate signal **/
	private double[] br;
	
	public Component(double[] hr, double[] br){
		this.hr = hr;
		this.br = br;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return this.name;
	}
	
	public void setCarac(Carac ca){
		this.carac = ca;
	}
	
	public Carac getCarac(){
		return this.carac;
	}
	
	public void setChi(Chi ch){
		this.chi = ch;
	}
	
	public Chi getChi(){
		return this.chi;
	}
	
	public double computeCValue(){
		double cValueHR = 0;
		double cValueBR = 0;
		double cValue[] = new double[6];
		double cValueHR_E = 0, cValueHR_Ex, cValueHR_Di, cValueHR_Re, cValueHR_Ba, cValueHR_Dr = 0;
		double cValueBR_E = 0, cValueBR_Ex, cValueBR_Di, cValueBR_Re, cValueBR_Ba, cValueBR_Dr  = 0;
		//double cValue_E, cValue_Ex, cValue_Di, cValue_Re, cValue_Ba, cValue_Dr  = 0;
		if(name == "hr"){
			double mean = 0;
			double arg1 = 0;
			double arg2 = 0;
			if(carac.getName().matches("average")){
				Object[] win = carac.getWindow();
				int previousWin = (Integer) win[0];
				int currentWin = (Integer) win[1];
				mean = computeMean(hr,previousWin, currentWin);
			}
			else if(carac.getName().matches("slope")){
				Object[] win = carac.getWindow();
				int previousWin = (Integer) win[0];
				int currentWin = (Integer) win[1];
				mean = computeSlope(hr, previousWin, currentWin);
			}
			if(chi.getName().matches("gaussian")){
				arg1 = chi.getArg1();
				arg2 = chi.getArg2();
			}
			cValueHR = Math.exp(-Math.pow((mean - arg1), 2)/arg2);
			/*cValueHR_E = Math.exp(-Math.pow((mean - 95), 2)/50);
			cValueHR.add(cValueHR_E);
			cValueHR_Ex = Math.exp(-Math.pow((mean - 110), 2)/60);
			cValueHR.add(cValueHR_Ex);
			cValueHR_Di = Math.exp(-Math.pow((mean - 100), 2)/50);
			cValueHR.add(cValueHR_Di);
			cValueHR_Dr = Math.exp(-Math.pow((mean - 75), 2)/40);
			cValueHR.add(cValueHR_Dr);
			cValueHR_Ba = Math.exp(-Math.pow((mean - 78), 2)/50);
			cValueHR.add(cValueHR_Ba);
			cValueHR_Re = Math.exp(-Math.pow((mean + 4.7), 2)/2);
			cValueHR.add(cValueHR_Re);*/
			
		}
		else{
			double mean = 0;
			double arg1 = 0;
			double arg2 = 0;
			if(carac.getName().matches("average")){
				Object[] win = carac.getWindow();
				int previousWin = (Integer) win[0];
				int currentWin = (Integer) win[1];
				mean = computeMean(br,previousWin,currentWin);
			}
			else if(carac.getName().matches("slope")){
				Object[] win = carac.getWindow();
				int previousWin = (Integer) win[0];
				int currentWin = (Integer) win[1];
				mean = computeSlope(br, previousWin, currentWin);
			}
			if(chi.getName().matches("gaussian")){
				arg1 = chi.getArg1();
				//System.out.println(arg1);
				arg2 = chi.getArg2();
				//System.out.println(arg2);
			}
			cValueBR = Math.exp(-Math.pow((mean - arg1), 2)/arg2);
			/*cValueBR_E = Math.exp(-Math.pow((mean - 16), 2)/25);
			cValueBR.add(cValueBR_E);
			cValueBR_Ex = Math.exp(-Math.pow((mean - 20), 2)/40);
			cValueBR.add(cValueBR_Ex);
			cValueBR_Di = Math.exp(-Math.pow((mean - 10), 2)/40);
			cValueBR.add(cValueBR_Di);
			cValueBR_Dr = Math.exp(-Math.pow((mean - 9), 2)/20);
			cValueBR.add(cValueBR_Dr);
			cValueBR_Ba = Math.exp(-Math.pow((mean - 10), 2)/35);
			cValueBR.add(cValueBR_Ba);
			cValueBR_Re = Math.exp(-Math.pow((mean + 3.4), 2)/2);
			cValueBR.add(cValueBR_Re);*/
		}
		/*for(int j = 0; j < cValueHR.size(); j++){
			cValue[j] = cValueBR.get(j) + cValueHR.get(j);
			System.out.println("index: " + cValue[j]);
		}
		double max = cValue[0];
		int index = 0;
		for(int l = 1; l <cValue.length; l++){
				if(cValue[l]> max){
					max = cValue[l];
					index = l;
				}
		}
		System.out.println("index: " + index);
		String state = null;
		switch(index){
		case 0: state = "Exercise"; break;
		case 1: state = "Exertion"; break;
		case 2: state = "Digestion"; break;
		case 3: state = "Drowsiness"; break;
		case 4: state = "Basal"; break;
		case 5: state = "Recovery"; break;
		}*/
		return cValueHR + cValueBR;
	}
	
	public double computeMean(double[] array, int previousWin, int currentWin){
		double mean = 0;
		if(currentWin < array.length){
			for(int i = previousWin; i < currentWin; i++){
				mean = mean + array[i];
			}
			mean = mean/(currentWin - previousWin);
		}
		return mean;
	}
	
	public double computeSlope(double[] array, int previousWin, int currentWin){
		double slope = 0;
		if(currentWin < array.length){
			double min = findMin(array,previousWin,currentWin);
			double max = findMax(array,previousWin,currentWin);
			slope = (max - min)/(currentWin - previousWin);
		}
		return slope;
	}
	
	public double findMin(double[] array, int previousWin, int currentWin){
		double min = array[previousWin];
		for(int i = previousWin; i < currentWin; i++){
			if(array[i] < min){
				min = array[i];
			}
		}
		return min;
	}
	public double findMax(double[] array, int previousWin, int currentWin){
		double max = array[previousWin];
		for(int i = previousWin; i < currentWin; i++){
			if(array[i] > max){
				max = array[i];
			}
		}
		return max;
	}
}
