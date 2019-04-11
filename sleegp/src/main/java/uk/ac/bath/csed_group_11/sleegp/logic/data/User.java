package uk.ac.bath.csed_group_11.sleegp.logic.data;

import uk.ac.bath.csed_group_11.sleegp.cli.SleegpConstants;
import uk.ac.bath.csed_group_11.sleegp.gui.Utilities.Resource;
import uk.ac.bath.csed_group_11.sleegp.logic.util.FileTools;
import uk.ac.bath.csed_group_11.sleegp.logic.util.Load;
import uk.ac.bath.csed_group_11.sleegp.logic.util.ObjectConverter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User extends ArrayList<DataCouple> implements Serializable {
    /**
     * This is serializable ID, set to static so that there is not an ID mis-match error when saving files
     */
    static final long serialVersionUID = -2183068004576095519L;

    //these integers set up the point system

    /**
     * Total amount of points
     */
    private int points = 0;
    //private int expPoints = (1000*level+15)%level;

    transient static final int POINTS_PER_LEVEL = 1500;
    transient static final int POINTS_FOR_REACHING_GOAL = 3000;


    /*
        Hours per week
     */
    private double currentGoal = 0;

    public static String EXTENSION = ".usr";

    public boolean saveToFile(File firectory) throws FileNotFoundException {
        return FileTools.saveObject(this, firectory, EXTENSION);
    }

    public void appendHourlyPoints(double sleptHours){
        points += (sleptHours/currentGoal) *POINTS_PER_LEVEL;
    }


    public int returnPoints(){
        return points;
    }

    public int returnLvl(){
        return points / POINTS_PER_LEVEL;
    }

    public double getPercentageProgress(){
        return (points/(POINTS_PER_LEVEL *1.0)) - (points/POINTS_PER_LEVEL);
    }

    public static User generateUserDB(){
        User user = new User();
        try {
            user.saveToFile(new File(SleegpConstants.RELATIVE_USER_FILE));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return user;
    }

    public static User loadDefaultUser() throws IOException, ClassNotFoundException {
        User loaded = null;
         try{loaded = User.loadUserFromFile(new File(SleegpConstants.RELATIVE_USER_FILE)); } catch(IOException e){loaded = null;}

        if(loaded ==null) return generateUserDB();

        return loaded;
    }

    public static User loadUserFromFile(File file) throws IOException,
        ClassNotFoundException {
        Load loader = new Load(file);
        Object loaded = loader.load();

        return (User)loaded;
    }

    public double getCurrentGoal() {
        return currentGoal;
    }

    public void setCurrentGoal(double currentGoal) {
        this.currentGoal = currentGoal;
    }

    //Temporary script for populating the User with new sleep data
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        User u = User.loadDefaultUser();
        EpochContainer ec = EpochContainer.loadContainerFromFile(new File("/Users/mathew" +
            "/Documents/GitHub/project/resources/test-data/3 Hour (Fixed).ec"));

        u.add(new DataCouple(ec));

        u.saveToFile(new File(SleegpConstants.RELATIVE_USER_FILE));

        System.out.println("[Process Complete]");

    }
}
