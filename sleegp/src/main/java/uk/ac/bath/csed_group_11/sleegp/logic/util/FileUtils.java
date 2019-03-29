package uk.ac.bath.csed_group_11.sleegp.logic.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileUtils {

    public static List<Double> convertFromFile(String path) throws FileNotFoundException {

        List<Double> list = new ArrayList<Double>();

        File file = new File(path);
        Scanner sc = new Scanner(file);

        while (sc.hasNextLine())
        {

            try{ list.add(Double.parseDouble(sc.nextLine()));

            } catch(Exception e){}
        }

        return list;

    }

    public static List<String> extractCSV(String path) throws FileNotFoundException {

        List<String> list = new ArrayList<String>();

        File file = new File(path);
        Scanner sc = new Scanner(file);

        while (sc.hasNextLine())
        {

            try{ list.add(sc.nextLine());

            } catch(Exception e){}
        }

        return list;

    }
}
