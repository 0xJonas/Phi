package de.delphi.phi.data;

import java.util.List;

public class ParameterList {

    private String[] names;

    private PhiCollection defaultValues;

    public ParameterList(List<String> names, PhiCollection defaultValues){
        this.names = names.toArray(new String[0]);
        this.defaultValues = defaultValues;
    }

    public PhiCollection getDefaultValues(){
        try {
            return (PhiCollection) defaultValues.clone();
        }catch(CloneNotSupportedException e){
            e.printStackTrace();
            return null;
        }
    }

    public int getParameterCount(){
        return names.length;
    }

    public boolean contains(String name){
        for(String s: names){
            if(s.equals(name))
                return true;
        }
        return false;
    }

    public String getName(int index){
        return names[index];
    }
}
