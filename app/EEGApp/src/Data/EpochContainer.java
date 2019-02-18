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
    
    
    public EpochContainer(){
        data = new ArrayList<Epoch>();
    }
    
    public EpochContainer(File file){
        
    }
    
    public int size(){
        return data.size();
    }
    
    public Epoch getEpoch(int id){
        return data.get(id);
    }
    
    public void addEpoch(Epoch e){
        data.add(e);
    }
    
    public boolean saveToFile(File firectory){
        
        return false;
    }
}
