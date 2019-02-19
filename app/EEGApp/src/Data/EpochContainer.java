/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Mathew Allington
 */
public class EpochContainer {
    private List<Epoch> data;
    
    /**
     * Creates an empty container instance
     */
    public EpochContainer(){
        data = new ArrayList<Epoch>();
    }
    
    /**
     *
     * @param file
     */
    public EpochContainer(File file){
        
    }
    
    /**
     *
     * @return
     */
    public int size(){
        return data.size();
    }
    
    /**
     *
     * @param id
     * @return
     */
    public Epoch getEpoch(int id){
        return data.get(id);
    }
    
    /**
     *
     * @param e
     */
    public void addEpoch(Epoch e){
        data.add(e);
    }
    
    /**
     *
     * @param firectory
     * @return
     */
    public boolean saveToFile(File firectory){
        
        return false;
    }
}
