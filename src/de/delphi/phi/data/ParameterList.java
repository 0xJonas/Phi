package de.delphi.phi.data;

import java.util.ArrayList;

public class ParameterList {

    private ArrayList<String> names;

    private PhiCollection defaultValues;

    public ParameterList(){
        names = new ArrayList<>();
        defaultValues = new PhiCollection();
    }

    public void addParameter(String name){
        names.add(name);
    }

    public void addParameter(String name, PhiObject defaultValue){
        names.add(name);
        defaultValues.createMember(new PhiSymbol(name));
        defaultValues.setNamed(name, defaultValue);
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
        return names.size();
    }

    public boolean contains(String name){
        return names.contains(name);
    }

    public String getName(int index){
        return names.get(index);
    }
}
