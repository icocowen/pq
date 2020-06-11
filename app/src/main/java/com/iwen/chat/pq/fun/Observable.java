package com.iwen.chat.pq.fun;

import android.util.Log;

import java.util.LinkedList;

public class Observable {

    private LinkedList<Observer> obs;
    private Object lock = new Object();



    public Observable() {
        obs = new LinkedList<>();
    }

    public void addObserver(Observer o) {
        synchronized (lock) {
            if (o == null)
                throw new NullPointerException();
            if (!obs.contains(o)) {
                obs.add(o);
            }
        }

    }

    public synchronized void deleteObserver(Observer o) {
        obs.remove(o);
    }

    public void notifyObservers() {
        notifyObservers(null);
    }

    public void notifyObservers(Object arg) {
        synchronized (lock) {
          obs.forEach(o -> {
              Log.e("通知类：", o.getClass().toString());
              o.update(this, arg);
          });
        }

    }
    public synchronized void deleteObservers() {
        obs.clear();
    }

    public synchronized int countObservers() {
        return obs.size();
    }

}
