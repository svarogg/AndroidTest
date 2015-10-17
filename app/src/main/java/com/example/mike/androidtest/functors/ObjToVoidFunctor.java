package com.example.mike.androidtest.functors;

/**
 * Created by Mike on 17/10/2015.
 */
public interface ObjToVoidFunctor<ArgType> {
    public void execute(ArgType arg);
}
