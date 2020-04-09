package de.delphi.phi.data;

import de.delphi.phi.PhiException;
import de.delphi.phi.PhiScope;

import java.util.List;

public class ParameterList {

    private String[] names;

    private PhiScope defaultValues;

    private boolean[] availableValues;

    public ParameterList(List<String> names, PhiScope defaultValues){
        this.names = names.toArray(new String[0]);
        this.defaultValues = defaultValues;

        availableValues = new boolean[this.names.length];
        for(String name: defaultValues.memberNames()){
            availableValues[indexOf(name)] = true;
        }
    }

    public PhiScope getDefaultValues(){
        try {
            return (PhiScope) defaultValues.clone();
        }catch(CloneNotSupportedException e){
            e.printStackTrace();
            return null;    //should never happen
        }
    }

    public int indexOf(String name){
        for(int i = 0; i < names.length; i++){
            if(names[i].equals(name))
                return i;
        }
        return -1;
    }

    public PhiScope supplyParameters(PhiCollection params) throws PhiException {
        if(params.getLength().longValue() > names.length)
            throw new PhiException("Too many parameters.");

        boolean[] valueSupplied = availableValues.clone();
        PhiScope values = getDefaultValues();

        //Set with unnamed values
        int numSuppliedParams = (int) params.getLength().longValue();
        for(int i = 0; i < names.length; i++){
            values.createMember(new PhiSymbol(names[i]));
            if(i < numSuppliedParams) {
                values.setNamed(names[i], params.getUnnamed(i));
                valueSupplied[i] = true;
            }
        }

        //Set with named values
        for(String name: params.memberNames()){
            int index = indexOf(name);
            if(index >= 0) {
                values.setNamed(name, params.getNamed(name));
                valueSupplied[index] = true;
            }else
                throw new PhiException("Parameter " + name + " does not exist.");
        }

        for(int i = 0; i < names.length; i++){
            if(!valueSupplied[i])
                throw new PhiException("Missing value for parameter " + names[i]);
        }

        return values;
    }
}
