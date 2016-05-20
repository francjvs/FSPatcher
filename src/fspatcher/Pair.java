/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fspatcher;

import java.util.ArrayList;

/**
 *
 * @author David Tynan
 * @param <L>
 * @param <R>
 */
public class Pair<L, R> {

    private L l;
    private R r;

    public Pair(L l, R r) {
        this.l = l;
        this.r = r;
    }

    public L getBase() {
        return l;
    }

    public R getVar() {
        return r;
    }

    public void setBase(L l) {
        this.l = l;
    }

    public void setVar(R r) {
        this.r = r;
    }
    
    public boolean baseContains(ArrayList<Pair<L, R>> list, L l1) {
        boolean found = false;
        
        for (Pair<L, R> p : list) {
            if (p.getBase().equals(l1)) {
                found = true;
                break;
            }
        }
        
        return found;
    }
    
    public ArrayList<Pair<L,Integer>> reorderPairArray(ArrayList<Pair<L,Integer>> list) {
        ArrayList<Pair<L,Integer>> ret = new ArrayList<>(0);
        ret.add(list.get(0));
        for (int i=1; i<list.size(); i++) {
            for (int j=0; j< ret.size(); j++) {
                if (j==ret.size()-1) { //last position, simply add to list
                    ret.add(list.get(i));
                    break;
                } else if (list.get(i).r < ret.get(j).r) { //if element in new list is bigger than element from old list add there
                    ret.add(j, list.get(i));
                    break;
                }
            }
        }
        
        return ret;
    }
            
            
}
