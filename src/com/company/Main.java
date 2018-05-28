package com.company;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final int PARAMETERS = 9;
    private static String file = "pima-indians-diabetes.data.csv";
    private static String fileResult_1 = "result_1.csv";
    private static String fileResult_2 = "result_2.csv";
    private static List<Double[]> healthful = new ArrayList<>();
    private static List<Double[]> healthfulWithoutEmptiness = new ArrayList<>();
    private static List<Double[]> diseased = new ArrayList<>();
    private static List<Double[]> diseasedWithoutEmptiness = new ArrayList<>();
    private static double[] averageValuesHealthful = new double[PARAMETERS];
    private static double[] averageValuesDiseased = new double[PARAMETERS];

    public static void main(String[] args) {
        makePatientLists(file);
        averageValuesHealthful = makeAverageValues(healthful);
        averageValuesDiseased = makeAverageValues(diseased);
        healthfulWithoutEmptiness = listWithoutEmptiness(healthful, averageValuesHealthful);
        diseasedWithoutEmptiness = listWithoutEmptiness(diseased, averageValuesDiseased);
        writeFilesWithPatientLists(healthfulWithoutEmptiness, diseasedWithoutEmptiness);
    }

    private static void makePatientLists(String filename) {
        try (Scanner inputStream = new Scanner(new File(filename));) {
            while (inputStream.hasNext()) {
                String data = inputStream.next();
                String[] values = data.split(",");
                Double[] tmpArray = new Double[values.length];
                for (int i = 0; i < values.length; i++) {
                    tmpArray[i] = Double.parseDouble(values[i]);
                }
                if (tmpArray[tmpArray.length - 1] == 1) {
                    diseased.add(tmpArray);
                } else if (tmpArray[tmpArray.length - 1] == 0) {
                    healthful.add(tmpArray);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("ERROR! File not found!");
        }
    }

    private static double[] makeAverageValues(List<Double[]> arrayOfValues) {
        double[] result = new double[PARAMETERS];
        for (int i = 0; i < PARAMETERS; i++) {
            double tmpSum = 0;
            int tmpCount = arrayOfValues.size();
            for (int j = 0; j < arrayOfValues.size(); j++) {
                tmpSum += arrayOfValues.get(j)[i];
                if (arrayOfValues.get(j)[i] == 0) {
                    tmpCount--;
                }
            }
            if (tmpCount == 0) {
                result[i] = 0;
            } else {
                result[i] = tmpSum / tmpCount;
            }
        }
        return result;
    }

    private static List<Double[]> listWithoutEmptiness(List<Double[]> listOfArrays, double[] averageValues) {
        List<Double[]> result = new ArrayList<>();
        for (int i = 0; i < listOfArrays.size(); i++) {
            for (int j = 0; j < listOfArrays.get(i).length; j++) {
                if (listOfArrays.get(i)[j] == 0) {
                    listOfArrays.get(i)[j] = averageValues[j];
                }
            }
            result.add(listOfArrays.get(i));
        }
        return result;
    }

    private static void writeFilesWithPatientLists(List<Double[]> healthfulPatients, List<Double[]> diseasedPatients) {
        int quantityOfPatients = healthfulPatients.size() + diseasedPatients.size();
        int quantityForFirstFile = (int) (quantityOfPatients * 0.8);
        int coefficientOfHealthful = healthfulPatients.size() * 100 / quantityOfPatients;
        int quantityHealthfulForFirstFile = quantityForFirstFile * coefficientOfHealthful / 100;
        int quantityDiseasedForFirstFile = quantityForFirstFile - quantityHealthfulForFirstFile;

        List<Double[]> patientsForFirstFile = new ArrayList<>();
        List<Double[]> patientsForSecondFile = new ArrayList<>();

        Collections.shuffle(healthfulPatients);
        for (int i = 0; i < healthfulPatients.size(); i++) {
            if (i < quantityHealthfulForFirstFile) {
                patientsForFirstFile.add(healthfulPatients.get(i));
            } else {
                patientsForSecondFile.add(healthfulPatients.get(i));
            }
        }

        Collections.shuffle(diseasedPatients);
        for (int i = 0; i < diseasedPatients.size(); i++) {
            if (i < quantityDiseasedForFirstFile) {
                patientsForFirstFile.add(diseasedPatients.get(i));
            } else {
                patientsForSecondFile.add(diseasedPatients.get(i));
            }
        }

        //Collections.shuffle(patientsForFirstFile);
        //Collections.shuffle(patientsForSecondFile);

        try(FileWriter writer = new FileWriter(fileResult_1);) {
            for (int i = 0; i < patientsForFirstFile.size(); i++) {
                StringBuilder tmpLine = new StringBuilder();
                for (int j = 0; j < patientsForFirstFile.get(i).length; j++) {
                    String tmpValue = patientsForFirstFile.get(i)[j].toString();
                    if(j == 5 || j == 6) {
                        tmpLine.append(tmpValue.substring(0, tmpValue.indexOf(".") + 2)).append(",");
                    }
                    if(j == 6) {
                            tmpLine.append(tmpValue).append(",");
                    }
                    tmpLine.append(tmpValue.substring(0, tmpValue.indexOf("."))).append(",");
                }
                writer.write(String.valueOf(tmpLine.substring(0, tmpLine.length() - 1)));
                writer.write("\n");
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try(FileWriter writer = new FileWriter(fileResult_2);) {
            for (int i = 0; i < patientsForSecondFile.size(); i++) {
                StringBuilder tmpLine = new StringBuilder();
                for (int j = 0; j < patientsForSecondFile.get(i).length; j++) {
                    String tmpValue = patientsForSecondFile.get(i)[j].toString();
                    if(j == 5 || j == 6) {
                        tmpLine.append(tmpValue.substring(0, tmpValue.indexOf(".") + 2)).append(",");
                    }
                    if(j == 6) {
                        tmpLine.append(tmpValue).append(",");
                    }
                    tmpLine.append(tmpValue.substring(0, tmpValue.indexOf("."))).append(",");
                }
                writer.write(String.valueOf(tmpLine.substring(0, tmpLine.length() - 1)));
                writer.write("\n");
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}