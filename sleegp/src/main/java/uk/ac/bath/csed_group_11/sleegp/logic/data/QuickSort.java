package uk.ac.bath.csed_group_11.sleegp.logic.data;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Creates a new sorter for a certain type of variable.
 * Sorts Dates, Integers, Floats etc. Must implement Comparable interface
 *
 * @param <t> the type of object to be compared (has to implement Comparable)
 */
public class QuickSort <t extends Comparable<t>>{
    private List<t> toSort;

    public QuickSort(List<t> toSort){
        this.toSort = toSort;
    }

    public List<t> quickSort(){
        quickSort(toSort, 0, toSort.size()-1);
        return toSort;
    }

    private void quickSort(List<t> nums, int start, int pivot) {

        int partition = partition(nums, start, pivot);

        if (partition - 1 > start) {
            quickSort(nums, start, partition - 1);
        }
        if (partition + 1 < pivot) {
            quickSort(nums, partition + 1, pivot);
        }
    }

    private int partition(List<t> nums, int start, int pivot) {
        t pivotNum = nums.get(pivot);

        for (int i = start; i < pivot; i++) {

            if (nums.get(i).compareTo(pivotNum) <0) {
                t temp = nums.get(start);
                nums.set(start, nums.get(i));
                nums.set(i, temp);
                start++;
            }
        }

        t temp = nums.get(start);
        nums.set(start, pivotNum);
        nums.set(pivot, temp);

        return start;
    }


    /**
     * Example implementation of a quick sort
     * @param args
     */
    public static void main(String[] args) {

        //Example implementation
        //Note that object has to implement comparable
        List<Date> list = Arrays.asList(new Date(2323223232L), new Date(323223232L) ,new Date(232323232L), new Date(2357823232L));
        System.out.println(list.toString());
        QuickSort<Date> sorter = new QuickSort<>(list);

        System.out.println(sorter.quickSort().toString());
    }
}
