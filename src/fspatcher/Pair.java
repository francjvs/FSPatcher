/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fspatcher;

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
}
