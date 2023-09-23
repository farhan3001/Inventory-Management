package com.example.qtitest.data;

import java.io.Serializable;
import java.util.ArrayList;

public class LocationResponseHome implements Serializable {
    ArrayList<ResultLocation> results;

    public ArrayList<ResultLocation> getResult() {
        return results;
    }

    public void setResult(ArrayList<ResultLocation> results) {
        this.results = results;
    }

    public static class ResultLocation implements Serializable {
        Location location;

        int count;

        public Location getLocation() {
            return location;
        }

        public void setLocation(Location location) {
            this.location = location;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public static class Location implements Serializable {
            String id;
            String name;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }

    }
}
