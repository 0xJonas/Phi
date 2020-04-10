package de.delphi.phi.data;

import de.delphi.phi.PhiAccessException;
import de.delphi.phi.PhiRuntimeException;
import de.delphi.phi.PhiStructureException;
import de.delphi.phi.PhiTypeException;
import de.delphi.phi.parser.PhiInternalException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Stack;

/**
 * Represents a collection.
 *
 * A PhiCollection can contain both unnamed and named members. Unnamed members are accessed by passing a
 * PhiInt to the get/set methods. Named members are accessed by passing a PhiSymbol to the get/set methods.
 * Other types are not valid.
 *
 * Before a member can be set and retrieved, it must be created with the createMember() method. This is
 * equivalent to writing
 * <code>
 *     var coll[a] = b
 * </code>
 * in Phi.
 */
public class PhiCollection extends PhiObject {

    /**
     * Initial capacity of the unnamed members array
     */
    private static final int INITIAL_CAPACITY = 16;

    /**
     * Map that contains all named members i.e members defined by a symbol instead of an index:
     * var coll.alpha = 25
     */
    protected HashMap<String, PhiObject> namedMembers;

    /**
     * Array that contains all unnamed members
     */
    private PhiObject[] unnamedMembers;

    /**
     * Number of unnamed members
     */
    private int length = 0;

    /**
     * Whether this collection has a 'super' member defined. Collections do not
     * have a 'super' member defined by default, because since 'super' itself needs to be a collection,
     * this would create infinite recursion. Instead, the creation of the 'super' member is delayed until it
     * is explicitly accessed.
     */
    private boolean hasSuperClassCollection = false;

    /**
     * Whether this collection is currently serving as the content of a 'super' member of some other collections.
     * If this value is non-zero, special checks have to be done in the setUnnamed() method, to prevent circular
     * inheritance. If this collection is added as a superclass collection of another collection, this number is
     * incremented. If it is overwritten, it is decremented.
     */
    private int isSuperClassCollectionOf;

    private PhiCollection parentScope;

    public PhiCollection(){
        this(null,false);
    }

    public PhiCollection(PhiCollection parentScope){
        this(parentScope, false);
    }

    private PhiCollection(boolean isSuperClassCollection){
        this(null, isSuperClassCollection);
    }

    private PhiCollection(PhiCollection parentScope, boolean isSuperClassCollection){
        this.parentScope = parentScope;
        isSuperClassCollectionOf = isSuperClassCollection ? 1:0;
        namedMembers = new HashMap<>();
        unnamedMembers = new PhiObject[INITIAL_CAPACITY];
    }

    @Override
    public Type getType() {
        return Type.COLLECTION;
    }

    @Override
    public String toString() {
        int numStrings = length + namedMembers.size();
        ArrayList<String> strings = new ArrayList<>(numStrings);
        for(int i = 0; i < length; i++) {
            strings.add(unnamedMembers[i].toString());
        }
        for(String key: namedMembers.keySet()){
            strings.add(key + " = " + namedMembers.get(key).toString());
        }
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for(int i = 0; i < numStrings; i++){
            if(i != 0)
                sb.append(", ");
            sb.append(strings.get(i));
        }
        sb.append(']');
        return sb.toString();
    }

    /**
     * Sets the capacity of the array containing the unnamed members.
     *
     * If the new capacity is less than the current capacity, the excess members will get discarded.
     * Note that this does not actually create new members, it just creates room in anticipation for
     * future calls to createMember().
     * @param capacity The new capacity.
     */
    public void setCapacity(int capacity){
        PhiObject[] newUnnamedMembers = new PhiObject[capacity];
        int copyLength = Math.min(unnamedMembers.length, capacity);
        System.arraycopy(unnamedMembers, 0, newUnnamedMembers, 0, copyLength);
        unnamedMembers = newUnnamedMembers;
    }

    /**
     * Creates a new member inside this PhiCollection.
     *
     * If key is a PhiInt, this method creates an unnamed member. If key is a PhiSymbol,
     * a named member is created. Other PhiObjects cause a PhiRuntimeException to be thrown.
     *
     * @param key the key of the new member.
     * @throws PhiAccessException If the index of an unnamed symbol is negative or a named symbol is reserved.
     * @throws PhiTypeException If key is neither a {@code PhiInt} or {@code PhiSymbol}.
     */
    public void createMember(PhiObject key) throws PhiAccessException, PhiTypeException {
        if(key.getType() == Type.INT){
            int index;
            try {
                index = (int) key.longValue();
            }catch(PhiRuntimeException e){
                throw new PhiInternalException(e);
            }

            if(index < 0)
                throw new PhiAccessException("Index must be positive.");
            if(index < length)  //Member already exists, do nothing
                return;

            //Increase capacity if necessary
            if(index >= unnamedMembers.length){
                int newCapacity = Math.max(index + 1, 2 * unnamedMembers.length);
                setCapacity(newCapacity);
            }
            //Initialize new members to PhiNull
            for(int i = length; i < index + 1; i++)
                unnamedMembers[i] = PhiNull.NULL;

            length = index + 1;
        }
        else if(key.getType() == Type.SYMBOL){
            String symbolName = key.toString();
            if(symbolName.equals("this") || symbolName.equals("length") || symbolName.equals("super"))
                throw new PhiAccessException("Cannot create reserved symbol " + symbolName);

            if(!namedMembers.containsKey(symbolName)){
                namedMembers.put(symbolName, PhiNull.NULL);
            }
        }
        else {
            throw new PhiTypeException("Key must be of type INT or SYMBOL");
        }
    }

    /**
     * Recursively access an unnamed element in the given collection. If {@code collection} contains the
     * requested element, it is returned. Otherwise, the superclasses of {@code collection} are searched
     * depth-first. If the requested element is not found, this method returns null.
     * @param collection The collection in which to look for the element.
     * @param index The index of the requested element.
     * @return The unnamed member at the given index or null if it is not found.
     */
    private PhiObject getUnnamedRecursive(PhiCollection collection, int index) {
        PhiObject result = null;

        if(index < collection.length)
            result = collection.unnamedMembers[index];

        if(result == null && collection.hasSuperClassCollection){
            try {
                PhiCollection superClasses = collection.getSuperClasses();
                long numSuperClasses = superClasses.getLength().longValue();
                for (int i = 0; i < numSuperClasses; i++) {
                    result = getUnnamedRecursive((PhiCollection) superClasses.getUnnamed(i), index);
                    if (result != null)
                        break;
                }
            }catch(PhiAccessException e){
                throw new PhiInternalException(e);
            }
        }

        return result;
    }

    /**
     * Retrieves an unnamed members from this collection.
     * @param index The index of the requested unnamed member.
     * @return The unnamed member at the given index.
     * @throws PhiAccessException If index is out of bounds.
     */
    @Override
    public PhiObject getUnnamed(int index) throws PhiAccessException{
        if(index < 0)
            throw new PhiAccessException("Index must be positive.");

        PhiObject result = getUnnamedRecursive(this, index);
        if(result == null)
            throw new PhiAccessException("Index " + index + " is out of bounds: length is " + length + ".");
        else
            return result;
    }

    /**
     * Recursively access an unnamed element in the given collection. If {@code collection} contains the
     * requested element, it is returned. Otherwise, the superclasses of {@code collection} are searched
     * depth-first. If the requested element is not found, this method returns null.
     * @param collection The collection in which to look for the element.
     * @param key The name of the named member.
     * @return The member with the given name or null if none is found.
     */
    private PhiObject getNamedRecursive(PhiCollection collection, String key){
        PhiObject result = collection.namedMembers.get(key);

        if(result == null && collection.hasSuperClassCollection){
            try {
                PhiCollection superClasses = collection.getSuperClasses();
                long numSuperClasses = superClasses.getLength().longValue();
                for (int i = 0; i < numSuperClasses; i++) {
                    result = getNamedRecursive((PhiCollection) superClasses.getUnnamed(i), key);
                    if (result != null)
                        break;
                }
            }catch(PhiAccessException e){
                throw new PhiInternalException(e);
            }
        }

        return result;
    }

    /**
     * Returns the list of superclasses for this collection. This method will create a new
     * superclass collection if there was none before.
     * @return The superclass collection for this collection.
     */
    public PhiCollection getSuperClasses(){
        PhiObject superClasses = namedMembers.get("super");

        // If this is a newly created collection, create a 'super' member.
        if(superClasses == null) {
            superClasses = new PhiCollection(true);
            namedMembers.put("super", superClasses);
            hasSuperClassCollection = true;
        }
        return (PhiCollection) superClasses;
    }

    /**
     * Returns the number of unnamed member that this PhiCollection can access. This is equals to the
     * maximum of the {@code length} fields in this PhiCollection and it's superclasses.
     * @return The number of accessible unnamed members.
     */
    public PhiInt getLength(){
        //Initialize with own length
        long maxLength = length;

        if(hasSuperClassCollection){
            try {
                //check superclasses
                PhiCollection superClasses = getSuperClasses();
                long numSuperClasses = superClasses.getLength().longValue();

                for (int i = 0; i < numSuperClasses; i++) {
                    PhiObject superClass = superClasses.getUnnamed(i);
                    //superclass list might contain NULL values, so check that we actually have a collection
                    if (superClass.getType() == Type.COLLECTION) {
                        long superLength = ((PhiCollection) superClass).getLength().longValue();
                        maxLength = Math.max(maxLength, superLength);
                    }
                }
            }catch(PhiAccessException e){
                throw new PhiInternalException(e);
            }
        }
        return new PhiInt(maxLength);
    }

    /**
     * Retrieves a named member from this PhiCollection.
     *
     * If there is no member with the given name, a {@code PhiRuntimeException} is thrown.
     *
     * PhiCollections include some special members that fulfill various functions.
     * The 'this' member evaluates to the collection itself and can be used to bind symbols to this collection.
     * This member is read-only. Attempting to write to it will throw a {@code PhiRuntimeException}.
     *
     * The 'length' member returns the number of unnamed members that this collection contains.
     * This member also is read-only.
     *
     * The 'super' member is used to specify a superclass for this collection. A collection can access all members of
     * all its superclasses as if they were contained in the derived collection. A newly created collection does not
     * explicitly create this member, because since the 'super' member has to be a collection itself, this would cause
     * an infinite recursion. Instead, the member is created when it is first accessed.
     * @param key The name of the requested member.
     * @return The member with the given name.
     * @throws PhiAccessException If the collection does not contain a member with the given name.
     */
    @Override
    public PhiObject getNamed(String key) throws PhiAccessException{
        //Retrieve 'this'
        if(key.equals("this"))
            return this;

        //Retrieve 'length'
        if(key.equals("length"))
            return getLength();

        //Retrieve 'super'
        if(key.equals("super"))
            return getSuperClasses();

        //Retrieve any other named member
        PhiObject result = getNamedRecursive(this, key);
        if(result == null) {
            if(parentScope != null)
                return parentScope.getNamed(key);
            else
                throw new PhiAccessException(key + " is not a member of this collection.");
        } else
            return result;
    }

    /**
     * Returns a set containing the names of all named members
     */
    public Set<String> memberNames(){
        return namedMembers.keySet();
    }

    /**
     * Validates that a given PhiObject can serve as the superclass list for this PhiCollection.
     *
     * This means that the PhiObject is a collection that contains only other collections or NULL values.
     * If the given PhiObject does not fulfill this requirement, a {@code PhiRuntimeException} is thrown.
     * @param obj The PhiObject to be checked.
     * @throws PhiStructureException If the given PhiObject is not a PhiCollection of PhiCollections.
     */
    private void validateTypes(PhiObject obj) throws PhiStructureException{
        if(obj.getType() != Type.COLLECTION)
            throw new PhiStructureException("Member super must be a collection of collections");

        PhiCollection superClasses = (PhiCollection) obj;
        long numSuperClasses = superClasses.getLength().longValue();
        for(int i = 0; i < numSuperClasses; i ++){
            try {
                Type type = superClasses.getUnnamed(i).getType();
                if (type != Type.COLLECTION && type != Type.NULL)
                    throw new PhiStructureException("Member super must be a collection of collections");
            }catch(PhiAccessException e){
                throw new PhiInternalException(e);
            }
        }
    }

    /**
     * Checks if the current collection contains a circular inheritance.
     *
     * This method recursively iterates depth-first over this collection and it's superclasses until
     * all collections have been visited or one collection has been encountered more than once.
     *
     * @param superClasses The current list of superclasses to iterate over
     * @param seenCollections A stack containing references to all collections that have been seen i.e. that
     *                        are derived from the current collection
     * @return true if circular inheritance has been detected (a collection has been encountered more than once),
     * false otherwise.
     */
    private boolean containsCycles(PhiCollection superClasses, Stack<PhiCollection> seenCollections){
        boolean result = false;
        try {
            long numSuperClasses = superClasses.getLength().longValue();
            for (int i = 0; i < numSuperClasses; i++) {
                PhiObject object = superClasses.getUnnamed(i);
                if (object.getType() == Type.NULL)
                    continue;
                PhiCollection superClass = (PhiCollection) object;

                //Circular inheritance has been detected
                if (seenCollections.contains(superClass))
                    return true;
                else if (superClass.hasSuperClassCollection) {
                    //Mark current collection as seen
                    seenCollections.push(superClass);

                    result = containsCycles(superClass.getSuperClasses(), seenCollections);
                    seenCollections.pop();
                    if (result)
                        break;
                }
            }
        }catch(PhiAccessException e){
            throw new PhiInternalException(e);
        }
        return result;
    }

    /**
     * Recursively set an unnamed member.
     *
     * This method first checks if an unnamed member with the given index exists in {@code collection}. If it does,
     * the member is set to the supplied value. Otherwise the superclasses of {@code collection} are checked in
     * depth-first order until an unnamed member with the corresponding index is found.
     *
     * If no collection contains the given index, false is returned and not set is performed.
     * @param collection The collection in which to search for the index.
     * @param index The index of the unnamed member to be set.
     * @param value The new value of the unnamed member.
     * @return true if the new value has been set somewhere in the collection hierarchy, false otherwise.
     */
    private boolean setUnnamedRecursive(PhiCollection collection, int index, PhiObject value){
        //Index has been found, perform the set
        if(index < collection.length){
            collection.unnamedMembers[index] = value;
            return true;
        }
        else if(collection.hasSuperClassCollection){
            try{
                //check superclasses
                PhiCollection superClasses = collection.getSuperClasses();
                long numSuperClasses = superClasses.getLength().longValue();
                boolean result = false;
                for(int i = 0; i < numSuperClasses; i++){
                    result = setUnnamedRecursive((PhiCollection) superClasses.getUnnamed(i), index, value);

                    //Set has been performed somewhere further up the hierarchy
                    if(result)
                        break;
                }
                return result;
            }catch(PhiAccessException e){
                throw new PhiInternalException(e);
            }
        }
        else
            return false;
    }

    /**
     * Sets an unnamed member in this collection.
     *
     * If this collection or any of it's superclasses contain a member at the given index, it is set to the
     * given value.
     * @param index The index of the unnamed member.
     * @param value The new value of the unnamed member.
     * @throws PhiAccessException If no unnamed member with the given index is found in the collection hierarchy.
     * @throws PhiStructureException If the set would create a circular inheritance in the underlying collection structure.
     */
    @Override
    public void setUnnamed(int index, PhiObject value) throws PhiAccessException, PhiStructureException{
        if (index < 0)
            throw new PhiAccessException("Index must be positive.");

        if (isSuperClassCollectionOf > 0 && value.getType() != Type.COLLECTION)
            throw new PhiStructureException("Member of a super class collection must be a collection.");

        PhiObject prevValue = getUnnamed(index);

        boolean success = setUnnamedRecursive(this, index, value);
        if (!success)
            throw new PhiAccessException("Index " + index + " is out of bounds: length is " + length + ".");

        //If this collection is a super class collection, do additional checks to prevent circular inheritance
        if (isSuperClassCollectionOf > 0) {
            boolean invalid = containsCycles(this, new Stack<>());
            if (invalid) {
                //Roll back the change
                setUnnamedRecursive(this, index, prevValue);
                throw new PhiStructureException("Collection can not be it's own super class.");
            }
        }
    }

    /**
     * Recursively set a named member.
     *
     * This method first checks if a member with the given name exists in {@code collection}. If it does,
     * the member is set to the supplied value. Otherwise the superclasses of {@code collection} are checked in
     * depth-first order until a member with the corresponding name is found.
     *
     * If no collection contains the given name, false is returned and not set is performed.
     * @param collection The collection to search for the name in.
     * @param key The name of the member to be set
     * @param value The new value of the named member.
     * @return true if a set was performed somewhere in the hierarchy, false otherwise
     */
    private boolean setNamedRecursive(PhiCollection collection, String key, PhiObject value){
        //Name was found, perform the set
        if(collection.namedMembers.containsKey(key)){
            collection.namedMembers.put(key, value);
            return true;
        }
        else if(collection.hasSuperClassCollection){
            try {
                //check superclasses
                PhiCollection superClasses = collection.getSuperClasses();
                long numSuperClasses = superClasses.getLength().longValue();
                boolean result = false;
                for (int i = 0; i < numSuperClasses; i++) {
                    result = setNamedRecursive((PhiCollection) superClasses.getUnnamed(i), key, value);

                    //Set has been performed somewhere further up the hierarchy
                    if (result)
                        break;
                }
                return result;
            }catch(PhiAccessException e){
                throw new PhiInternalException(e);
            }
        }else
            return false;
    }

    /**
     * Sets an named member in this collection.
     *
     * If this collection or any of it's superclasses contain a member with the given name, it is set to the
     * given value.
     *
     * PhiCollections contain some special named members that are handled differently from normal ones. These include
     * <ul>
     *     <li>{@code this}: A reference to the collection itself. This member is read-only. Attempting to set it
     *     will cause a PhiRuntimeException to be thrown.</li>
     *     <li>{@code length}: The number of unnamed members that this collection can access. This includes unnamed
     *     members of superclasses. Also read-only.</li>
     *     <li>{@code super}: The list of superclasses. This member must always be a collection of collections.
     *     This method also ensures that no circular inheritance is created i.e. that a collection can not
     *     be it's own superclass, directly or indirectly. If any of these rules is violated, a PhiRuntimeException
     *     will be thrown.</li>
     * </ul>
     * @param key The name of the member.
     * @param value The new value of the named member.
     * @throws PhiAccessException If no named member with the given name is found in the collection hierarchy, if
     * a set to a read-only member was attempted or if bad value for the {@code super} member was set.
     * @throws PhiStructureException If the set would create a circular inheritance in the underlying collection structure.
     */
    @Override
    public void setNamed(String key, PhiObject value) throws PhiAccessException, PhiStructureException{
        if(key.equals("length") || key.equals("this"))
            throw new PhiAccessException("Collection member is read-only.");

        if(key.equals("super")){
            validateTypes(value);

            //Back up current state in case we have to roll it back later
            PhiCollection prevValue = null;
            boolean prevHasSuperClassCollection = hasSuperClassCollection;
            if(hasSuperClassCollection){
                prevValue = getSuperClasses();
            }

            //Perform the set
            ((PhiCollection) value).isSuperClassCollectionOf++;
            namedMembers.put("super", value);
            hasSuperClassCollection = true;

            //Check if a circular inheritance was created and roll back the previous state if it was.
            boolean invalid = containsCycles((PhiCollection) value, new Stack<>());
            if(invalid){
                if(prevValue == null)
                    namedMembers.remove("super");
                else
                    namedMembers.put("super", prevValue);
                hasSuperClassCollection = prevHasSuperClassCollection;
                ((PhiCollection) value).isSuperClassCollectionOf--;
                throw new PhiStructureException("Collection can not be it's own super class");
            }else if(prevValue != null){
                prevValue.isSuperClassCollectionOf--;
            }
        }
        else {
            boolean success = setNamedRecursive(this, key, value);
            if(!success) {
                if(parentScope != null)
                    parentScope.setNamed(key, value);
                else
                    throw new PhiAccessException("Member " + key + " does not exist.");
            }
        }
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        PhiCollection cloned = (PhiCollection) super.clone();

        cloned.unnamedMembers = new PhiObject[this.unnamedMembers.length];
        for(int i = 0; i < length; i++){
            cloned.unnamedMembers[i] = (PhiObject) this.unnamedMembers[i].clone();
        }

        cloned.namedMembers = new HashMap<>();
        for(String key: namedMembers.keySet()){
            cloned.namedMembers.put(key, (PhiObject) namedMembers.get(key).clone());
        }

        return cloned;
    }
}
