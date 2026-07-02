package com.example.constant;

public class BaseContext {
    private static final ThreadLocal<Integer> threadLocal=new ThreadLocal<>();

    public static void set(Integer id){
        threadLocal.set(id);
    }
    public static Integer get(){
        return threadLocal.get();
    }
    public static void remove(){
        threadLocal.remove();
    }
}
