package de.delphi.phi.data;

import de.delphi.phi.PhiArgumentException;
import de.delphi.phi.PhiRuntimeException;
import de.delphi.phi.PhiScope;
import de.delphi.phi.parser.PhiInternalException;

import java.util.List;

/**
 * Represents the list of parameters that a PhiFunction accepts.
 */
public class ParameterList {

    /**
     * Names of the parameters
     */
    private String[] names;

    /**
     * Default values of the parameters, if provided
     */
    private PhiScope defaultValues;

    /**
     * stores which names have default values associated with them. Used to check if a function call has a value for
     * every parameter.
     */
    private boolean[] availableValues;

    public ParameterList(List<String> names, PhiScope defaultValues){
        this.names = names.toArray(new String[0]);
        this.defaultValues = defaultValues;

        availableValues = new boolean[this.names.length];
        for(String name: defaultValues.memberNames()){
            availableValues[indexOf(name)] = true;
        }
    }

    /**
     * Returns a copy of the default values. This can be used as the scope of the function call.
     */
    public PhiScope getDefaultValues(){
        try {
            return (PhiScope) defaultValues.clone();
        }catch(CloneNotSupportedException e){
            throw new PhiInternalException(e);
        }
    }

    /**
     * Returns the index of the given name in the names array, or -1 if it is not found.
     * @param name The name to get the index for.
     */
    private int indexOf(String name){
        for(int i = 0; i < names.length; i++){
            if(names[i].equals(name))
                return i;
        }
        return -1;
    }

    /**
     * Creates a scope from the names in this ParameterList and the values given as an argument.
     *
     * The scope is set up in the following way: Firstly the unnamed members of the collection are put into the
     * scope in order. Secondly the named members of the collection are added to the scope. This may override values
     * already set with unnamed parameters.
     *
     * @param params A collection containing values for parameters.
     * @return A PhiScope in which the parameter names are associated with the given values.
     * @throws PhiRuntimeException If too few, too many or wrong parameters are supplied.
     */
    public PhiScope supplyParameters(PhiCollection params) throws PhiRuntimeException {
        if(params.getLength().longValue() > names.length)
            throw new PhiArgumentException("Too many parameters.");

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
                throw new PhiArgumentException("Parameter " + name + " does not exist.");
        }

        for(int i = 0; i < names.length; i++){
            if(!valueSupplied[i])
                throw new PhiArgumentException("Missing value for parameter " + names[i]);
        }

        return values;
    }
}
