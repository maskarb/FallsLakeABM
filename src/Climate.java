// import java.io.BufferedReader;
// import java.io.FileReader;
// import java.io.IOException;
// import java.util.ArrayList;
// import java.util.Scanner;

import pertubing.DataList;

/** */

/** @author amashha */
public class Climate {

  // private static ArrayList<Double> precipitation;
  // private static ArrayList<Double> evapotrans;

  /* new code perturbing */
  private DataList precipitation;
  private DataList evapotrans;

  // private ArrayList<Double> temperature;

  // public Climate(ArrayList<ArrayList<Double>> list) {
  //
  // precipitation = list.get(0);
  // evapotrans = list.get(1);
  //
  // }

  // public static void readClimateData(String fileName) {
  //
  //// ArrayList<ArrayList<Double>> list = new ArrayList<>();
  // ArrayList<Double> precipList = new ArrayList<>();
  // ArrayList<Double> evapoList = new ArrayList<>();
  //
  // Scanner s = null;
  // try {
  // s = new Scanner(new BufferedReader(new FileReader(fileName)));
  // while (s.hasNext()) {
  // String comment = s.nextLine();
  // String[] splitedCmt = comment.split("\\s+");
  //
  // precipList.add(Double.parseDouble(splitedCmt[0]));
  // evapoList.add(Double.parseDouble(splitedCmt[1]));
  //
  // }
  //
  //// list.add(precipList);
  //// list.add(evapoList);
  // precipitation = precipList;
  // evapotrans = evapoList;
  //
  //
  // } catch (IOException e) {
  // e.printStackTrace();
  // } finally {
  // if (s != null) {
  // s.close();
  // }
  // }
  //// return list;
  //
  // }
  //
  // public static double getPrecip(int time){
  // return precipitation.get(time);
  // }
  //
  // public static double getEvapotrans(int time){
  // return evapotrans.get(time);
  // }

  /* new code perturbing */
  public void setPrecipitation(DataList prec) {
    precipitation = prec;
  }

  public void setEvapotrans(DataList evapo) {
    evapotrans = evapo;
  }

  public double getPrecip(int time) {
    return precipitation.value(time);
  }

  public double getEvapotrans(int time) {
    return evapotrans.value(time);
  }
}
